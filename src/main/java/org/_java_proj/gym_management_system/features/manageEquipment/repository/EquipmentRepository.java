package org._java_proj.gym_management_system.features.manageEquipment.repository;

import org._java_proj.gym_management_system.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
}
