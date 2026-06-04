package com.raul.backend.controller;

import com.raul.backend.dto.dashboard.DashboardSummaryDTO;
import com.raul.backend.dto.dashboard.MonthlyRevenueDTO;
import com.raul.backend.service.DashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCIAL_ANALYST', 'FINANCIAL_MANAGER')")
    public ResponseEntity<DashboardSummaryDTO> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }

    @GetMapping("/monthly-revenue")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCIAL_ANALYST', 'FINANCIAL_MANAGER')")
    public ResponseEntity<List<MonthlyRevenueDTO>> getMonthlyRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(dashboardService.getMonthlyRevenue(startDate, endDate));
    }
}