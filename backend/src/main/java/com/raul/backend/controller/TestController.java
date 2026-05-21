package com.raul.backend.controller;

import com.raul.backend.entity.Payment;
import com.raul.backend.enums.PaymentStatus;
import com.raul.backend.repository.PaymentRepository;
import com.raul.backend.service.InvoiceStatusService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {

    private final PaymentRepository paymentRepository;
    private final InvoiceStatusService invoiceStatusService;

    public TestController(PaymentRepository paymentRepository,
                          InvoiceStatusService invoiceStatusService) {
        this.paymentRepository = paymentRepository;
        this.invoiceStatusService = invoiceStatusService;
    }

    @PostMapping("/simulate-approval/{paymentId}")
    public void simulateApproval(@PathVariable Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment não encontrado"));

        payment.setPaymentStatus(PaymentStatus.APPROVED);
        paymentRepository.save(payment);

        invoiceStatusService.recalculateInvoiceStatus(payment.getInvoice().getId());
    }

    @PostMapping("/simulate-refund/{paymentId}")
    public void simulateRefund(@PathVariable Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment não encontrado"));

        payment.setPaymentStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);

        invoiceStatusService.recalculateInvoiceStatus(payment.getInvoice().getId());
    }

}