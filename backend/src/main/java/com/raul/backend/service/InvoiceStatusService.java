package com.raul.backend.service;

import com.raul.backend.entity.Invoice;
import com.raul.backend.enums.InvoiceStatus;
import com.raul.backend.repository.InvoiceRepository;
import com.raul.backend.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

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

        if (invoice.getStatus() == InvoiceStatus.CANCELED || invoice.getStatus() == InvoiceStatus.REFUNDED) {
            return;
        }

        BigDecimal totalPaid = paymentRepository.sumApprovedByInvoice(invoice.getId());

        if (totalPaid.compareTo(invoice.getAmount()) >= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else if (LocalDate.now().isAfter(invoice.getDueDay())) {
            invoice.setStatus(InvoiceStatus.OVERDUE);
        } else {
            invoice.setStatus(InvoiceStatus.PENDING);
        }

        invoiceRepository.save(invoice);
    }
}