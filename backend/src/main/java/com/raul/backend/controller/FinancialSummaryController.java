package com.raul.backend.controller;

import com.raul.backend.dto.financialsummary.FinancialSummaryDTO;
import com.raul.backend.service.FinancialSummaryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/financial")
public class FinancialSummaryController {

    private final FinancialSummaryService financialSummaryService;

    public FinancialSummaryController(FinancialSummaryService financialSummaryService) {
        this.financialSummaryService = financialSummaryService;
    }

    /*
    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCIAL_ANALYST', 'FINANCIAL_MANAGER')")
    public FinancialSummaryDTO getSummary(@RequestParam LocalDate start, @RequestParam LocalDate end){
        return financialSummaryService.getSummary(start, end);
    }*/

    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_MANAGER')")
    @GetMapping("/financial/report")
    public FinancialSummaryDTO getReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return financialSummaryService.getFullReport(start, end);
    }

    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL_MANAGER')")
    @GetMapping("/financial/report/export")
    public ResponseEntity<byte[]> exportReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {

        FinancialSummaryDTO report = financialSummaryService.getFullReport(start, end);

        String content = """
        Relatório Financeiro
    
        Período: %s a %s
        Receita Bruta: %s
        Recebido: %s
        Pendente: %s
        Inadimplente: %s
        """.formatted(
                report.getStartDate(),
                report.getEndDate(),
                report.getGrossRevenue(),
                report.getTotalReceived(),
                report.getTotalPending(),
                report.getTotalOverdue()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.txt")
                .body(content.getBytes());
    }
}
