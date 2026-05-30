package com.raul.backend.service;

import com.raul.backend.dto.invoice.*;
import com.raul.backend.entity.*;
import com.raul.backend.enums.AuditAction;
import com.raul.backend.enums.InvoiceStatus;
import com.raul.backend.enums.PaymentStatus;
import com.raul.backend.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class InvoiceService {

    private final InvoiceRepository repository;
    private final ContractRepository contractRepository;
    private final ClientRepository clientRepository;
    private final InvoiceCalculatorService invoiceCalculatorService;
    private final AuditLogService auditLogService;
    private final UserRepository userRepository;

    // CREATE
    @Transactional
    public InvoiceResponseDTO create(InvoiceCreateDTO dto) {

        Contract contract = contractRepository.findById(dto.getContractId())
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado"));

        // impede fatura duplicada para o mesmo contrato e período
        LocalDate periodStart = dto.getIssueDate().withDayOfMonth(1);
        LocalDate periodEnd = periodStart.plusMonths(1).minusDays(1);

        boolean alreadyExists = repository.existsByContractIdAndIssueDateBetweenAndStatusNot(
                contract.getId(), periodStart, periodEnd, InvoiceStatus.CANCELLED
        );

        if (alreadyExists) {
            throw new RuntimeException(
                    "Já existe uma fatura gerada para este contrato no período informado (mês " +
                            dto.getIssueDate().getMonthValue() + "/" + dto.getIssueDate().getYear() + ")"
            );
        }

        Invoice invoice = new Invoice();

        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setIssueDate(dto.getIssueDate());
        invoice.setDueDate(dto.getDueDate());
        invoice.setLateFreeAmount(BigDecimal.ZERO);
        invoice.setInterestAmount(BigDecimal.ZERO);
        invoice.setContract(contract);

        BigDecimal totalInvoice = BigDecimal.ZERO;

        for (ContractItem item : contract.getItems()) {

            InvoiceLine line = new InvoiceLine();
            line.setDescription(item.getName());
            line.setQuantity(item.getQuantity());
            line.setUnitPrice(item.getUnitPrice());

            BigDecimal total = item.getQuantity().multiply(item.getUnitPrice());
            line.setTotal(total);

            line.setInvoice(invoice);
            line.setContractItem(item);
            invoice.getInvoiceLines().add(line);

            totalInvoice = totalInvoice.add(total);
        }

        invoice.setAmount(totalInvoice);
        invoice.setOriginalAmount(totalInvoice);

        invoice = repository.save(invoice);

        User loggedUser = getLoggedUser();

        auditLogService.log(invoice.getId(), "INVOICE", AuditAction.INVOICE_CREATED,
                loggedUser != null ? loggedUser.getId() : null,
                loggedUser != null ? loggedUser.getName() : null,
                "Fatura gerada para contrato #" + contract.getId());

        return toDTO(invoice);
    }

    // UPDATE
    @Transactional
    public InvoiceResponseDTO update(Long id, InvoiceUpdateDTO dto) {

        Invoice invoice = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice não encontrada"));

        if (dto.getStatus() != null) invoice.setStatus(dto.getStatus());
        if (dto.getIssueDate() != null) invoice.setIssueDate(dto.getIssueDate());
        if (dto.getDueDate() != null) invoice.setDueDate(dto.getDueDate());
        if (dto.getContractId() != null) {
            Contract contract = contractRepository.findById(dto.getContractId())
                    .orElseThrow(() -> new RuntimeException("Contrato não encontrado"));
            invoice.setContract(contract);
        }

        return toDTO(repository.save(invoice));
    }

    // GET ALL
    public Page<InvoiceResponseDTO> findAll(Pageable pageable, String search) {
        if (search != null && !search.isBlank()) {
            try {
                Long contractId = Long.parseLong(search);
                return repository.findByContractIdAndDeletedAtIsNull(contractId, pageable).map(this::toDTO);
            } catch (NumberFormatException ignored) {}

            try {
                InvoiceStatus status = InvoiceStatus.valueOf(search.toUpperCase());
                return repository.findByStatusAndDeletedAtIsNull(status, pageable).map(this::toDTO);
            } catch (IllegalArgumentException ignored) {}
        }
        return repository.findByDeletedAtIsNull(pageable).map(this::toDTO);
    }

    // GET BY ID
    public InvoiceResponseDTO findById(Long id) {
        Invoice invoice = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice não encontrada"));

        return toDTO(invoice);
    }

    // soft delete — substitui o deleteById direto
    @Transactional
    public void delete(Long id) {
        Invoice invoice = repository.findByIdWithPayments(id)
                .orElseThrow(() -> new RuntimeException("Invoice não encontrada"));

        boolean hasActivePayments = invoice.getPayment() != null &&
                invoice.getPayment().stream()
                        .anyMatch(p -> p.getPaymentStatus() != PaymentStatus.REFUNDED
                                && p.getPaymentStatus() != PaymentStatus.CANCELLED);

        if (hasActivePayments) {
            throw new RuntimeException(
                    "Não é possível excluir uma fatura com pagamentos registrados. " +
                            "Cancele os pagamentos antes de excluir a fatura."
            );
        }

        invoice.setStatus(InvoiceStatus.CANCELLED);
        invoice.setDeletedAt(LocalDateTime.now());

        repository.save(invoice);

        User loggedUser = getLoggedUser();

        auditLogService.log(invoice.getId(), "INVOICE", AuditAction.INVOICE_CANCELLED,
                loggedUser != null ? loggedUser.getId() : null,
                loggedUser != null ? loggedUser.getName() : null,
                "Fatura cancelada manualmente");
    }

    public void identifyDefaulters() {

        List<Invoice> overdueInvoices =
                repository.findByDueDateBeforeAndStatusNot(
                        LocalDate.now(),
                        InvoiceStatus.PAID
                );

        Set<Client> defaulters = new HashSet<>();

        for (Invoice invoice : overdueInvoices) {
            Client client = invoice.getContract().getClient();
            defaulters.add(client);
        }

        defaulters.forEach(client -> client.setDefaulter(true));
        clientRepository.saveAll(defaulters);
    }

    @Transactional
    public void cancelByContract(List<Invoice> invoices) {
        for (Invoice invoice : invoices) {
            if (invoice.getStatus() == InvoiceStatus.CANCELLED) continue;

            boolean hasActivePayments = invoice.getPayment() != null &&
                    invoice.getPayment().stream()
                            .anyMatch(p -> p.getPaymentStatus() != PaymentStatus.REFUNDED
                                    && p.getPaymentStatus() != PaymentStatus.CANCELLED);

            if (hasActivePayments) {
                throw new RuntimeException(
                        "Fatura #" + invoice.getId() + " possui pagamentos registrados e não pode ser cancelada. " +
                                "Cancele os pagamentos antes de cancelar o contrato."
                );
            }

            invoice.setStatus(InvoiceStatus.CANCELLED);
            repository.save(invoice);
        }
    }

    private User getLoggedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        }
        return null;
    }

    // MAPPER
    private InvoiceResponseDTO toDTO(Invoice invoice) {

        BigDecimal paidAmount = invoiceCalculatorService.getTotalPaid(invoice);
        BigDecimal remainingAmount = invoiceCalculatorService.getRemainingAmountCapped(invoice);
        BigDecimal overpaidAmount = invoiceCalculatorService.getOverpaidAmount(invoice);

        return new InvoiceResponseDTO(
                invoice.getId(),
                invoice.getStatus(),
                invoice.getIssueDate(),
                invoice.getDueDate(),
                invoice.getAmount(),
                paidAmount,
                overpaidAmount,
                remainingAmount,
                invoice.getCreatedAt(),
                invoice.getUpdatedAt(),
                invoice.getContract() != null ? invoice.getContract().getId() : null,
                invoice.getPayment() != null
                        ? invoice.getPayment().stream().map(Payment::getId).toList()
                        : List.of(),
                invoice.getInvoiceLines() != null
                        ? invoice.getInvoiceLines().stream().map(InvoiceLine::getId).toList()
                        : List.of()
        );
    }
}