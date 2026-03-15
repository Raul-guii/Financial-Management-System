package com.raul.backend.dto.contract;

import com.raul.backend.enums.BillingPeriod;
import com.raul.backend.enums.ContractStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractResponseDTO {

    private Long id;
    private ContractStatus status;
    private BillingPeriod billingPeriod;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long createdById;
    private Long clientId;
    private List<Long> invoiceIds;
    private List<Long> itemIds;
}
