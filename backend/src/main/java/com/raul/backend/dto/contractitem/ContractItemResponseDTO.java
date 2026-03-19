package com.raul.backend.dto.contractitem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractItemResponseDTO {

    private Long id;
    private String name;
    private String description;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private Boolean active;
    private Long contractId;
    private List<Long> invoiceLineIds;
}
