package com.raul.backend.controller;

import com.raul.backend.dto.payment.*;
import com.raul.backend.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_ANALYST','FINANCIAL_MANAGER')")
    public ResponseEntity<PaymentResponseDTO> create(@RequestBody @Valid PaymentCreateDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_ANALYST','FINANCIAL_MANAGER')")
    public ResponseEntity<PaymentResponseDTO> update(@PathVariable Long id,
                                                     @RequestBody @Valid PaymentUpdateDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_ANALYST','FINANCIAL_MANAGER')")
    public ResponseEntity<List<PaymentResponseDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_ANALYST','FINANCIAL_MANAGER')")
    public ResponseEntity<PaymentResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}