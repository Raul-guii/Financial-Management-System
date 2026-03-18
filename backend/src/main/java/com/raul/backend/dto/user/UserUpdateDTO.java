package com.raul.backend.dto.user;

import com.raul.backend.enums.Roles;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    private String name;

    @Email
    private String email;

    private String password;
    private Roles role;
}
