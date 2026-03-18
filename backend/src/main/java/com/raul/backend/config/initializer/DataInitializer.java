package com.raul.backend.config.initializer;

import com.raul.backend.entity.User;
import com.raul.backend.enums.Roles;
import com.raul.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Profile("dev")
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository,
                                PasswordEncoder passwordEncoder) {

        return args -> {

            System.out.println("Inicializando usuário admin...");

            userRepository.findByEmail("admin@sgf.com")
                    .orElseGet(() -> {

                        User admin = new User();

                        admin.setName("Admin");
                        admin.setEmail("admin@sgf.com");
                        admin.setPassword(passwordEncoder.encode("12345678"));
                        admin.setRole(Roles.ADMIN);

                        return userRepository.save(admin);
                    });

        };
    }
}
