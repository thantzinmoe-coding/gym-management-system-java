package org._java_proj.gym_management_system.features.assignedSchedule.service;

import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.features.assignedSchedule.dto.request.AssignedScheduleRequest;
import org.springframework.security.access.prepost.PreAuthorize;

public interface AssignedScheduleService {
    @PreAuthorize("hasRole('ROLE_TRAINER')")
    ApiResponse assignedSchedule(AssignedScheduleRequest request);

    @PreAuthorize("hasRole('ROLE_TRAINER')")
    ApiResponse unassignedSchedule(Long trainerID);
}
