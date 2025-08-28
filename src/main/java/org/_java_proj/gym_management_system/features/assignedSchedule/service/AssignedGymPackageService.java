package org._java_proj.gym_management_system.features.assignedSchedule.service;

import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.features.assignedSchedule.dto.request.AssignedGymPackageRequest;
import org.springframework.security.access.prepost.PreAuthorize;

public interface AssignedGymPackageService {
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    ApiResponse assignedGymPackage(AssignedGymPackageRequest request);

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    ApiResponse unassignedGymPackage(Long trainerID);
}
