package com.raul.backend.dto.contract;

import com.raul.backend.enums.BillingPeriod;
import com.raul.backend.enums.ContractStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractUpdateDTO {

    private ContractStatus status;
    private BillingPeriod billingPeriod;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long clientId;
}
