package com.raul.backend.controller;

import com.raul.backend.dto.invoiceline.InvoiceLineCreateDTO;
import com.raul.backend.dto.invoiceline.InvoiceLineResponseDTO;
import com.raul.backend.service.InvoiceLineService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invoice-lines")
public class InvoiceLineController {

    private final InvoiceLineService service;

    public InvoiceLineController(InvoiceLineService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_ANALYST','FINANCIAL_MANAGER')")
    public ResponseEntity<InvoiceLineResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/invoice/{invoiceId}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_ANALYST','FINANCIAL_MANAGER')")
    public ResponseEntity<List<InvoiceLineResponseDTO>> findByInvoice(
            @PathVariable Long invoiceId
    ) {
        return ResponseEntity.ok(service.findByInvoice(invoiceId));
    }
}