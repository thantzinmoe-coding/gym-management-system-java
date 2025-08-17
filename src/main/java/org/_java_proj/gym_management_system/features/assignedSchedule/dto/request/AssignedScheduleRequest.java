package org._java_proj.gym_management_system.features.assignedSchedule.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssignedScheduleRequest {
    @NotBlank(message = "Trainer id is required")
    private Long trainerID;

    @NotBlank(message = "Schedule id is required")
    private Long scheduleID;
}
