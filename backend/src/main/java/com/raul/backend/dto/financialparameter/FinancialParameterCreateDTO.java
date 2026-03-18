package com.raul.backend.dto.financialparameter;

import com.raul.backend.enums.FinancialParameterType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialParameterCreateDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String value;

    @NotBlank
    private FinancialParameterType type;

    @NotBlank
    private String description;

    private Boolean active;
}
