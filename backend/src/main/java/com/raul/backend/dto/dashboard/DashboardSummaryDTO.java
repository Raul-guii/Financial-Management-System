package com.raul.backend.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DashboardSummaryDTO {

    private BigDecimal totalReceived;
    private BigDecimal totalPending;
    private BigDecimal totalRefunded;

    private Long paidInvoices;
    private Long pendingInvoices;
    private Long overdueInvoices;
}
