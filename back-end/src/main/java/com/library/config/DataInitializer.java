package com.library.config;

import com.library.entity.Role;
import com.library.entity.User;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initializeUsers() {

        return args -> {

            if (userRepository.count() > 0) {
                return;
            }

            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .enabled(true)
                    .mustChangePassword(true)
                    .build();

            userRepository.save(admin);
        };
    }
}