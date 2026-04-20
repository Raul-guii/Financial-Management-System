package com.raul.backend.service;

import com.raul.backend.dto.dashboard.DashboardSummaryDTO;
import com.raul.backend.enums.InvoiceStatus;
import com.raul.backend.repository.InvoiceRepository;
import com.raul.backend.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DashboardService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    public DashboardService(PaymentRepository paymentRepository,
                            InvoiceRepository invoiceRepository) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public DashboardSummaryDTO getSummary() {

        BigDecimal grossRevenue = paymentRepository.sumApprovedPayments();
        BigDecimal refunded = paymentRepository.sumRefundedPayments();
        BigDecimal netRevenue = grossRevenue.subtract(refunded);

        BigDecimal totalPending = invoiceRepository.sumPendingInvoices();

        Long paidInvoices = invoiceRepository.countByStatus(InvoiceStatus.PAID);
        Long pendingInvoices = invoiceRepository.countByStatus(InvoiceStatus.PENDING);
        Long overdueInvoices = invoiceRepository.countByStatus(InvoiceStatus.OVERDUE);

        System.out.println(invoiceRepository.findByStatus(InvoiceStatus.PENDING).size());
        System.out.println(invoiceRepository.findByStatus(InvoiceStatus.OVERDUE).size());

        return new DashboardSummaryDTO(
                grossRevenue,
                refunded,
                netRevenue,
                totalPending,
                paidInvoices,
                pendingInvoices,
                overdueInvoices
        );
    }
}