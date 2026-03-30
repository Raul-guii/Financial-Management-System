package com.raul.backend.dto.mercadopago;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MercadoPagoResponseDTO {

    private Long id;
    private String status;
    private String statusDetail;

    private BigDecimal transactionAmount;

    private PointOfInteraction pointOfInteraction;

    @Data
    public static class PointOfInteraction {
        private TransactionData transactionData;
    }

    @Data
    public static class TransactionData {
        private String qrCode;
        private String ticketUrl;
    }
}
