package com.raul.backend.service;

import com.raul.backend.entity.Invoice;
import com.raul.backend.entity.Payment;
import com.raul.backend.enums.PaymentStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class InvoiceCalculatorService {

    public BigDecimal getTotalPaid(Invoice invoice) {
        return invoice.getPayment()
                .stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.APPROVED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalWithFees(Invoice invoice) {
        return invoice.getOriginalAmount()
                .add(invoice.getInterestAmount())
                .add(invoice.getLateFreeAmount());
    }

    public BigDecimal getRemainingAmount(Invoice invoice) {
        return getTotalWithFees(invoice)
                .subtract(getTotalPaid(invoice));
    }
}
