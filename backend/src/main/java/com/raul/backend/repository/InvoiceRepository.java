package com.raul.backend.repository;

import ch.qos.logback.core.status.Status;
import com.raul.backend.entity.Invoice;
import com.raul.backend.enums.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByStatus(InvoiceStatus status);
    List<Invoice> findByDueDateBeforeAndStatusNot(LocalDate date, InvoiceStatus status);
    boolean existsByContractIdAndIssueDateBetweenAndStatusNot(Long contractId, LocalDate start, LocalDate end, InvoiceStatus status);
    List<Invoice> findByStatusIn(List<InvoiceStatus> statuses);
    Page<Invoice> findByDeletedAtIsNull(Pageable pageable);
    Page<Invoice> findByContractIdAndDeletedAtIsNull(Long contractId, Pageable pageable);
    Page<Invoice> findByStatusAndDeletedAtIsNull(InvoiceStatus status, Pageable pageable);

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

    @Query("""
    SELECT COUNT(i)
    FROM Invoice i
    WHERE i.status = :status
      AND i.issueDate BETWEEN :start AND :end
    """)
    Long countByStatusAndPeriod(InvoiceStatus status, LocalDate start, LocalDate end);

    @Query("""
    SELECT i
    FROM Invoice i
    JOIN FETCH i.contract c
    JOIN FETCH c.createdBy
    WHERE i.status = 'PENDING'
    AND i.dueDate BETWEEN :today AND :limit
    """)
    List<Invoice> findUpcomingDueInvoices(LocalDate today, LocalDate limit);

    @Query("""
    SELECT COALESCE(SUM(i.amount), 0)
    FROM Invoice i
    WHERE i.status = 'PENDING'
      AND i.dueDate BETWEEN :start AND :end
    """)
    BigDecimal sumPendingByPeriod(LocalDate start, LocalDate end);

    @Query("SELECT i FROM Invoice i LEFT JOIN FETCH i.payment WHERE i.id = :id")
    Optional<Invoice> findByIdWithPayments(Long id);
}
