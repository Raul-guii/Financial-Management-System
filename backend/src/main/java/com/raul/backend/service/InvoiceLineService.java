package com.raul.backend.service;

import com.raul.backend.dto.invoiceline.InvoiceLineCreateDTO;
import com.raul.backend.dto.invoiceline.InvoiceLineResponseDTO;
import com.raul.backend.entity.ContractItem;
import com.raul.backend.entity.Invoice;
import com.raul.backend.entity.InvoiceLine;
import com.raul.backend.repository.ContractItemRepository;
import com.raul.backend.repository.InvoiceLineRepository;
import com.raul.backend.repository.InvoiceRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class InvoiceLineService {

    private final InvoiceLineRepository repository;
    private final InvoiceRepository invoiceRepository;
    private final ContractItemRepository contractItemRepository;

    public InvoiceLineService(InvoiceLineRepository repository,
                              InvoiceRepository invoiceRepository,
                              ContractItemRepository contractItemRepository) {
        this.repository = repository;
        this.invoiceRepository = invoiceRepository;
        this.contractItemRepository = contractItemRepository;
    }

    // GET BY ID
    public InvoiceLineResponseDTO findById(Long id) {
        return repository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("InvoiceLine não encontrada"));
    }

    // LIST BY INVOICE
    public List<InvoiceLineResponseDTO> findByInvoice(Long invoiceId) {
        return repository.findByInvoiceId(invoiceId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private void recalculateInvoiceAmount(Invoice invoice) {

        List<InvoiceLine> lines = repository.findByInvoiceId(invoice.getId());

        BigDecimal total = lines.stream()
                .map(InvoiceLine::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        invoice.setAmount(total);

        invoiceRepository.save(invoice);
    }

    // MAPPER
    private InvoiceLineResponseDTO toDTO(InvoiceLine line) {
        return new InvoiceLineResponseDTO(
                line.getId(),
                line.getDescription(),
                line.getQuantity(),
                line.getUnitPrice(),
                line.getTotal(),
                line.getCreatedAt(),
                line.getContractItem() != null ? line.getContractItem().getId() : null,
                line.getInvoice() != null ? line.getInvoice().getId() : null
        );
    }
}