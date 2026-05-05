package com.raul.backend.dto.gatewaytransaction;

import lombok.Data;

@Data
public class GatewayResponse {

    private String transactionId;
    private String status;
    private String statusDetail;
    private String orderId;

    private String qrCode;
    private String ticketUrl;

    private String rawResponse;
}