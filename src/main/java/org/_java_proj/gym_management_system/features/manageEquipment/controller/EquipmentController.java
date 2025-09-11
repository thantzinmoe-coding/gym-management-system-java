package org._java_proj.gym_management_system.features.manageEquipment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org._java_proj.gym_management_system.config.response.util.ResponseUtils;
import org._java_proj.gym_management_system.features.manageEquipment.dto.request.EquipmentCreateRequest;
import org._java_proj.gym_management_system.features.manageEquipment.dto.request.EquipmentUpdateRequest;
import org._java_proj.gym_management_system.features.manageEquipment.dto.response.EquipmentResponseDto;
import org._java_proj.gym_management_system.features.manageEquipment.service.EquipmentService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/equipment")
@Tag(name = "Equipment API", description = "Endpoints for managing gym equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;

    @PostMapping(consumes = "multipart/form-data")
    @Operation(summary = "Add new equipment with photo")
    public ResponseEntity<ApiResponse> createEquipment(
            @ModelAttribute EquipmentCreateRequest request,
            HttpServletRequest servletRequest
    ) {
        ApiResponse response = equipmentService.createEquipment(request);
        return ResponseUtils.buildResponse(servletRequest, response);
    }

    @PatchMapping(value = "/{id}", consumes = "multipart/form-data")
    @Operation(summary = "Update equipment with photo")
    public ResponseEntity<ApiResponse> updateEquipment(
            @PathVariable String id, // Changed to String to match frontend
            @ModelAttribute EquipmentUpdateRequest request,
            HttpServletRequest servletRequest
    ) {
        ApiResponse response = equipmentService.updateEquipment(id, request);
        return ResponseUtils.buildResponse(servletRequest, response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get equipment by ID")
    public ResponseEntity<ApiResponse> getEquipment(
            @PathVariable String id, // Changed to String
            HttpServletRequest servletRequest
    ) {
        ApiResponse response = equipmentService.getEquipment(id);
        return ResponseUtils.buildResponse(servletRequest, response);
    }

    @GetMapping
    @Operation(
            summary = "List all equipments",
            description = "Get all gym equipments",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Fetched successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Gym equipment not found")
            }
    )
    public ResponseEntity<PaginatedApiResponse<EquipmentResponseDto>> listEquipments(
            @Parameter(description = "Page number")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(value = "size", defaultValue = "20") int size,
            HttpServletRequest servletRequest
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PaginatedApiResponse<EquipmentResponseDto> response = equipmentService.listEquipments(pageable);
        return ResponseUtils.buildPaginatedResponse(servletRequest, response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete equipment")
    public ResponseEntity<ApiResponse> deleteEquipment(
            @PathVariable String id, // Changed to String
            HttpServletRequest servletRequest
    ) {
        ApiResponse response = equipmentService.deleteEquipment(id);
        return ResponseUtils.buildResponse(servletRequest, response);
    }
}