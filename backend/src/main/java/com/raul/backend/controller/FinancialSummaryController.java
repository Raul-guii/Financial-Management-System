package com.raul.backend.controller;

import com.raul.backend.dto.financialsummary.FinancialSummaryDTO;
import com.raul.backend.service.FinanciaSummaryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/financial")
public class FinancialSummaryController {

    private final FinanciaSummaryService financiaSummaryService;

    public FinancialSummaryController(FinanciaSummaryService financiaSummaryService) {
        this.financiaSummaryService = financiaSummaryService;
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCIAL_ANALYST', 'FINANCIAL_MANAGER')")
    public FinancialSummaryDTO getSummary(@RequestParam LocalDate start, @RequestParam LocalDate end){
        return financiaSummaryService.getSummary(start, end);
    }
}
