package com.raul.backend.service;

import com.raul.backend.entity.Invoice;
import com.raul.backend.enums.InvoiceStatus;
import com.raul.backend.repository.InvoiceRepository;
import com.raul.backend.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class InvoiceStatusService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    public InvoiceStatusService(PaymentRepository paymentRepository,
                                InvoiceRepository invoiceRepository) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional
    public void recalculateInvoiceStatus(Long invoiceId) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice não encontrada"));

        BigDecimal totalPaid = paymentRepository.sumApprovedByInvoice(invoice.getId());

        if (totalPaid.compareTo(invoice.getAmount()) >= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else {
            invoice.setStatus(InvoiceStatus.PENDING);
        }

        invoiceRepository.save(invoice);
    }
}
