package com.raul.backend.controller;

import com.raul.backend.dto.invoice.*;
import com.raul.backend.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService service;

    public InvoiceController(InvoiceService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_ANALYST','FINANCIAL_MANAGER')")
    public ResponseEntity<InvoiceResponseDTO> create(@RequestBody @Valid InvoiceCreateDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_ANALYST','FINANCIAL_MANAGER')")
    public ResponseEntity<InvoiceResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid InvoiceUpdateDTO dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_ANALYST','FINANCIAL_MANAGER')")
    @GetMapping
    public ResponseEntity<List<InvoiceResponseDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_ANALYST','FINANCIAL_MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_MANAGER')")
    @PostMapping("/check-defaulters")
    public ResponseEntity<Void> checkDefaulters() {
        service.identifyDefaulters();
        return ResponseEntity.ok().build();
    }
}