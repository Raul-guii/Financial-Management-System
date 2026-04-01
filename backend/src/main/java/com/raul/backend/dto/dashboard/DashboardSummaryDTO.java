package com.raul.backend.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DashboardSummaryDTO {

    private BigDecimal grossRevenue;
    private BigDecimal refunded;
    private BigDecimal netRevenue;

    private BigDecimal totalPending;

    private Long paidInvoices;
    private Long pendingInvoices;
    private Long overdueInvoices;
}
