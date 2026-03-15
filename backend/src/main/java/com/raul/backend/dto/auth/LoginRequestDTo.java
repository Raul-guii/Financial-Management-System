package com.raul.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTo {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
