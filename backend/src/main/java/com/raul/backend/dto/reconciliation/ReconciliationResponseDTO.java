package com.raul.backend.dto.reconciliation;

import com.raul.backend.dto.reconciliationitem.ReconciliationItemResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliationResponseDTO {

    private Long id;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private LocalDateTime executedAt;
    private Long executedById;
    private List<ReconciliationItemResponseDTO> items;
}
