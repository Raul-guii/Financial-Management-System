package com.raul.backend.dto.financialparameter;

import com.raul.backend.enums.FinancialParameterType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialParameterUpdateDTO {

    @Size(max = 100)
    private String name;

    @Size(max = 254)
    private String value;
    private FinancialParameterType type;

    @Size(max = 254)
    private String description;
    private Boolean active;
    private Long updatedById;
    private Long createdById;
}
