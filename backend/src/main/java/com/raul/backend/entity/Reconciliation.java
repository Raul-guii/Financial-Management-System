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

    @Column(nullable = false)
    private LocalDate periodStart;

    @Column(nullable = false)
    private LocalDate periodEnd;

    @Column(nullable = false)
    private LocalDateTime executedAt;

    @ManyToOne
    @JoinColumn(name = "executed_by")
    private User executed_by;

    @OneToMany(mappedBy = "reconciliations", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReconciliationItem> items = new ArrayList<>();
}
