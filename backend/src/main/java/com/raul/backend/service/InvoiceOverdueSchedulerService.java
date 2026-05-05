package com.raul.backend.service;

import com.raul.backend.entity.Invoice;
import com.raul.backend.entity.Payment;
import com.raul.backend.enums.InvoiceStatus;
import com.raul.backend.enums.PaymentStatus;
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
    private final FinancialParameterService financialParameterService;
    private final InvoiceCalculatorService invoiceCalculatorService;

    //  @Scheduled(cron = "0 0 0 * * *")
    @Scheduled(fixedRate = 10000) // a cada 10 segundos
    @Transactional
    public void applyInterestAndFees() {

        List<Invoice> overdueInvoices =
                invoiceRepository.findByDueDateBeforeAndStatusNot(
                        LocalDate.now(),
                        InvoiceStatus.PAID
                );

        BigDecimal lateFeePercent = financialParameterService.getActiveValueByName("LATE_FEE");
        BigDecimal dailyInterest = financialParameterService.getActiveValueByName("DAILY_INTEREST");
        int graceDays = financialParameterService
                .getActiveValueByName("GRACE_PERIOD_DAYS")
                .intValue();

        for (Invoice invoice : overdueInvoices) {

            BigDecimal remaining = invoiceCalculatorService.getRemainingAmount(invoice);
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                invoice.setStatus(InvoiceStatus.PAID);
                continue;
            }

            LocalDate dueWithGrace = invoice.getDueDate().plusDays(graceDays);

            if (!LocalDate.now().isAfter(dueWithGrace)) continue;

            if (invoice.getStatus() != InvoiceStatus.OVERDUE) {
                invoice.setStatus(InvoiceStatus.OVERDUE);
            }

            long daysLate = ChronoUnit.DAYS.between(dueWithGrace, LocalDate.now());

            if (remaining.compareTo(BigDecimal.ZERO) <= 0) continue;

            BigDecimal multa = invoice.getOriginalAmount()
                    .multiply(lateFeePercent)
                    .divide(BigDecimal.valueOf(100));

            BigDecimal fatorDiario = dailyInterest.divide(BigDecimal.valueOf(100));
            BigDecimal juros = invoice.getOriginalAmount()
                    .multiply(fatorDiario)
                    .multiply(BigDecimal.valueOf(daysLate));

            BigDecimal total = invoice.getOriginalAmount().add(multa).add(juros);

            invoice.setLateFreeAmount(multa);
            invoice.setInterestAmount(juros);
            invoice.setAmount(total);

            System.out.println("Invoice " + invoice.getId() + " atualizada.");
        }

        invoiceRepository.saveAll(overdueInvoices);
    }
}