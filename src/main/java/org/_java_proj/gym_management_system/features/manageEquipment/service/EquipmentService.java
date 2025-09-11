package org._java_proj.gym_management_system.features.manageEquipment.service;

import jakarta.transaction.Transactional;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org._java_proj.gym_management_system.features.manageEquipment.dto.request.EquipmentCreateRequest;
import org._java_proj.gym_management_system.features.manageEquipment.dto.request.EquipmentUpdateRequest;
import org._java_proj.gym_management_system.features.manageEquipment.dto.response.EquipmentResponseDto;
import org.springframework.data.domain.Pageable;

public interface EquipmentService {
    @Transactional
    ApiResponse createEquipment(EquipmentCreateRequest request);

    ApiResponse getEquipment(String id);

    PaginatedApiResponse<EquipmentResponseDto> listEquipments(Pageable pageable);

    @Transactional
    ApiResponse updateEquipment(String id, EquipmentUpdateRequest request);

    @Transactional
    ApiResponse deleteEquipment(String id);
}