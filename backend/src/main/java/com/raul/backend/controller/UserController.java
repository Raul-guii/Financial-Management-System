package com.raul.backend.controller;

import com.raul.backend.dto.user.UserCreateDTO;
import com.raul.backend.dto.user.UserResponseDTO;
import com.raul.backend.dto.user.UserUpdateDTO;
import com.raul.backend.entity.User;
import com.raul.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> create(@RequestBody @Valid UserCreateDTO dto) {
        UserResponseDTO user = userService.createUser(dto);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long id, @RequestBody @Valid UserUpdateDTO dto
    ) {
        UserResponseDTO user = userService.updateUser(id, dto);
        return ResponseEntity.ok(user);
    }
}