package com.raul.backend.service;

import com.raul.backend.entity.Invoice;
import com.raul.backend.enums.InvoiceStatus;
import com.raul.backend.repository.InvoiceRepository;
import com.raul.backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class InvoiceStatusService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;


    @Transactional
    public void recalculateInvoiceStatus(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice não encontrada"));

        if (invoice.getStatus() == InvoiceStatus.CANCELLED
                || invoice.getStatus() == InvoiceStatus.REFUNDED) {
            return;
        }

        BigDecimal totalPaid = paymentRepository.sumApprovedByInvoice(invoice.getId());

        if (totalPaid.compareTo(invoice.getAmount()) >= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        } else if (LocalDate.now().isAfter(invoice.getDueDate())) {
            invoice.setStatus(InvoiceStatus.OVERDUE);
        } else {
            invoice.setStatus(InvoiceStatus.PENDING);
        }

        invoiceRepository.save(invoice);
    }
}