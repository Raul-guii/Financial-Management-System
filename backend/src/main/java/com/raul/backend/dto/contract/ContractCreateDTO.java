package com.raul.backend.dto.contract;

import com.raul.backend.enums.BillingPeriod;
import com.raul.backend.enums.ContractStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private Long clientId;
}
