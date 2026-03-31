package com.raul.backend.repository;

import com.raul.backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("""
    SELECT COALESCE(SUM(p.amount), 0)
    FROM Payment p
    WHERE p.paymentStatus = 'APPROVED'
    AND p.paymentDate BETWEEN :start AND :end
    """)
    BigDecimal sumApprovedPaymentsByPeriod(LocalDateTime start, LocalDateTime end);

    @Query("""
    SELECT COALESCE(SUM(p.amount), 0)
    FROM Payment p
    WHERE p.invoice.id = :invoiceId
    AND p.paymentStatus = 'APPROVED'
""")
    BigDecimal sumApprovedByInvoice(Long invoiceId);
}
