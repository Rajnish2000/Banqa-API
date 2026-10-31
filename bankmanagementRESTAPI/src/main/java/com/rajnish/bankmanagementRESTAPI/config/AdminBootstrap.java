package com.rajnish.bankmanagementRESTAPI.config;

import com.rajnish.bankmanagementRESTAPI.entities.User;
import com.rajnish.bankmanagementRESTAPI.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class AdminBootstrap implements ApplicationRunner {

    private final Environment environment;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        String email = environment.getProperty("ADMIN_EMAIL", "").trim().toLowerCase(Locale.ROOT);
        String password = environment.getProperty("ADMIN_PASSWORD", "");
        if (email.isEmpty() && password.isEmpty()) {
            return;
        }
        if (email.isEmpty() || password.length() < 12) {
            throw new IllegalStateException(
                    "Set both ADMIN_EMAIL and an ADMIN_PASSWORD of at least 12 characters to bootstrap an administrator.");
        }
        User existingUser = userRepository.findByEmailIgnoreCase(email).orElse(null);
        if (existingUser != null && existingUser.getRole() == User.UserRole.ADMIN) {
            return;
        }
        if (existingUser != null) {
            throw new IllegalStateException(
                    "ADMIN_EMAIL is already registered as a regular user; refusing to elevate that account.");
        }
        userRepository.save(User.builder()
                .name("Administrator")
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(User.UserRole.ADMIN)
                .emailVerified(true)
                .build());
    }
}
