package org._java_proj.gym_management_system.features.message.repository;

import org._java_proj.gym_management_system.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
}
