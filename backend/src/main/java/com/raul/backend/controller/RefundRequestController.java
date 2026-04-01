package com.raul.backend.controller;

import com.raul.backend.dto.refundrequest.RefundRequestCreateDTO;
import com.raul.backend.dto.refundrequest.RefundRequestResponseDTO;
import com.raul.backend.service.RefundRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/refund-requests")
public class RefundRequestController {

    private final RefundRequestService refundRequestService;

    public RefundRequestController(RefundRequestService refundRequestService) {
        this.refundRequestService = refundRequestService;
    }

    @PreAuthorize("hasAnyRole('FINANCIAL_ANALYST', 'FINANCIAL_MANAGER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<RefundRequestResponseDTO> create(@Valid @RequestBody RefundRequestCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(refundRequestService.create(dto));
    }

    @PreAuthorize("hasAnyRole('FINANCIAL_ANALYST', 'FINANCIAL_MANAGER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<RefundRequestResponseDTO>> findAll() {
        return ResponseEntity.ok(refundRequestService.findAll());
    }

    @PreAuthorize("hasAnyRole('FINANCIAL_ANALYST', 'FINANCIAL_MANAGER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<RefundRequestResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(refundRequestService.findById(id));
    }

    @PreAuthorize("hasAnyRole('FINANCIAL_MANAGER', 'ADMIN')")
    @PatchMapping("/{id}/approve")
    public ResponseEntity<RefundRequestResponseDTO> approve(@PathVariable Long id) {
        return ResponseEntity.ok(refundRequestService.approve(id));
    }

    @PreAuthorize("hasAnyRole('FINANCIAL_MANAGER', 'ADMIN')")
    @PatchMapping("/{id}/reject")
    public ResponseEntity<RefundRequestResponseDTO> reject(@PathVariable Long id) {
        return ResponseEntity.ok(refundRequestService.reject(id));
    }
}