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
public class ContractUpdateDTO {

    @NotNull
    @Size(max = 20)
    private ContractStatus status;

    @NotNull
    @Size(max = 20)
    private BillingPeriod billingPeriod;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;
}
