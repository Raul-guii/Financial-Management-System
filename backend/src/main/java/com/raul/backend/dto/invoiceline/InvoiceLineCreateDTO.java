package com.raul.backend.dto.invoiceline;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceLineCreateDTO {

    @NotBlank
    private String description;

    @NotNull
    @Positive
    private BigDecimal quantity;

    @NotNull
    @PositiveOrZero
    private BigDecimal unitPrice;

    private Long contractItemId;

    @NotNull
    private Long invoiceId;
}
