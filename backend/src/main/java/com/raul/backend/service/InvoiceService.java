package com.raul.backend.service;

import ch.qos.logback.core.status.Status;
import com.raul.backend.dto.invoice.*;
import com.raul.backend.entity.*;
import com.raul.backend.enums.InvoiceStatus;
import com.raul.backend.enums.PaymentStatus;
import com.raul.backend.repository.ClientRepository;
import com.raul.backend.repository.ContractRepository;
import com.raul.backend.repository.InvoiceLineRepository;
import com.raul.backend.repository.InvoiceRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class InvoiceService {

    private final InvoiceRepository repository;
    private final ContractRepository contractRepository;
    private final InvoiceLineRepository invoiceLineRepository;
    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;
    private final FinancialParameterService financialParameterService;

    public InvoiceService(InvoiceRepository repository, ContractRepository contractRepository, InvoiceLineRepository invoiceLineRepository, InvoiceRepository invoiceRepository, ClientRepository clientRepository, FinancialParameterService financialParameterService) {
        this.repository = repository;
        this.contractRepository = contractRepository;
        this.invoiceLineRepository = invoiceLineRepository;
        this.invoiceRepository = invoiceRepository;
        this.clientRepository = clientRepository;
        this.financialParameterService = financialParameterService;
    }

    // CREATE
    @Transactional
    public InvoiceResponseDTO create(InvoiceCreateDTO dto) {

        Contract contract = contractRepository.findById(dto.getContractId())
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado"));

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

    private BigDecimal calculateFinalAmount(Invoice invoice) {

        LocalDate today = LocalDate.now();

        if (!today.isAfter(invoice.getDueDate())) {
            return invoice.getOriginalAmount();
        }

        BigDecimal lateFeePercent = financialParameterService.getActiveValueByName("LATE_FEE");
        BigDecimal dailyInterest = financialParameterService.getActiveValueByName("DAILY_INTEREST");
        int graceDays = financialParameterService.getActiveValueByName("GRACE_PERIOD_DAYS").intValue();

        LocalDate dueWithGrace = invoice.getDueDate().plusDays(graceDays);

        if (!today.isAfter(dueWithGrace)) {
            return invoice.getOriginalAmount();
        }

        long daysLate = java.time.temporal.ChronoUnit.DAYS.between(dueWithGrace, today);

        BigDecimal multa = invoice.getOriginalAmount()
                .multiply(lateFeePercent)
                .divide(BigDecimal.valueOf(100));

        BigDecimal juros = invoice.getOriginalAmount()
                .multiply(dailyInterest)
                .multiply(BigDecimal.valueOf(daysLate));

        return invoice.getOriginalAmount()
                .add(multa)
                .add(juros);
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

    // DELETE
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public void identifyDefaulters() {

        List<Invoice> overdueInvoices =
                invoiceRepository.findByDueDateBeforeAndStatusNot(
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

    private BigDecimal calculateRemainingAmount(Invoice invoice) {

        if (invoice.getPayment() == null || invoice.getPayment().isEmpty()) {
            return invoice.getAmount();
        }

        BigDecimal totalPaid = invoice.getPayment()
                .stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.APPROVED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return invoice.getAmount().subtract(totalPaid);
    }

    // MAPPER
    private InvoiceResponseDTO toDTO(Invoice invoice) {

        BigDecimal remainingAmount = calculateRemainingAmount(invoice);

        return new InvoiceResponseDTO(
                invoice.getId(),
                invoice.getStatus(),
                invoice.getIssueDate(),
                invoice.getDueDate(),
                invoice.getAmount(),
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