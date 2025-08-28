package org._java_proj.gym_management_system.features.assignedSchedule.repository;

import jakarta.validation.constraints.NotBlank;
import org._java_proj.gym_management_system.common.constant.Status;
import org._java_proj.gym_management_system.model.AssignedGymPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssignedGymPackageRepository extends JpaRepository<AssignedGymPackage, Long> {

    AssignedGymPackage findByTrainerIdAndStatus(Long trainerID, Status status);

    boolean existsByTrainerIdAndStatus(@NotBlank(message = "Trainer id is required") Long trainerID, Status status);

    boolean existsByAssignedGymPackageIdAndStatus(@NotBlank(message = "Schedule id is required") Long scheduleID, Status status);

    Optional<AssignedGymPackage> findByTrainerIdAndAssignedGymPackageIdAndStatus(@NotBlank(message = "Trainer id is required") Long trainerID, @NotBlank(message = "Schedule id is required") Long scheduleID, Status status);
}
