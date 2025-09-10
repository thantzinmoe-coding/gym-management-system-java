package org._java_proj.gym_management_system.features.managePackage.service;

import org._java_proj.gym_management_system.common.constant.GymPackageType;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org._java_proj.gym_management_system.features.managePackage.dto.request.GymPackageCreateRequest;
import org._java_proj.gym_management_system.features.managePackage.dto.request.GymPackageUpdateRequest;
import org._java_proj.gym_management_system.features.managePackage.dto.response.GymPackageResponseDto;
import org.springframework.data.domain.Pageable;

public interface GymPackageService {
    ApiResponse createGymPackage(GymPackageCreateRequest request);

    ApiResponse getGymPackageById(Long gymPackageId);

    PaginatedApiResponse<GymPackageResponseDto> getAllGymPackages(Pageable pageable);

    ApiResponse updateGymPackage(Long id, GymPackageUpdateRequest request);

    ApiResponse deleteGymPackage(Long id);

    PaginatedApiResponse<GymPackageResponseDto> getGymPackagesByType(GymPackageType type, Pageable pageable);

}
