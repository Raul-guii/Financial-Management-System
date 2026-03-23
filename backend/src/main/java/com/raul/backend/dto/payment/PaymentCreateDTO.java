package com.raul.backend.dto.payment;

import com.raul.backend.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateDTO {

    @NotNull
    @PositiveOrZero
    private BigDecimal amount;

    @NotNull
    private LocalDateTime paymentDate;

    @NotNull
    private PaymentMethod paymentMethod;

    @NotNull
    private Long invoiceId;
}
