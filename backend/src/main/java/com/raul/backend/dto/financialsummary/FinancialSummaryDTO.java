package com.raul.backend.dto.financialsummary;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class FinancialSummaryDTO {

    private BigDecimal totalReceived;
    private BigDecimal totalInvoiced;
    private BigDecimal totalPending;

    private LocalDate startDate;
    private LocalDate endDate;
}
