package com.raul.backend.service;

import com.raul.backend.dto.financialsummary.FinancialSummaryDTO;
import com.raul.backend.repository.InvoiceRepository;
import com.raul.backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class FinanciaSummaryService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    public FinanciaSummaryService(PaymentRepository paymentRepository, InvoiceRepository invoiceRepository) {

        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public FinancialSummaryDTO getSummary(LocalDate start, LocalDate end) {

        // Ajuste importante porque payment usa LocalDateTime
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);

        BigDecimal totalReceived = paymentRepository
                .sumApprovedPaymentsByPeriod(startDateTime, endDateTime);

        BigDecimal totalInvoiced = invoiceRepository
                .sumInvoicesByPeriod(start, end);

        BigDecimal totalPending = totalInvoiced.subtract(totalReceived);

        return new FinancialSummaryDTO(
                totalReceived,
                totalInvoiced,
                totalPending,
                start,
                end
        );
    }
}
