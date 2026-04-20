package com.raul.backend.service;

import com.raul.backend.entity.Invoice;
import com.raul.backend.enums.InvoiceStatus;
import com.raul.backend.repository.InvoiceRepository;
import com.raul.backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceOverdueSchedulerService {

    private final InvoiceRepository invoiceRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void applyInterestAndFees() {

        List<Invoice> overdueInvoices =
                invoiceRepository.findByStatus(InvoiceStatus.OVERDUE);

        for (Invoice invoice : overdueInvoices) {

            long daysLate = ChronoUnit.DAYS.between(
                    invoice.getDueDate(),
                    LocalDate.now()
            );

            if (daysLate <= 0) continue;

            BigDecimal total = invoice.getAmount();

            // multa fixa
            if (invoice.getLateFreeAmount() != null) {
                total = total.add(invoice.getLateFreeAmount());
            }

            // juros por dia
            if (invoice.getInterestAmount() != null) {
                BigDecimal interest = total
                        .multiply(invoice.getInterestAmount())
                        .multiply(BigDecimal.valueOf(daysLate));

                total = total.add(interest);
            }

            invoice.setAmount(total);
        }

        invoiceRepository.saveAll(overdueInvoices);
    }
}