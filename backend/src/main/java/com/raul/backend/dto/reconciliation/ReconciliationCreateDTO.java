package com.raul.backend.dto.reconciliation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliationCreateDTO {

    @NotNull
    private LocalDate periodStart;

    @NotNull
    private LocalDate periodEnd;

    private LocalDateTime executedAt;
    private Long executedById;
}
