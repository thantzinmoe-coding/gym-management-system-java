package org._java_proj.gym_management_system.features.users.repository;

import org._java_proj.gym_management_system.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String customer);
    Optional<Role> findById(long id);
}
