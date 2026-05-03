package com.raul.backend.service;

import com.raul.backend.dto.gatewaytransaction.GatewayResponse;
import com.raul.backend.entity.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class MercadoPagoClient {

    @Value("${MERCADOPAGO_ACCESS_TOKEN}")
    private String accessToken;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final int MAX_RETRIES = 3;

    public GatewayResponse createPayment(Payment payment) {

        int attempt = 0;

        while (true) {
            try {
                return doCreatePayment(payment);

            } catch (HttpServerErrorException | ResourceAccessException e) {

                attempt++;

                System.out.println("Tentativa " + attempt + " falhou");

                if (attempt >= MAX_RETRIES) {
                    throw new RuntimeException("Gateway indisponível após retries");
                }

                try {
                    Thread.sleep(1000 * attempt);
                } catch (InterruptedException ignored) {}
            }
        }
    }

    private GatewayResponse doCreatePayment(Payment payment) {

        String url = "https://api.mercadopago.com/v1/payments";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Idempotency-Key", UUID.randomUUID().toString());

        Map<String, Object> body = new HashMap<>();
        body.put("transaction_amount", payment.getAmount().doubleValue());
        body.put("payment_method_id", "pix");
        body.put("description", "Pagamento teste");

        Map<String, Object> payer = new HashMap<>();
        payer.put("email", "test_user_3942810040893257769@testuser.com");

        body.put("payer", payer);

        System.out.println("URL: " + url);
        System.out.println("HEADERS: " + headers);
        System.out.println("BODY: " + body);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(url, request, Map.class);

            Map<String, Object> responseBody = response.getBody();

            Map<String, Object> poi =
                    (Map<String, Object>) responseBody.get("point_of_interaction");

            Map<String, Object> transactionData =
                    (Map<String, Object>) poi.get("transaction_data");

            GatewayResponse gatewayResponse = new GatewayResponse();

            gatewayResponse.setTransactionId(
                    Long.valueOf(responseBody.get("id").toString())
            );

            gatewayResponse.setStatus(responseBody.get("status").toString());

            gatewayResponse.setQrCode((String) transactionData.get("qr_code"));
            gatewayResponse.setTicketUrl((String) transactionData.get("ticket_url"));

            gatewayResponse.setRawResponse(responseBody.toString());

            return gatewayResponse;

        } catch (HttpClientErrorException e) {

            System.out.println("ERRO CLIENTE MP: " + e.getResponseBodyAsString());

            throw e;

        } catch (HttpServerErrorException e) {

            System.out.println("ERRO SERVIDOR MP: " + e.getResponseBodyAsString());

            throw e;

        } catch (ResourceAccessException e) {

            System.out.println("ERRO CONEXÃO MP: " + e.getMessage());

            throw e;

        }
    }

    public GatewayResponse getPayment(String id) {
        String url = "https://api.mercadopago.com/v1/payments/" + id;

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


}
