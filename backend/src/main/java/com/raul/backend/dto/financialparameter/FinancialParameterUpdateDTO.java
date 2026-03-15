package com.raul.backend.dto.financialparameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialParameterUpdateDTO {

    private String value;
    private String type;
    private String description;
    private Boolean active;
}
