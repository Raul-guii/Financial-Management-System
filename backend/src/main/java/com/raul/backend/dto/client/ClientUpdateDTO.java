package com.raul.backend.dto.client;

import com.raul.backend.enums.ClientType;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientUpdateDTO {

    private String name;
    private ClientType type;
    private String document;

    @Email
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
}
