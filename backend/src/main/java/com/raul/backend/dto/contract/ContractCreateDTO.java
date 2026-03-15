package com.raul.backend.dto.contract;

import com.raul.backend.enums.BillingPeriod;
import com.raul.backend.enums.ContractStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractCreateDTO {

    @NotNull
    private ContractStatus status;

    @NotNull
    private BillingPeriod billingPeriod;

    private LocalDate startDate;
    private LocalDate endDate;

    @NotNull
    private Long clientId;
}
