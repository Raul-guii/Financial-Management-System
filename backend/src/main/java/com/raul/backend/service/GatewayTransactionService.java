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
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class GatewayTransactionService {

    private final GatewayTransactionRepository repository;
    private final PaymentRepository paymentRepository;
    private final InvoiceStatusService invoiceStatusService;
    private final MercadoPagoClient mercadoPagoClient;

    public GatewayTransactionService(GatewayTransactionRepository repository, PaymentRepository paymentRepository, InvoiceStatusService invoiceStatusService, MercadoPagoClient mercadoPagoClient) {
        this.repository = repository;
        this.paymentRepository = paymentRepository;
        this.invoiceStatusService = invoiceStatusService;
        this.mercadoPagoClient = mercadoPagoClient;
    }

    @Transactional
    public GatewayTransaction processPayment(Payment payment) {

     // chamar gateway
     GatewayResponse response = mercadoPagoClient.createPayment(payment);

     // criar transaction
     GatewayTransaction transaction = new GatewayTransaction();

     transaction.setPayment(payment);
     transaction.setExternalId(String.valueOf(response.getTransactionId()));
     transaction.setGatewayName("MERCADO_PAGO");
     transaction.setAmount(payment.getAmount());

     transaction.setStatus(mapStatus(response.getStatus()));

     transaction.setQrCode(response.getQrCode());
     transaction.setTicketUrl(response.getTicketUrl());

     transaction.setRawResponse(response.getRawResponse());

     transaction = repository.save(transaction);

     // atualizar payment
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
}