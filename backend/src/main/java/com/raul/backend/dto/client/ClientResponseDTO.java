package com.raul.backend.dto.client;

import com.raul.backend.enums.ClientType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponseDTO {

    private Long id;
    private String name;
    private ClientType type;
    private String document;
    private String email;
    private String phone;
    private String addressStreet;
    private String addressNumber;
    private String addressNeighborhood;
    private String addressCity;
    private String addressState;
    private String addressPostalCode;
    private String addressCountry;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
}
