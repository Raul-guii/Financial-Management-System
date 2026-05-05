package com.raul.backend.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raul.backend.enums.PaymentMethod;
import com.raul.backend.enums.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateDTO {

    @NotNull
    @PositiveOrZero
    private BigDecimal amount;

    @NotNull
    private LocalDateTime paymentDate;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private String payerEmail;

    @NotBlank
    private String payerFirstName;

    @NotBlank
    private String payerLastName;

    @NotBlank
    private String payerDocument;

    @JsonProperty("date_of_expiration")
    private OffsetDateTime dateOfExpiration;

    @NotNull
    private Long invoiceId;
}
