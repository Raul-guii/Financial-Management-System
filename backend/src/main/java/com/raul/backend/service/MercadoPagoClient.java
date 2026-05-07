package com.raul.backend.service;

import com.raul.backend.dto.gatewaytransaction.GatewayResponse;
import com.raul.backend.dto.payment.PaymentCreateDTO;
import com.raul.backend.entity.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class MercadoPagoClient {

    @Value("${MERCADOPAGO_ACCESS_TOKEN}")
    private String accessToken;

    private final RestTemplate restTemplate = new RestTemplate();

    public GatewayResponse createPayment(Payment payment) {

        String url = "https://api.mercadopago.com/v1/orders";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Idempotency-Key", UUID.randomUUID().toString());

        // Payer
        Map<String, String> payer = new HashMap<>();
        payer.put("email", payment.getPayerEmail()); // e-mail real do pagador
        payer.put("first_name", "APRO"); // remover em produção

        // Payment method
        Map<String, String> paymentMethod = new HashMap<>();
        paymentMethod.put("id", "pix");
        paymentMethod.put("type", "bank_transfer");

        // Payment entry
        Map<String, Object> paymentEntry = new HashMap<>();
        paymentEntry.put("amount", payment.getAmount().toString());
        paymentEntry.put("payment_method", paymentMethod);

        // Transactions
        Map<String, Object> transactions = new HashMap<>();
        transactions.put("payments", java.util.List.of(paymentEntry));

        // Body
        Map<String, Object> body = new HashMap<>();
        body.put("type", "online");
        body.put("external_reference", UUID.randomUUID().toString());
        body.put("total_amount", payment.getAmount().toString());
        body.put("payer", payer);
        body.put("transactions", transactions);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response =
                    restTemplate.postForEntity(url, request, Map.class);

            Map<String, Object> responseBody = response.getBody();
            System.out.println("RESPOSTA MP: " + responseBody);
            System.out.println("ORDER ID: " + responseBody.get("id"));

            Map<String, Object> transactionsResp =
                    (Map<String, Object>) responseBody.get("transactions");

            java.util.List<Map<String, Object>> payments =
                    (java.util.List<Map<String, Object>>) transactionsResp.get("payments");

            Map<String, Object> firstPayment = payments.get(0);
            System.out.println("PAYMENT ID: " + firstPayment.get("id"));

            Map<String, Object> paymentMethodResp =
                    (Map<String, Object>) firstPayment.get("payment_method");

            GatewayResponse gatewayResponse = new GatewayResponse();
            gatewayResponse.setOrderId(responseBody.get("id").toString());

            gatewayResponse.setTransactionId(
                    firstPayment.get("id").toString()
            );

            gatewayResponse.setStatus(responseBody.get("status").toString());
            gatewayResponse.setQrCode((String) paymentMethodResp.get("qr_code"));
            gatewayResponse.setTicketUrl((String) paymentMethodResp.get("ticket_url"));
            gatewayResponse.setRawResponse(responseBody.toString());

            return gatewayResponse;

        } catch (Exception e) {
            System.out.println("ERRO MERCADO PAGO: " + e.getMessage());
            throw new RuntimeException("Erro ao chamar Mercado Pago", e);
        }
    }

    public GatewayResponse getOrder(String orderId) {
        String url = "https://api.mercadopago.com/v1/orders/" + orderId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response =
                restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

        Map<String, Object> body = response.getBody();
        System.out.println("Resposta GET MP: " + body);

        GatewayResponse res = new GatewayResponse();
        res.setStatus(body.get("status").toString());

        return res;
    }

    public void refundPayment(String mercadoPagoPaymentId) {
        String url = "https://api.mercadopago.com/v1/payments/" + mercadoPagoPaymentId + "/refunds";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Idempotency-Key", UUID.randomUUID().toString());

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            restTemplate.postForEntity(url, request, Map.class);
        } catch (Exception e) {
            System.out.println("ERRO REFUND MP: " + e.getMessage());
            throw new RuntimeException("Erro ao processar reembolso no Mercado Pago", e);
        }
    }
}