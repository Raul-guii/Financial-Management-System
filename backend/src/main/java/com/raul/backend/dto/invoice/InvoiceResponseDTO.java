package com.raul.backend.dto.invoice;

import com.raul.backend.enums.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponseDTO {

    private Long id;
    private InvoiceStatus status;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private BigDecimal amount;
    private BigDecimal finalAmount;
    private BigDecimal lateFreeAmount;
    private BigDecimal interestAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long contractId;
    private List<Long> paymentIds;
    private List<Long> lineIds;
}
