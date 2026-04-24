package com.raul.backend.controller;

import com.raul.backend.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PreAuthorize("hasAnyRole('FINANCIAL_ANALYST', 'FINANCIAL_MANAGER', 'ADMIN')")
    @GetMapping("/reconciliation")
    public ResponseEntity<byte[]> exportReconciliation() {

        String csv = reportService.generateReconciliationCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reconciliation.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(csv.getBytes());
    }
}