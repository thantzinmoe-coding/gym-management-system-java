package org.java_proj.gym_management_system.features.assignedGymPackage.service;

import org.java_proj.gym_management_system.config.response.dto.ApiResponse;
import org.java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org.java_proj.gym_management_system.features.assignedGymPackage.dto.request.AssignedGymPackageRequest;
import org.java_proj.gym_management_system.features.assignedGymPackage.dto.response.TrainerPackage;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

public interface AssignedGymPackageService {
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    ApiResponse assignedGymPackage(AssignedGymPackageRequest request);

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    ApiResponse unassignedGymPackage(Long trainerID, Long packageId);

    ApiResponse updateAssign(Long trainerId, Long packageId);

    PaginatedApiResponse<TrainerPackage> getAssignedPackagesByTrainer(Long trainerId, Pageable pageable);
}
