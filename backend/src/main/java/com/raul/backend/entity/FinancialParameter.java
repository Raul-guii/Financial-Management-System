package com.raul.backend.entity;

import com.raul.backend.config.auditable.SoftDeletable;
import com.raul.backend.enums.FinancialParameterCategory;
import com.raul.backend.enums.FinancialParameterType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "financial_parameters")
public class FinancialParameter extends SoftDeletable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FinancialParameterType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FinancialParameterCategory category;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal value;

    @Column(nullable = false, length = 254)
    private String description;

    private Boolean active;

    @ManyToOne(optional = false)
    @JoinColumn(name = "updated_by", nullable = false)
    private User updatedBy;

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
}
