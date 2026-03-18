package com.raul.backend.dto.auth;

import com.raul.backend.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;
    private String type;
    private Long userId;
    private String name;
    private String email;
    private Roles role;
}
