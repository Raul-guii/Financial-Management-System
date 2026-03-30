package com.raul.backend.dto.payment;

import com.raul.backend.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {

    private Long id;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;
    private Long invoiceId;
    private Long gatewayTransactionId;
    private List<Long> refundRequestIds;
    private PaymentStatus paymentStatus;
    private String qrcode;
    private String ticketUrl;
}
