package com.raul.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reconciliations")
public class Reconciliation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;

    @ManyToOne
    @JoinColumn(name = "executed_by")
    private User executedBy;

    @OneToMany(mappedBy = "reconciliation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReconciliationItem> items = new ArrayList<>();
}
