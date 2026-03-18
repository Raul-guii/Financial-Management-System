package com.raul.backend.controller;

import com.raul.backend.dto.user.UserCreateDTO;
import com.raul.backend.dto.user.UserUpdateDTO;
import com.raul.backend.entity.User;
import com.raul.backend.service.UserService;
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
    public User create(@RequestBody UserCreateDTO dto) {
        return userService.createUser(dto);
    }

    @PutMapping("/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public User update(@PathVariable String email,
                       @RequestBody UserUpdateDTO dto) {
        return userService.updateUser(email, dto);
    }
}
