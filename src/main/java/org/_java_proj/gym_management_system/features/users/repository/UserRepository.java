package org._java_proj.gym_management_system.features.users.repository;

import org._java_proj.gym_management_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
