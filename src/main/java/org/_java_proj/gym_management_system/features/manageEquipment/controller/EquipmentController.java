package org._java_proj.gym_management_system.features.manageEquipment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.util.ResponseUtils;
import org._java_proj.gym_management_system.features.manageEquipment.dto.request.EquipmentCreateRequest;
import org._java_proj.gym_management_system.features.manageEquipment.dto.request.EquipmentUpdateRequest;
import org._java_proj.gym_management_system.features.manageEquipment.service.EquipmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base.path}/equipment")
@Tag(name = "Equipment API", description = "Endpoints for managing gym equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;

    @PostMapping
    @Operation(summary = "Add new equipment")
    public ResponseEntity<ApiResponse> createEquipment(
            @RequestBody EquipmentCreateRequest request,
            HttpServletRequest servletRequest
    ) {
        ApiResponse response = equipmentService.createEquipment(request);
        return ResponseUtils.buildResponse(servletRequest, response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get equipment by ID")
    public ResponseEntity<ApiResponse> getEquipment(
            @PathVariable Long id,
            HttpServletRequest servletRequest
    ) {
        ApiResponse response = equipmentService.getEquipment(id);
        return ResponseUtils.buildResponse(servletRequest, response);
    }

    @GetMapping
    @Operation(summary = "List all equipment")
    public ResponseEntity<ApiResponse> listEquipments(HttpServletRequest servletRequest) {
        ApiResponse response = equipmentService.listEquipments();
        return ResponseUtils.buildResponse(servletRequest, response);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update equipment")
    public ResponseEntity<ApiResponse> updateEquipment(
            @PathVariable Long id,
            @RequestBody EquipmentUpdateRequest request,
            HttpServletRequest servletRequest
    ) {
        ApiResponse response = equipmentService.updateEquipment(id, request);
        return ResponseUtils.buildResponse(servletRequest, response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete equipment")
    public ResponseEntity<ApiResponse> deleteEquipment(
            @PathVariable Long id,
            HttpServletRequest servletRequest
    ) {
        ApiResponse response = equipmentService.deleteEquipment(id);
        return ResponseUtils.buildResponse(servletRequest, response);
    }
}
