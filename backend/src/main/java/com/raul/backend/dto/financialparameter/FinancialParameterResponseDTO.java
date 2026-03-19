package com.raul.backend.dto.financialparameter;

import com.raul.backend.enums.FinancialParameterType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialParameterResponseDTO {

    private Long id;
    private String name;
    private String value;
    private FinancialParameterType type;
    private String description;
    private Boolean active;
    private Long updatedById;
    private Long createdById;
}
