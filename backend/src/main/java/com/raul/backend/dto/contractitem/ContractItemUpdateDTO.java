package com.raul.backend.dto.contractitem;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractItemUpdateDTO {

    @Size(max = 100)
    private String name;

    @Size(max = 254)
    private String description;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private Boolean active;
    private Long contractId;
}
