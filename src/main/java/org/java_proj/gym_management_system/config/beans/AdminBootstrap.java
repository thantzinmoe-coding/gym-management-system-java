package org.java_proj.gym_management_system.config.beans;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.java_proj.gym_management_system.features.users.repository.RoleRepository;
import org.java_proj.gym_management_system.features.users.repository.UserRepository;
import org.java_proj.gym_management_system.model.Role;
import org.java_proj.gym_management_system.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates the first administrator from {@code BOOTSTRAP_ADMIN_EMAIL} / {@code BOOTSTRAP_ADMIN_PASSWORD}.
 *
 * <p>Public sign-up can only create members and trainers, so a new deployment needs another way
 * to get its first admin. This runs at startup, does nothing unless both variables are set, and
 * does nothing once any admin exists, so leaving the variables in place is harmless (but remove
 * them after the first sign-in). The password is never logged.
 */
@Slf4j
@Component
@Order(2) // after RoleSeeder
@RequiredArgsConstructor
public class AdminBootstrap implements ApplicationRunner {

    private static final int MIN_PASSWORD_LENGTH = 12;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap-admin.email:}")
    private String email;

    @Value("${app.bootstrap-admin.password:}")
    private String password;

    @Override
    @Transactional
    public void run(final ApplicationArguments args) {
        if (email.isBlank() && password.isBlank()) {
            return;
        }
        if (email.isBlank() || password.isBlank()) {
            log.warn("Admin bootstrap skipped: set both BOOTSTRAP_ADMIN_EMAIL and BOOTSTRAP_ADMIN_PASSWORD.");
            return;
        }
        if (!userRepository.findByRoleName("ADMIN", PageRequest.of(0, 1)).isEmpty()) {
            log.info("Admin bootstrap skipped: an administrator already exists. "
                    + "You can remove BOOTSTRAP_ADMIN_EMAIL and BOOTSTRAP_ADMIN_PASSWORD.");
            return;
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalStateException(
                    "BOOTSTRAP_ADMIN_PASSWORD must be at least " + MIN_PASSWORD_LENGTH + " characters.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException(
                    "BOOTSTRAP_ADMIN_EMAIL belongs to an existing non-admin account; use a different address.");
        }
        final Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role is missing."));

        final User admin = new User();
        admin.setEmail(email.trim());
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole(adminRole);
        userRepository.save(admin);
        log.info("Created the first administrator account: {}", email.trim());
    }
}
