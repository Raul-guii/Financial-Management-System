package com.raul.backend.service;

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

import java.util.UUID;

@Service
public class GatewayTransactionService {

    private final GatewayTransactionRepository repository;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    public GatewayTransactionService(GatewayTransactionRepository repository,
                          PaymentRepository paymentRepository,
                          InvoiceRepository invoiceRepository) {
        this.repository = repository;
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional
    public GatewayTransaction processPayment(Payment payment) {

        // Criar transação
        GatewayTransaction transaction = new GatewayTransaction();

        transaction.setPayment(payment);
        transaction.setExternalId(UUID.randomUUID().toString());
        transaction.setGatewayName("FAKE_GATEWAY");
        transaction.setAmount(payment.getAmount());

        // Simular resposta
        boolean approved = Math.random() > 0.2;

        if (approved) {
            transaction.setStatus(GatewayStatus.APPROVED);
            payment.setPaymentStatus(PaymentStatus.APPROVED);
        } else {
            transaction.setStatus(GatewayStatus.REJECTED);
            payment.setPaymentStatus(PaymentStatus.REJECTED);
        }

        transaction.setRawResponse("{\"mock\": true}");

        // Salvar transação
        transaction = repository.save(transaction);

        // Linkar no payment
        payment.setGatewayTransaction(transaction);
        paymentRepository.save(payment);

        // atualizar invoice (se aprovado)
        if (transaction.getStatus() == GatewayStatus.APPROVED) {

            Invoice invoice = payment.getInvoice();

            updateInvoiceStatus(invoice);
        }

        return transaction;
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
}