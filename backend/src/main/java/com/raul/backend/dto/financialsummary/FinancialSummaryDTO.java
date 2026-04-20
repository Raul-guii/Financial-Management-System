package com.raul.backend.dto.financialsummary;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class FinancialSummaryDTO {

    private LocalDate startDate;
    private LocalDate endDate;

    private BigDecimal grossRevenue;
    private BigDecimal totalReceived;
    private BigDecimal totalPending;
    private BigDecimal totalOverdue;

    private Long totalInvoices;
    private Long paidInvoices;
    private Long pendingInvoices;
    private Long overdueInvoices;

    private Long totalDefaulters;
}
