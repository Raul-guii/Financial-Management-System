package com.raul.backend.dto.client;

import com.raul.backend.enums.ClientType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    private String email;
    private String phone;

    @Size(max = 150)
    private String addressStreet;

    @Size(max = 20)
    private String addressNumber;

    @Size(max = 100)
    private String addressNeighborhood;

    @Size(max = 100)
    private String addressCity;

    @Size(max = 50)
    private String addressState;

    @Size(max = 20)
    private String addressPostalCode;

    @Size(max = 100)
    private String addressCountry;
}
