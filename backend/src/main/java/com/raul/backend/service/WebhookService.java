package com.raul.backend.service;

import com.raul.backend.dto.gatewaytransaction.GatewayResponse;
import com.raul.backend.entity.GatewayTransaction;
import com.raul.backend.entity.Invoice;
import com.raul.backend.entity.Payment;
import com.raul.backend.enums.GatewayStatus;
import com.raul.backend.enums.PaymentStatus;
import com.raul.backend.repository.GatewayTransactionRepository;
import com.raul.backend.repository.InvoiceRepository;
import com.raul.backend.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class WebhookService {

    private final GatewayTransactionRepository repository;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final MercadoPagoClient mercadoPagoClient;
    private final InvoiceStatusService invoiceStatusService;

    public WebhookService(GatewayTransactionRepository repository, PaymentRepository paymentRepository, InvoiceRepository invoiceRepository, MercadoPagoClient mercadoPagoClient, InvoiceStatusService invoiceStatusService) {
        this.repository = repository;
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.mercadoPagoClient = mercadoPagoClient;
        this.invoiceStatusService = invoiceStatusService;
    }

    @Transactional
    public void process(Map<String, Object> payload) {

        String externalId = extractExternalId(payload);
        if (externalId == null) {
            System.out.println("WEBHOOK: externalId não encontrado no payload");
            return;
        }

        GatewayTransaction transaction = repository
                .findByExternalId(externalId)
                .orElse(null);

        if (transaction == null) {
            System.out.println("WEBHOOK: transaction não encontrada para externalId: " + externalId);
            return;
        }

        // busca status real do MP
        GatewayResponse response = mercadoPagoClient.getOrder(externalId);

        transaction.setStatus(mapStatus(response.getStatus()));
        repository.save(transaction);

        Payment payment = transaction.getPayment();
        payment.setPaymentStatus(mapToPaymentStatus(transaction.getStatus()));
        paymentRepository.save(payment);

        invoiceStatusService.recalculateInvoiceStatus(payment.getInvoice().getId());
    }

    private String extractExternalId(Map<String, Object> payload) {
        try {
            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            if (data == null) return null;
            Object id = data.get("id");
            return id != null ? id.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private GatewayStatus mapStatus(String status){
        if (status == null) return GatewayStatus.ERROR;

        return switch (status){
            case "approved" -> GatewayStatus.APPROVED;
            case "pending" -> GatewayStatus.PENDING;
            case "rejected" -> GatewayStatus.REJECTED;
            case "action_required" -> GatewayStatus.PENDING;
            case "processing" -> GatewayStatus.PENDING;
            default -> GatewayStatus.ERROR;
        };
    }

    private PaymentStatus mapToPaymentStatus(GatewayStatus status) {
        return switch (status) {
            case APPROVED -> PaymentStatus.APPROVED;
            case REJECTED -> PaymentStatus.REJECTED;
            case PENDING -> PaymentStatus.PENDING;
            default -> PaymentStatus.ERROR;
        };
    }

    private String extractStatus(Map<String, Object> payload) {
        return payload.get("status").toString();
    }
}
