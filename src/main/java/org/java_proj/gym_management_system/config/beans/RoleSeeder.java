package org.java_proj.gym_management_system.config.beans;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.java_proj.gym_management_system.features.users.repository.RoleRepository;
import org.java_proj.gym_management_system.model.Role;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Inserts the roles registration depends on when they are missing, so a fresh
 * database works without running V2__Insert_Roles.sql by hand. Idempotent.
 */
@Slf4j
@Component
@Order(1) // roles must exist before AdminBootstrap runs
@RequiredArgsConstructor
public class RoleSeeder implements ApplicationRunner {

    private static final List<String> ROLES = List.of("MEMBER", "TRAINER", "ADMIN");

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(final ApplicationArguments args) {
        for (final String name : ROLES) {
            if (roleRepository.findByName(name).isEmpty()) {
                roleRepository.save(new Role(name));
                log.info("Seeded missing role {}", name);
            }
        }
    }
}
