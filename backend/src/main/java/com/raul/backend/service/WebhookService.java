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

    public WebhookService(GatewayTransactionRepository repository, PaymentRepository paymentRepository, InvoiceRepository invoiceRepository, MercadoPagoClient mercadoPagoClient) {
        this.repository = repository;
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.mercadoPagoClient = mercadoPagoClient;
    }

    @Transactional
    public void process(Map<String, Object> payload) {

        // pegar ID da transação do gateway
        String externalId = extractExternalId(payload);

        GatewayTransaction transaction = repository
                .findByExternalId(externalId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        //atualizar status
        GatewayResponse response = mercadoPagoClient.getPayment(externalId);

        response.setStatus("approved");

        transaction.setStatus(mapStatus(response.getStatus()));

        repository.save(transaction);

        Payment payment = transaction.getPayment();
        payment.setPaymentStatus(mapToPaymentStatus(transaction.getStatus()));

        paymentRepository.save(payment);

        updateInvoiceStatus(payment.getInvoice());
    }

    private String extractExternalId(Map<String, Object> payload) {

        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        return data.get("id").toString();
    }

    private void updateInvoiceStatus(Invoice invoice) {

        var totalPaid = paymentRepository.sumApprovedByInvoice(invoice.getId());

        if (totalPaid.compareTo(invoice.getAmount()) >= 0) {
            invoice.setStatus(com.raul.backend.enums.InvoiceStatus.PAID);
        } else {
            invoice.setStatus(com.raul.backend.enums.InvoiceStatus.PENDING);
        }

        invoiceRepository.save(invoice);
    }

    private GatewayStatus mapStatus(String status){
        if (status == null) return GatewayStatus.ERROR;

        return switch (status){
            case "approved" -> GatewayStatus.APPROVED;
            case "pending" -> GatewayStatus.PENDING;
            case "rejected" -> GatewayStatus.REJECTED;
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
