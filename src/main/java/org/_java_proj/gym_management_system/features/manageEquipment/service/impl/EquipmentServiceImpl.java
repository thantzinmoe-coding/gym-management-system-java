package org._java_proj.gym_management_system.features.manageEquipment.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.common.storage.StorageService;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginationMeta;
import org._java_proj.gym_management_system.features.manageEquipment.dto.request.EquipmentCreateRequest;
import org._java_proj.gym_management_system.features.manageEquipment.dto.request.EquipmentUpdateRequest;
import org._java_proj.gym_management_system.features.manageEquipment.dto.response.EquipmentResponseDto;
import org._java_proj.gym_management_system.features.manageEquipment.repository.EquipmentRepository;
import org._java_proj.gym_management_system.features.manageEquipment.service.EquipmentService;
import org._java_proj.gym_management_system.model.Equipment;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final ModelMapper modelMapper;
    private final StorageService storageService;

    @Override
    @Transactional
    public ApiResponse createEquipment(EquipmentCreateRequest request) {
        Equipment equipment = new Equipment();
        equipment.setName(request.getName());
        equipment.setPurchaseDate(request.getPurchaseDate());
        equipment.setEquipmentCondition(request.getEquipmentCondition());
        equipment.setLastMaintenanceDate(request.getLastMaintenanceDate());
        equipment.setNextMaintenanceDate(request.getNextMaintenanceDate());

        if (request.getEquipmentPhoto() != null && !request.getEquipmentPhoto().isEmpty()) {
            String filename = storageService.store(request.getEquipmentPhoto());
            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/files/")
                    .path(filename)
                    .toUriString();
            equipment.setEquipmentPhoto(fileUrl);
        }

        equipmentRepository.save(equipment);
        EquipmentResponseDto dto = modelMapper.map(equipment, EquipmentResponseDto.class);
        dto.setId(String.valueOf(equipment.getId())); // Convert Long to String

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.CREATED.value())
                .data(Map.of("equipment", dto))
                .message("Equipment created successfully.")
                .build();
    }

    @Override
    public ApiResponse getEquipment(String id) {
        if (id == null || id.trim().isEmpty()) {
            return ApiResponse.builder()
                    .success(0)
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("Equipment ID cannot be null or empty")
                    .build();
        }
        try {
            Long equipmentId = Long.parseLong(id);
            Equipment eq = equipmentRepository.findById(equipmentId)
                    .orElseThrow(() -> new EntityNotFoundException("Equipment not found"));
            EquipmentResponseDto dto = modelMapper.map(eq, EquipmentResponseDto.class);
            dto.setId(String.valueOf(eq.getId())); // Convert Long to String

            return ApiResponse.builder()
                    .success(1)
                    .code(HttpStatus.OK.value())
                    .data(Map.of("equipment", dto))
                    .message("Equipment fetched.")
                    .build();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid equipment ID format");
        }
    }

    @Override
    public PaginatedApiResponse<EquipmentResponseDto> listEquipments(Pageable pageable) {
        Page<Equipment> page = equipmentRepository.getAllEquipments(pageable);
        List<EquipmentResponseDto> data = page.getContent().stream()
                .map(equipment -> {
                    EquipmentResponseDto dto = modelMapper.map(equipment, EquipmentResponseDto.class);
                    dto.setId(String.valueOf(equipment.getId())); // Convert Long to String
                    return dto;
                })
                .toList();

        PaginationMeta meta = new PaginationMeta();
        meta.setTotalItems(page.getTotalElements());
        meta.setTotalPages(page.getTotalPages());
        meta.setCurrentPage(pageable.getPageNumber() + 1);

        return PaginatedApiResponse.<EquipmentResponseDto>builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Fetched successfully")
                .meta(meta)
                .data(data)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse updateEquipment(String id, EquipmentUpdateRequest request) {
        if (id == null || id.trim().isEmpty()) {
            return ApiResponse.builder()
                    .success(0)
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("Equipment ID cannot be null or empty")
                    .build();
        }
        try {
            Long equipmentId = Long.parseLong(id);
            Equipment eq = equipmentRepository.findById(equipmentId)
                    .orElseThrow(() -> new EntityNotFoundException("Equipment not found"));

            if (request.getName() != null) eq.setName(request.getName());
            if (request.getPurchaseDate() != null) eq.setPurchaseDate(request.getPurchaseDate());
            if (request.getEquipmentCondition() != null) eq.setEquipmentCondition(request.getEquipmentCondition());
            if (request.getLastMaintenanceDate() != null) eq.setLastMaintenanceDate(request.getLastMaintenanceDate());
            if (request.getNextMaintenanceDate() != null) eq.setNextMaintenanceDate(request.getNextMaintenanceDate());

            if (request.getEquipmentPhoto() != null && !request.getEquipmentPhoto().isEmpty()) {
                String filename = storageService.store(request.getEquipmentPhoto());
                String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/files/")
                        .path(filename)
                        .toUriString();
                eq.setEquipmentPhoto(fileUrl);
            }

            equipmentRepository.save(eq);
            EquipmentResponseDto dto = modelMapper.map(eq, EquipmentResponseDto.class);
            dto.setId(String.valueOf(eq.getId())); // Convert Long to String

            return ApiResponse.builder()
                    .success(1)
                    .code(HttpStatus.OK.value())
                    .data(Map.of("equipment", dto))
                    .message("Equipment updated successfully.")
                    .build();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid equipment ID format");
        }
    }

    @Override
    @Transactional
    public ApiResponse deleteEquipment(String id) {
        if (id == null || id.trim().isEmpty()) {
            return ApiResponse.builder()
                    .success(0)
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("Equipment ID cannot be null or empty")
                    .build();
        }
        try {
            Long equipmentId = Long.parseLong(id);
            Equipment eq = equipmentRepository.findById(equipmentId)
                    .orElseThrow(() -> new EntityNotFoundException("Equipment not found"));
            equipmentRepository.delete(eq);
            return ApiResponse.builder()
                    .success(1)
                    .code(HttpStatus.OK.value())
                    .message("Equipment deleted successfully.")
                    .build();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid equipment ID format");
        }
    }
}