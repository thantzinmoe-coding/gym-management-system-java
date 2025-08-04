package org._java_proj.gym_management_system.features.users.repository;

import org._java_proj.gym_management_system.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String customer);
    Optional<Role> findById(long id);
}
