package com.raul.backend.dto.financialparameter;

import com.raul.backend.enums.FinancialParameterType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialParameterCreateDTO {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 254)
    private String value;

    @NotNull
    private FinancialParameterType type;

    @NotBlank
    @Size(max = 254)
    private String description;

    private Boolean active;

    @NotNull
    private Long updatedById;

    @NotNull
    private Long createdById;
}
