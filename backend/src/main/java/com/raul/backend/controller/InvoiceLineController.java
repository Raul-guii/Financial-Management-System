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

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceLineResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    // LISTAR POR INVOICE
    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<List<InvoiceLineResponseDTO>> findByInvoice(
            @PathVariable Long invoiceId
    ) {
        return ResponseEntity.ok(service.findByInvoice(invoiceId));
    }
}