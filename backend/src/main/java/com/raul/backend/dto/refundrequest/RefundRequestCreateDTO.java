package com.raul.backend.dto.refundrequest;

import com.raul.backend.enums.RefundStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequestCreateDTO {

    @NotBlank
    private String reason;

    @NotNull
    private Long paymentId;
}
