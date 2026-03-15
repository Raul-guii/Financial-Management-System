package com.raul.backend.dto.reconciliation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliationUpdateDTO {

    private LocalDate periodStart;
    private LocalDate periodEnd;
    private LocalDateTime executedAt;
    private Long executedById;
}
