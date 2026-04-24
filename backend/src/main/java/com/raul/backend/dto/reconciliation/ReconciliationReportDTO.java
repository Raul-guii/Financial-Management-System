package com.raul.backend.dto.reconciliation;

import java.math.BigDecimal;

public record ReconciliationReportDTO(
        Long itemId,
        Long paymentId,
        Long invoiceId,
        Long gatewayTransactionId,
        BigDecimal systemAmount,
        BigDecimal gatewayAmount,
        String status
) {}
