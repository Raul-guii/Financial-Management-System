package com.raul.backend.repository;

import ch.qos.logback.core.status.Status;
import com.raul.backend.entity.Invoice;
import com.raul.backend.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByContractId(Long contractId);
    List<Invoice> findByStatusAndDueDateBefore(InvoiceStatus status, LocalDate date);
    List<Invoice> findByStatus(InvoiceStatus status);
    List<Invoice> findByDueDateBeforeAndStatusNot(LocalDate date, InvoiceStatus status);

    @Query("""
    SELECT COALESCE(SUM(i.amount), 0)
    FROM Invoice i
    WHERE i.issueDate BETWEEN :start AND :end
    """)
    BigDecimal sumInvoicesByPeriod(LocalDate start, LocalDate end);

    @Query("""
    SELECT COALESCE(SUM(i.amount), 0)
    FROM Invoice i
    WHERE i.status = 'PENDING'
    """)
    BigDecimal sumPendingInvoices();

    @Query("""
    SELECT COUNT(i)
    FROM Invoice i
    WHERE i.status = :status
    """)
    Long countByStatus(InvoiceStatus status);

    @Query("""
    SELECT COALESCE(SUM(i.amount), 0)
    FROM Invoice i
    WHERE i.dueDate < CURRENT_DATE
      AND i.status <> 'PAID'
      AND i.dueDate BETWEEN :start AND :end
    """)
    BigDecimal sumOverdueByPeriod(LocalDate start, LocalDate end);

    @Query("""
    SELECT COUNT(i)
    FROM Invoice i
    WHERE i.issueDate BETWEEN :start AND :end
    """)
    Long countByPeriod(LocalDate start, LocalDate end);
}
