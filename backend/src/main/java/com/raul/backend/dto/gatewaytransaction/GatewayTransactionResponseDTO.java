package com.raul.backend.dto.gatewaytransaction;

import com.raul.backend.enums.GatewayStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GatewayTransactionResponseDTO {

    private Long id;
    private GatewayStatus status;
    private String externalId;
    private String gatewayName;
    private BigDecimal amount;
    private String rawResponse;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long invoiceId;
    private Long paymentId;

}
