package org._java_proj.gym_management_system.features.manageEquipment.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org._java_proj.gym_management_system.common.constant.Condition;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.features.manageEquipment.dto.request.EquipmentCreateRequest;
import org._java_proj.gym_management_system.features.manageEquipment.dto.request.EquipmentUpdateRequest;
import org._java_proj.gym_management_system.features.manageEquipment.dto.response.EquipmentResponseDto;
import org._java_proj.gym_management_system.features.manageEquipment.repository.EquipmentRepository;
import org._java_proj.gym_management_system.features.manageEquipment.service.EquipmentService;
import org._java_proj.gym_management_system.model.Equipment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;

    @Override
    @Transactional
    public ApiResponse createEquipment(EquipmentCreateRequest request) {
        Equipment equipment = new Equipment();
        equipment.setName(request.getName());
        equipment.setPurchase_date(request.getPurchaseDate());

        // *** Update the setter call to use the new field name ***
        equipment.setEquipmentCondition(convertIntegerToCondition(request.getEquipmentCondition())); // Changed line

        equipment.setLastMaintenanceDate(request.getLastMaintenanceDate());
        equipment.setNextMaintenanceDate(request.getNextMaintenanceDate());

        equipmentRepository.save(equipment);

        EquipmentResponseDto dto = mapToDto(equipment);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.CREATED.value())
                .data(Map.of("equipment", dto))
                .message("Equipment created successfully.")
                .build();
    }


    @Override
    public ApiResponse getEquipment(Long id) {
        Equipment eq = equipmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipment not found"));
        EquipmentResponseDto dto = mapToDto(eq);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of("equipment", dto))
                .message("Equipment fetched.")
                .build();
    }

    @Override
    public ApiResponse listEquipments() {
        List<EquipmentResponseDto> dtos = equipmentRepository.findAll()
                .stream().map(this::mapToDto).toList();

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of("equipments", dtos))
                .message("All equipment entries fetched.")
                .build();
    }

    @Override
    @Transactional
    public ApiResponse updateEquipment(Long id, EquipmentUpdateRequest request) {
        Equipment eq = equipmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipment not found"));

        if (request.getName() != null) eq.setName(request.getName());
        if (request.getPurchaseDate() != null) eq.setPurchase_date(request.getPurchaseDate());

        // *** Update the setter call to use the new field name ***
        if (request.getEquipmentCondition() != null) {
            eq.setEquipmentCondition(convertIntegerToCondition(request.getEquipmentCondition())); // Changed line
        }

        if (request.getLastMaintenanceDate() != null) eq.setLastMaintenanceDate(request.getLastMaintenanceDate());
        if (request.getNextMaintenanceDate() != null) eq.setNextMaintenanceDate(request.getNextMaintenanceDate());

        equipmentRepository.save(eq);
        EquipmentResponseDto dto = mapToDto(eq);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of("equipment", dto))
                .message("Equipment updated successfully.")
                .build();
    }


    @Override
    @Transactional
    public ApiResponse deleteEquipment(Long id) {
        Equipment eq = equipmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipment not found"));
        equipmentRepository.delete(eq);
        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Equipment deleted successfully.")
                .build();
    }

    // --- Helper Methods ---

    /**
     * Converts an Integer to the Condition enum.
     * Assumes a mapping where 1=GOOD, 2=FAIR, 3=POOR. Adjust as per your enum definition.
     *
     * @param conditionInt The integer representation of the condition.
     * @return The corresponding Condition enum value.
     */
    private Condition convertIntegerToCondition(Integer conditionInt) {
        if (conditionInt == null) {
            throw new IllegalArgumentException("Condition cannot be null.");
        }
        // ** IMPORTANT: Adjust these mappings based on your actual Condition enum values **
        return switch (conditionInt) {
            case 1 -> Condition.GOOD;
            case 2 -> Condition.FAIR;
            case 3 -> Condition.POOR;
            default -> throw new IllegalArgumentException("Invalid condition integer: " + conditionInt);
        };
    }

    /**
     * Converts the Condition enum to an Integer for the response DTO.
     *
     * @param condition The Condition enum value.
     * @return The corresponding Integer value.
     */
    private Integer convertConditionToInteger(Condition condition) {
        if (condition == null) {
            return null;
        }
        // ** IMPORTANT: Adjust these mappings based on your actual Condition enum values **
        return switch (condition) {
            case GOOD -> 1;
            case FAIR -> 2;
            case POOR -> 3;
            default -> null; // Or some default integer
        };
    }

    /**
     * Maps an Equipment entity to its DTO.
     *
     * @param e The Equipment entity.
     * @return The EquipmentResponseDto.
     */
    private EquipmentResponseDto mapToDto(Equipment e) {
        EquipmentResponseDto dto = new EquipmentResponseDto();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setPurchaseDate(e.getPurchase_date()); // Using the correct getter
        // *** Update the DTO mapping to use the new field name ***
        dto.setEquipmentCondition(convertConditionToInteger(e.getEquipmentCondition())); // Changed line
        dto.setLastMaintenanceDate(e.getLastMaintenanceDate());
        dto.setNextMaintenanceDate(e.getNextMaintenanceDate());
        return dto;
    }
}