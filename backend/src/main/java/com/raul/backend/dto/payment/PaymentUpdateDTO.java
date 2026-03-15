package com.raul.backend.dto.payment;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentUpdateDTO {

    @PositiveOrZero
    private BigDecimal amount;

    private LocalDateTime paymentDate;
    private Long invoiceId;
}
