package com.raul.backend.service;

import com.raul.backend.dto.invoice.*;
import com.raul.backend.entity.*;
import com.raul.backend.enums.InvoiceStatus;
import com.raul.backend.repository.ClientRepository;
import com.raul.backend.repository.ContractRepository;
import com.raul.backend.repository.InvoiceLineRepository;
import com.raul.backend.repository.InvoiceRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class InvoiceService {

    private final InvoiceRepository repository;
    private final ContractRepository contractRepository;
    private final InvoiceLineRepository invoiceLineRepository;
    private final ClientRepository clientRepository;
    private final FinancialParameterService financialParameterService;
    private final InvoiceCalculatorService invoiceCalculatorService;

    public InvoiceService(InvoiceRepository repository,
                          ContractRepository contractRepository,
                          InvoiceLineRepository invoiceLineRepository,
                          ClientRepository clientRepository,
                          FinancialParameterService financialParameterService,
                          InvoiceCalculatorService invoiceCalculatorService) {
        this.repository = repository;
        this.contractRepository = contractRepository;
        this.invoiceLineRepository = invoiceLineRepository;
        this.clientRepository = clientRepository;
        this.financialParameterService = financialParameterService;
        this.invoiceCalculatorService = invoiceCalculatorService;
    }

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
    public List<InvoiceResponseDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
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
        Invoice invoice = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice não encontrada"));

        boolean hasPayments = invoice.getPayment() != null && !invoice.getPayment().isEmpty();
        if (hasPayments) {
            throw new RuntimeException(
                    "Não é possível excluir uma fatura com pagamentos registrados. " +
                            "Cancele os pagamentos antes de excluir a fatura."
            );
        }

        invoice.setStatus(InvoiceStatus.CANCELLED);
        invoice.setDeletedAt(LocalDateTime.now());
        repository.save(invoice);
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