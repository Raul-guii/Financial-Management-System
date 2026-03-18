package com.raul.backend.dto.financialparameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialParameterResponseDTO {

    private Long id;
    private String name;
    private String value;
    private String type;
    private String description;
    private Boolean active;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    private Long updatedById;
    private Long createdById;
}
