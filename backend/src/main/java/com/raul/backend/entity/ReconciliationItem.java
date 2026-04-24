package com.raul.backend.entity;

import com.raul.backend.enums.GatewayStatus;
import com.raul.backend.enums.ReconciliationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reconciliation_items")
public class ReconciliationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "system_amount", nullable = false)
    private BigDecimal systemAmount;

    @Column(name = "gateway_amount", nullable = false)
    private BigDecimal gatewayAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReconciliationStatus status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reconciliation_id", nullable = false)
    private Reconciliation reconciliation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne(optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @ManyToOne(optional = false)
    @JoinColumn(name = "gateway_transaction_id")
    private GatewayTransaction gatewayTransaction;
}
