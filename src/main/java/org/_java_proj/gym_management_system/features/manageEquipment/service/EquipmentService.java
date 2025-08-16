package org._java_proj.gym_management_system.features.manageEquipment.service;

import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.features.manageEquipment.dto.request.EquipmentCreateRequest;
import org._java_proj.gym_management_system.features.manageEquipment.dto.request.EquipmentUpdateRequest;

public interface EquipmentService {
    ApiResponse createEquipment(EquipmentCreateRequest request);
    ApiResponse getEquipment(Long id);
    ApiResponse listEquipments();
    ApiResponse updateEquipment(Long id, EquipmentUpdateRequest request);
    ApiResponse deleteEquipment(Long id);
}