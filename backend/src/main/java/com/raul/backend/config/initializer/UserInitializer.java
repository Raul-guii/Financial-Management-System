package com.raul.backend.config.initializer;

import com.raul.backend.entity.User;
import com.raul.backend.enums.Roles;
import com.raul.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@Profile("dev")
@RequiredArgsConstructor
public class UserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (userRepository.existsByEmail("admin@sgf.com")) {
            return;
        }

        User admin = new User();

        admin.setName("Administrador");
        admin.setEmail("admin@sgf.com");
        admin.setPassword(passwordEncoder.encode("12345678"));
        admin.setRole(Roles.ADMIN);

        userRepository.save(admin);

        System.out.println("Admin criado.");
    }
}
