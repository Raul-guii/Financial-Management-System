package com.raul.backend.dto.reconciliationitem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliationItemResponseDTO {

    private Long id;
    private Long paymentId;
    private Long gatewayTransactionId;
    private BigDecimal systemAmount;
    private BigDecimal gatewayAmount;
    private String status;
}
