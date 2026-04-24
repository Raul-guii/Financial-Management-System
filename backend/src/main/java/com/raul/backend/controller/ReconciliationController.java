package com.raul.backend.controller;

import com.raul.backend.dto.reconciliation.ReconciliationCreateDTO;
import com.raul.backend.dto.reconciliation.ReconciliationResponseDTO;
import com.raul.backend.service.ReconciliationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reconciliations")
@RequiredArgsConstructor
public class ReconciliationController {

    private final ReconciliationService reconciliationService;

    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_ANALYST','FINANCIAL_MANAGER')")
    @PostMapping
    public ResponseEntity<ReconciliationResponseDTO> execute(
            @RequestBody @Valid ReconciliationCreateDTO dto
    ) {
        ReconciliationResponseDTO response = reconciliationService.execute(dto);
        return ResponseEntity.ok(response);
    }
}