package com.raul.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long entity_id;

    @NotBlank
    @Column(nullable = false, length = 254)
    private String action;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String entity_type;

    @Column(nullable = false)
    private LocalDateTime time_stamp;

    @ManyToOne(optional = true)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
