package org._java_proj.gym_management_system.features.manageEquipment.repository;

import org._java_proj.gym_management_system.model.Equipment;
import org._java_proj.gym_management_system.model.GymPackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    @Query("SELECT e FROM Equipment e")
    Page<Equipment> getAllEquipments(Pageable pageable);
}
