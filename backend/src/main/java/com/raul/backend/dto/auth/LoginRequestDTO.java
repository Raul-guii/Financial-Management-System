package com.raul.backend.dto.auth;

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
public class LoginRequestDTO {

    @Email
    @NotBlank
    @NotNull
    @Size(max = 150)
    private String email;

    @NotBlank
    @Size(max = 254)
    private String password;
}
