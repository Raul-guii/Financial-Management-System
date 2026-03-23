package com.raul.backend.dto.invoice;

import com.raul.backend.enums.InvoiceStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceCreateDTO {

    @NotNull
    private InvoiceStatus status;

    @NotNull
    private LocalDate issueDate;

    @NotNull
    private LocalDate dueDay;

    @NotNull
    @PositiveOrZero
    private BigDecimal lateFreeAmount;

    @NotNull
    @PositiveOrZero
    private BigDecimal interestAmount;

    @NotNull
    private Long contractId;
}
