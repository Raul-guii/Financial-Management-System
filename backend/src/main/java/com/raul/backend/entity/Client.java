package com.raul.backend.entity;

import com.raul.backend.config.auditable.SoftDeletable;
import com.raul.backend.enums.ClientType;
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
@Table(name = "clients")
public class Client extends SoftDeletable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ClientType type;

    @Column(nullable = false)
    private Boolean defaulter = false;

    @NotBlank
    @Column(nullable = false, length = 20)
    private String document;

    @NotBlank
    @Column(length = 150)
    private String email;

    @NotBlank
    @Column(length = 20)
    private String phone;

    @Column(name = "address_street", length = 150)
    private String addressStreet;

    @Column(name = "address_number", length = 20)
    private String addressNumber;

    @Column(name = "address_neighborhood", length = 100)
    private String addressNeighborhood;

    @Column(name = "address_city", length = 100)
    private String addressCity;

    @Column(name = "address_state", length = 50)
    private String addressState;

    @Column(name = "address_postal_code", length = 20)
    private String addressPostalCode;

    @Column(name = "address_country", length = 100)
    private String addressCountry;

    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

}
