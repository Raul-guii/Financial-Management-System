package com.raul.backend.service;

import com.raul.backend.dto.gatewaytransaction.GatewayResponse;
import com.raul.backend.dto.payment.PaymentCreateDTO;
import com.raul.backend.entity.GatewayTransaction;
import com.raul.backend.entity.Invoice;
import com.raul.backend.entity.Payment;
import com.raul.backend.enums.GatewayStatus;
import com.raul.backend.enums.PaymentStatus;
import com.raul.backend.repository.GatewayTransactionRepository;
import com.raul.backend.repository.InvoiceRepository;
import com.raul.backend.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GatewayTransactionService {

    private final GatewayTransactionRepository repository;
    private final PaymentRepository paymentRepository;
    private final InvoiceStatusService invoiceStatusService;
    private final MercadoPagoClient mercadoPagoClient;

    @Transactional
    public GatewayTransaction processPayment(Payment payment) {
        
     GatewayResponse response = mercadoPagoClient.createPayment(payment);

     GatewayTransaction transaction = new GatewayTransaction();

     transaction.setPayment(payment);
     transaction.setExternalId(response.getOrderId());
     transaction.setTransactionId(response.getTransactionId());
     transaction.setGatewayName("MERCADO_PAGO");
     transaction.setAmount(payment.getAmount());

     transaction.setStatus(mapStatus(response.getStatus()));

     transaction.setQrCode(response.getQrCode());
     transaction.setTicketUrl(response.getTicketUrl());

     transaction.setRawResponse(response.getRawResponse());

     transaction = repository.save(transaction);


     payment.setGatewayTransaction(transaction);
     payment.setPaymentStatus(mapToPaymentStatus(transaction.getStatus()));

     paymentRepository.save(payment);

     invoiceStatusService.recalculateInvoiceStatus(payment.getInvoice().getId());
     return transaction;
    }

    private GatewayStatus mapStatus(String status){
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

    @Transactional
    public void processRefund(Payment payment) {
        GatewayTransaction transaction = payment.getGatewayTransaction();

        if (transaction == null) {
            throw new RuntimeException("The payment has no transaction on the gateway");
        }

        try {
            mercadoPagoClient.refundPayment(transaction.getTransactionId());
        } catch (Exception e) {
            System.out.println("WARNING: Refund on the gateway failed(sandbox): " + e.getMessage());
        }

        transaction.setStatus(GatewayStatus.REFUNDED);
        repository.save(transaction);
    }
}