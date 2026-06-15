package com.raul.backend.controller;

import com.raul.backend.dto.user.UserCreateDTO;
import com.raul.backend.dto.user.UserResponseDTO;
import com.raul.backend.dto.user.UserUpdateDTO;
import com.raul.backend.entity.User;
import com.raul.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDTO>> findAll(
            @PageableDefault(size = 20, sort = "name") Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role) {
        return ResponseEntity.ok(userService.findAll(pageable, search, role));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}