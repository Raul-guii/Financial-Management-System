package com.raul.backend.dto.refundrequest;

import com.raul.backend.enums.RefundStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequestUpdateDTO {

    private RefundStatus refundStatus;
    private String reason;
    private LocalDateTime approvedAt;
}
