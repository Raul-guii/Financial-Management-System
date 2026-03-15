package com.raul.backend.dto.client;

import com.raul.backend.enums.ClientType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientCreateDTO {

    @NotBlank
    private String name;

    @NotNull
    private ClientType type;

    @NotBlank
    private String document;

    @NotBlank
    @Email
    private String email;

    @NotBlank
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
