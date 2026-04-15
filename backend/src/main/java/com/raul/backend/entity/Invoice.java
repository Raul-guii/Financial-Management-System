package com.raul.backend.entity;

import com.raul.backend.config.auditable.Auditable;
import com.raul.backend.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invoices")
public class Invoice extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvoiceStatus status;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "due_day", nullable = false)
    private LocalDate dueDay;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "original_amount",nullable = false)
    private BigDecimal originalAmount;

    @Column(name = "late_free_amount", nullable = false)
    private BigDecimal lateFreeAmount;

    @Column(name = "interest_amount", nullable = false)
    private BigDecimal interestAmount;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @OneToMany(mappedBy = "invoice")
    private List<Payment> payment;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceLine> invoiceLines = new ArrayList<>();
}
