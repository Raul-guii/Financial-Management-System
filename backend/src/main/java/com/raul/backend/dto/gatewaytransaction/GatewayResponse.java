package com.raul.backend.dto.gatewaytransaction;

import lombok.Data;

@Data
public class GatewayResponse {

    private Long transactionId;
    private String status;
    private String statusDetail;

    private String qrCode;
    private String ticketUrl;

    private String rawResponse;
}