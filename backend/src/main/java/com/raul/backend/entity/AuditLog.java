package com.raul.backend.entity;

import com.raul.backend.config.auditable.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit_logs")
public class AuditLog extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @NotBlank
    @Column(nullable = false, length = 254)
    private String action;

    @NotBlank
    @Column(name = "entity_type", nullable = false, length = 100)
    private String entityType;

    @Column(name = "user_id")
    private Long userId;
}
