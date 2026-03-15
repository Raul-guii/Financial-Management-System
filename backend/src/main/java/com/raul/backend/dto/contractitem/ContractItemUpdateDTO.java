package com.raul.backend.dto.contractitem;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractItemUpdateDTO {

    private String name;
    private String description;

    @Positive
    private BigDecimal quantity;

    @Positive
    private BigDecimal unitPrice;

    private Boolean active;
    private Long contractId;
}
