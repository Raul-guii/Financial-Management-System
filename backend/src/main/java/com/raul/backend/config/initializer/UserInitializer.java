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
        if (userRepository.count() > 0) return;

        save("Administrador", "admin@sgf.com", Roles.ADMIN);

        for (int i = 1; i <= 33; i++) {
            save("Admin " + i, "admin" + i + "@sgf.com", Roles.ADMIN);
        }

        for (int i = 1; i <= 33; i++) {
            save("Gestor " + i, "gestor" + i + "@sgf.com", Roles.FINANCIAL_MANAGER);
        }

        for (int i = 1; i <= 33; i++) {
            save("Analista " + i, "analista" + i + "@sgf.com", Roles.FINANCIAL_ANALYST);
        }

        System.out.println("100 usuários criados.");
    }

    private void save(String name, String email, Roles role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("12345678"));
        user.setRole(role);
        userRepository.save(user);
    }
}

