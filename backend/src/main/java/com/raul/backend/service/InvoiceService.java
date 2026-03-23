package com.raul.backend.service;

import com.raul.backend.dto.invoice.*;
import com.raul.backend.entity.*;
import com.raul.backend.repository.ContractRepository;
import com.raul.backend.repository.InvoiceLineRepository;
import com.raul.backend.repository.InvoiceRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class InvoiceService {

    private final InvoiceRepository repository;
    private final ContractRepository contractRepository;
    private final InvoiceLineRepository invoiceLineRepository;

    public InvoiceService(InvoiceRepository repository, ContractRepository contractRepository, InvoiceLineRepository invoiceLineRepository) {
        this.repository = repository;
        this.contractRepository = contractRepository;
        this.invoiceLineRepository = invoiceLineRepository;
    }

    // CREATE
    @Transactional
    public InvoiceResponseDTO create(InvoiceCreateDTO dto) {

        Contract contract = contractRepository.findById(dto.getContractId())
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado"));

        Invoice invoice = new Invoice();

        invoice.setStatus(dto.getStatus());
        invoice.setIssueDate(dto.getIssueDate());
        invoice.setDueDay(dto.getDueDay());
        invoice.setLateFreeAmount(dto.getLateFreeAmount());
        invoice.setInterestAmount(dto.getInterestAmount());
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
        if (dto.getDueDay() != null) invoice.setDueDay(dto.getDueDay());
        if (dto.getLateFreeAmount() != null) invoice.setLateFreeAmount(dto.getLateFreeAmount());
        if (dto.getInterestAmount() != null) invoice.setInterestAmount(dto.getInterestAmount());

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
        return repository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Invoice não encontrada"));
    }

    // DELETE
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // MAPPER
    private InvoiceResponseDTO toDTO(Invoice invoice) {
        return new InvoiceResponseDTO(
                invoice.getId(),
                invoice.getStatus(),
                invoice.getIssueDate(),
                invoice.getDueDay(),
                invoice.getAmount(),
                invoice.getLateFreeAmount(),
                invoice.getInterestAmount(),
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