package com.smartcampus.backend.config;

import com.smartcampus.backend.entity.User;
import com.smartcampus.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Cree le compte admin au demarrage s'il n'existe pas encore.
 * Idempotent : ne fait rien si un utilisateur avec ce username OU cet email existe deja.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        boolean exists = userRepository.existsByUsername(adminUsername)
                || userRepository.existsByEmail(adminEmail);

        if (exists) {
            log.info("Compte admin deja present, aucune action");
            return;
        }

        User admin = User.builder()
                .username(adminUsername)
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .role(User.Role.ADMIN)
                .build();

        userRepository.save(admin);
        log.info("Compte admin cree avec succes : {}", adminUsername);
    }
}