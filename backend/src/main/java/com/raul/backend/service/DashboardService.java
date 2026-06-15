package com.raul.backend.service;

import com.raul.backend.dto.dashboard.DashboardSummaryDTO;
import com.raul.backend.dto.dashboard.MonthlyRevenueDTO;
import com.raul.backend.enums.InvoiceStatus;
import com.raul.backend.repository.ClientRepository;
import com.raul.backend.repository.InvoiceRepository;
import com.raul.backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class DashboardService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;
    private final InvoiceCalculatorService invoiceCalculatorService;

    public DashboardSummaryDTO getSummary() {

        BigDecimal grossRevenue = paymentRepository.sumAllSuccessfulPayments();
        BigDecimal refunded = paymentRepository.sumRefundedPayments();
        BigDecimal netRevenue = grossRevenue.subtract(refunded);

        BigDecimal totalPending = invoiceRepository.findByStatus(InvoiceStatus.PENDING)
                .stream()
                .map(invoiceCalculatorService::getRemainingAmountCapped)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPartiallyPaid = invoiceRepository.findByStatus(InvoiceStatus.PARTIALLY_PAID)
                .stream()
                .map(invoiceCalculatorService::getRemainingAmountCapped)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalPending = totalPending.add(totalPartiallyPaid);

        Long paidInvoices = invoiceRepository.countByStatus(InvoiceStatus.PAID);
        Long pendingInvoices = invoiceRepository.countByStatus(InvoiceStatus.PENDING);
        Long overdueInvoices = invoiceRepository.countByStatus(InvoiceStatus.OVERDUE);
        Long clientDefaulters = clientRepository.countDefaulters();

        return new DashboardSummaryDTO(
                grossRevenue,
                refunded,
                netRevenue,
                totalPending,
                paidInvoices,
                pendingInvoices,
                overdueInvoices,
                clientDefaulters
        );
    }

    public List<MonthlyRevenueDTO> getMonthlyRevenue(LocalDate startDate, LocalDate endDate) {
        List<MonthlyRevenueDTO> result = new ArrayList<>();
        String[] monthNames = {"Jan","Fev","Mar","Abr","Mai","Jun","Jul","Ago","Set","Out","Nov","Dez"};

        LocalDate ref = startDate.withDayOfMonth(1);
        while (!ref.isAfter(endDate.withDayOfMonth(1))) {
            LocalDate start = ref.withDayOfMonth(1);
            LocalDate end   = ref.withDayOfMonth(ref.lengthOfMonth());

            BigDecimal revenue = paymentRepository.sumApprovedByPeriod(
                    start.atStartOfDay(), end.atTime(23, 59, 59)
            );
            BigDecimal pending  = invoiceRepository.sumPendingByPeriod(start, end);
            BigDecimal overdue  = invoiceRepository.sumOverdueByPeriod(start, end);

            result.add(new MonthlyRevenueDTO(monthNames[ref.getMonthValue() - 1], revenue, pending, overdue));
            ref = ref.plusMonths(1);
        }

        return result;
    }
}