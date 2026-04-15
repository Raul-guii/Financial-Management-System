package com.raul.backend.service;

import com.raul.backend.entity.Invoice;
import com.raul.backend.enums.InvoiceStatus;
import com.raul.backend.repository.InvoiceRepository;
import com.raul.backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceOverdueSchedulerService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void markOverdueInvoices() {
        List<Invoice> overdueCandidates =
                invoiceRepository.findByStatusAndDueDayBefore(InvoiceStatus.PENDING, LocalDate.now());

        for (Invoice invoice : overdueCandidates) {
            if (paymentRepository.sumApprovedByInvoice(invoice.getId()).compareTo(invoice.getAmount()) < 0) {
                invoice.setStatus(InvoiceStatus.OVERDUE);
            }
        }

        invoiceRepository.saveAll(overdueCandidates);
    }
}