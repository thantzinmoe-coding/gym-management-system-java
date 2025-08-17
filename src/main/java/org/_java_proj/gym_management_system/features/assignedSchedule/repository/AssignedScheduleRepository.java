package org._java_proj.gym_management_system.features.assignedSchedule.repository;

import jakarta.validation.constraints.NotBlank;
import org._java_proj.gym_management_system.common.constant.Status;
import org._java_proj.gym_management_system.model.AssignedGymSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssignedScheduleRepository extends JpaRepository<AssignedGymSchedule, Long> {

    AssignedGymSchedule findByTrainerIdAndStatus(Long trainerID, Status status);

    boolean existsByTrainerIdAndStatus(@NotBlank(message = "Trainer id is required") Long trainerID, Status status);

    boolean existsByAssignedGymScheduleIdAndStatus(@NotBlank(message = "Schedule id is required") Long scheduleID, Status status);

    Optional<AssignedGymSchedule> findByTrainerIdAndAssignedGymScheduleIdAndStatus(@NotBlank(message = "Trainer id is required") Long trainerID, @NotBlank(message = "Schedule id is required") Long scheduleID, Status status);
}
