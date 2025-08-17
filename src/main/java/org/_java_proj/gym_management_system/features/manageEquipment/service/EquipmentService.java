package org._java_proj.gym_management_system.features.manageEquipment.service;

import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org._java_proj.gym_management_system.features.manageEquipment.dto.request.EquipmentCreateRequest;
import org._java_proj.gym_management_system.features.manageEquipment.dto.request.EquipmentUpdateRequest;
import org._java_proj.gym_management_system.features.manageEquipment.dto.response.EquipmentResponseDto;
import org.springframework.data.domain.Pageable;

public interface EquipmentService {
    ApiResponse createEquipment(EquipmentCreateRequest request);
    ApiResponse getEquipment(Long id);
    PaginatedApiResponse<EquipmentResponseDto> listEquipments(Pageable pageable);
    ApiResponse updateEquipment(Long id, EquipmentUpdateRequest request);
    ApiResponse deleteEquipment(Long id);
}