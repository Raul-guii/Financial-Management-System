package com.raul.backend.dto.financialparameter;

import com.raul.backend.enums.FinancialParameterCategory;
import com.raul.backend.enums.FinancialParameterType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialParameterResponseDTO {

    private Long id;
    private String name;
    private BigDecimal value;
    private FinancialParameterType type;
    private FinancialParameterCategory category;
    private String description;
    private Boolean active;
    private Long updatedById;
    private Long createdById;
}
