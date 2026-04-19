package com.raul.backend.dto.invoice;

import com.raul.backend.enums.InvoiceStatus;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceUpdateDTO {

    private InvoiceStatus status;
    private LocalDate issueDate;
    private LocalDate dueDate;

    @PositiveOrZero
    private BigDecimal amount;

    @PositiveOrZero
    private BigDecimal lateFreeAmount;

    @PositiveOrZero
    private BigDecimal interestAmount;

    private Long contractId;
}
