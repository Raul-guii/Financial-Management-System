package com.raul.backend.service;

import com.raul.backend.dto.payment.*;
import com.raul.backend.entity.Invoice;
import com.raul.backend.entity.Payment;
import com.raul.backend.enums.InvoiceStatus;
import com.raul.backend.enums.PaymentStatus;
import com.raul.backend.repository.GatewayTransactionRepository;
import com.raul.backend.repository.InvoiceRepository;
import com.raul.backend.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentService {

    private final GatewayTransactionService gatewayTransactionService;
    private final PaymentRepository repository;
    private final InvoiceRepository invoiceRepository;

    public PaymentService(GatewayTransactionService gatewayTransactionService, PaymentRepository repository, InvoiceRepository invoiceRepository) {
        this.gatewayTransactionService = gatewayTransactionService;
        this.repository = repository;
        this.invoiceRepository = invoiceRepository;
    }

    // CREATE
    @Transactional
    public PaymentResponseDTO create(PaymentCreateDTO dto) {

        Invoice invoice = invoiceRepository.findById(dto.getInvoiceId())
                .orElseThrow(() -> new RuntimeException("Invoice não encontrada"));

        Payment payment = new Payment();

        payment.setAmount(dto.getAmount());
        payment.setPaymentDate(dto.getPaymentDate());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setInvoice(invoice);

        payment = repository.save(payment);

        // chama gateway
        gatewayTransactionService.processPayment(payment);

        payment = repository.findById(payment.getId()).get();
        return toDTO(payment);
    }

    // UPDATE
    @Transactional
    public PaymentResponseDTO update(Long id, PaymentUpdateDTO dto) {

        Payment payment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment não encontrado"));

        if (dto.getAmount() != null) payment.setAmount(dto.getAmount());
        if (dto.getPaymentDate() != null) payment.setPaymentDate(dto.getPaymentDate());
        if (dto.getPaymentMethod() != null) { payment.setPaymentMethod(dto.getPaymentMethod());}
        if (dto.getInvoiceId() != null) {
            Invoice invoice = invoiceRepository.findById(dto.getInvoiceId())
                    .orElseThrow(() -> new RuntimeException("Invoice não encontrada"));
            payment.setInvoice(invoice);
        }

        payment = repository.save(payment);

        return toDTO(payment);
    }

    // GET ALL
    public List<PaymentResponseDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // GET BY ID
    public PaymentResponseDTO findById(Long id) {
        return repository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Payment não encontrado"));
    }

    // DELETE
    @Transactional
    public void delete(Long id) {

        Payment payment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment não encontrado"));

        Invoice invoice = payment.getInvoice();

        repository.delete(payment);

        updateInvoiceStatus(invoice);
    }

    // REGRA CENTRAL
    private void updateInvoiceStatus(Invoice invoice) {

        BigDecimal totalPaid = repository.sumApprovedByInvoice(invoice.getId());

        if (totalPaid.compareTo(invoice.getAmount()) >= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else {
            invoice.setStatus(InvoiceStatus.PENDING);
        }

        invoiceRepository.save(invoice);
    }

    // MAPPER
    private PaymentResponseDTO toDTO(Payment payment) {
        return new PaymentResponseDTO(
                payment.getId(),
                payment.getAmount(),
                payment.getPaymentDate(),
                payment.getCreatedAt(),
                payment.getInvoice() != null ? payment.getInvoice().getId() : null,
                payment.getGatewayTransaction() != null ? payment.getGatewayTransaction().getId() : null,
                payment.getRefundRequests() != null
                        ? payment.getRefundRequests().stream().map(r -> r.getId()).toList()
                        : List.of(),
                payment.getPaymentStatus(),
                payment.getGatewayTransaction() != null
                        ? payment.getGatewayTransaction().getQrCode()
                        : null,
                payment.getGatewayTransaction() != null
                        ? payment.getGatewayTransaction().getTicketUrl()
                        : null
        );
    }
}