package com.raul.backend.dto.financialparameter;

import com.raul.backend.enums.FinancialParameterCategory;
import com.raul.backend.enums.FinancialParameterType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialParameterCreateDTO {

    @NotBlank
    @Size(max = 100)
    private String name;

    @DecimalMin("0.0")
    private BigDecimal value;

    @NotNull
    private FinancialParameterType type;

    private FinancialParameterCategory category;

    @NotBlank
    @Size(max = 254)
    private String description;

    private Boolean active;

}

