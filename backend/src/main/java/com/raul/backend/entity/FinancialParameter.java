package com.raul.backend.entity;

import com.raul.backend.auditable.SoftDeletable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contracts")
public class FinancialParameter extends SoftDeletable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private String type;

    @Column(nullable = false, length = 100)
    private String key;

    @Column(nullable = false, length = 254)
    private String value;

    @Column(nullable = false, length = 254)
    private String description;

    private Boolean active;

    @ManyToOne(optional = true)
    @JoinColumn(name = "updated_by", nullable = false)
    private User updatedBy;

    @ManyToOne(optional = true)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
}
