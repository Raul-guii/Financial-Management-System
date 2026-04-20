package com.raul.backend.service;

import com.raul.backend.dto.financialsummary.FinancialSummaryDTO;
import com.raul.backend.repository.ClientRepository;
import com.raul.backend.repository.InvoiceRepository;
import com.raul.backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.raul.backend.enums.InvoiceStatus.*;

@Service
public class FinancialSummaryService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;

    public FinancialSummaryService(PaymentRepository paymentRepository, InvoiceRepository invoiceRepository, ClientRepository clientRepository) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.clientRepository = clientRepository;
    }

    public FinancialSummaryDTO getFullReport(LocalDate start, LocalDate end) {

        BigDecimal received = paymentRepository.sumApprovedPaymentsByPeriod(
                start.atStartOfDay(),
                end.atTime(23, 59, 59)
        );
        BigDecimal invoiced = invoiceRepository.sumInvoicesByPeriod(start, end);
        BigDecimal overdue = invoiceRepository.sumOverdueByPeriod(start, end);

        Long totalInvoices = invoiceRepository.countByPeriod(start, end);

        Long paid = invoiceRepository.countByStatusAndPeriod(PAID, start, end);
        Long pending = invoiceRepository.countByStatusAndPeriod(PENDING, start, end);
        Long overdueCount = invoiceRepository.countByStatusAndPeriod(OVERDUE, start, end);

        BigDecimal pendingValue = invoiced.subtract(received);

        return new FinancialSummaryDTO(
                start,
                end,
                invoiced,
                received,
                pendingValue,
                overdue,
                totalInvoices,
                paid,
                pending,
                overdueCount,
                clientRepository.countDefaulters()
        );
    }
}
