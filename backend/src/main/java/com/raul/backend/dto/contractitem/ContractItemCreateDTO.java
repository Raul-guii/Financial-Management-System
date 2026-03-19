package com.raul.backend.dto.contractitem;

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
public class ContractItemCreateDTO {

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 254)
    private String description;

    @NotNull
    private BigDecimal quantity;

    @NotNull
    private BigDecimal unitPrice;

    private Boolean active;

    @NotNull
    private Long contractId;
}
