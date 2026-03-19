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
public class ClientCreateDTO {

    @NotBlank
    @NotNull
    @Size(max = 150)
    private String name;

    @NotNull
    private ClientType type;

    @NotBlank
    @NotNull
    @Size(max = 20)
    private String document;

    @NotBlank
    @NotNull
    @Email
    @Size(max = 150)
    private String email;

    @NotBlank
    @NotNull
    @Size(max = 20)
    private String phone;

    @NotBlank
    @Size(max = 150)
    private String addressStreet;

    @NotBlank
    @Size(max = 20)
    private String addressNumber;

    @NotBlank
    @Size(max = 100)
    private String addressNeighborhood;

    @NotBlank
    @Size(max = 100)
    private String addressCity;

    @NotBlank
    @Size(max = 50)
    private String addressState;

    @NotBlank
    @Size(max = 20)
    private String addressPostalCode;

    @NotBlank
    @Size(max = 100)
    private String addressCountry;

    private Boolean active;
}
