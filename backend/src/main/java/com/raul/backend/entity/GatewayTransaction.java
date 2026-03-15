package com.raul.backend.entity;

import com.raul.backend.auditable.Auditable;
import com.raul.backend.enums.GatewayStatus;
import com.raul.backend.enums.InvoiceStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "gateway_transactions")
public class GatewayTransaction extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GatewayStatus status;

    @NotBlank
    @Column(name = "external_id", nullable = false, unique = true, length = 254)
    private String externalId;

    @NotBlank
    @Column(name = "gateway_name", nullable = false, length = 254)
    private String gatewayName;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "raw_response", nullable = false, length = 254)
    private String rawResponse;

    @OneToOne
    @JoinColumn(name = "payment_id", nullable = false, unique = true)
    private Payment payment;
}
