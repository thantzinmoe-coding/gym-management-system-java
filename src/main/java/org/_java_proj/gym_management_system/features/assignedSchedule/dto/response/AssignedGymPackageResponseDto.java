package org._java_proj.gym_management_system.features.assignedSchedule.dto.response;

import lombok.Data;

@Data
public class AssignedGymPackageResponseDto {
    private Long id;

    private Long trainerID;

    private Long scheduleID;
}
