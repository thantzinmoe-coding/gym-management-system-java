package org._java_proj.gym_management_system.features.assignedGymPackage.service;

import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.features.assignedGymPackage.dto.request.AssignedGymPackageRequest;
import org.springframework.security.access.prepost.PreAuthorize;

public interface AssignedGymPackageService {
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    ApiResponse assignedGymPackage(AssignedGymPackageRequest request);

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    ApiResponse unassignedGymPackage(Long trainerID, Long packageId);

    ApiResponse updateAssign(Long trainerId, Long packageId);
}
