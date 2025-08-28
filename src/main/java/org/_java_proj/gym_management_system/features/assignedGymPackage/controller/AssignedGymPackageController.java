package org._java_proj.gym_management_system.features.assignedGymPackage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.util.ResponseUtils;
import org._java_proj.gym_management_system.features.assignedGymPackage.dto.request.AssignedGymPackageRequest;
import org._java_proj.gym_management_system.features.assignedGymPackage.service.AssignedGymPackageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base.path}/assign-schedule")
@RequiredArgsConstructor
@Tag(name = "Assign Schedule API", description = "Endpoints for assigning schedule for trainer")
public class AssignedGymPackageController {
    private final AssignedGymPackageService assignedGymPackageService;

    @PostMapping
    @Operation(
            summary = "Trainer assign to schedule",
            description = "Trainer assign schedule to the gym management system.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Assign schedule request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AssignedGymPackageRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Trainer assigned successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    public ResponseEntity<ApiResponse> assignedGymSchedule(@RequestBody AssignedGymPackageRequest assignedGymPackageRequest, HttpServletRequest request) {
        ApiResponse response = this.assignedGymPackageService.assignedGymPackage(assignedGymPackageRequest);
        return ResponseUtils.buildResponse(request, response);
    }

    @DeleteMapping("{id}")
    @Operation(
            summary = "Trainer unassign schedule",
            description = "Unassigned a schedule by a trainer with his id",
            parameters = {
                    @Parameter(name = "id", description = "Trainer ID", required = true)
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Trainer unassigned successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Trainer ID not found")
            }
    )
    public ResponseEntity<ApiResponse> unassignedSchedule(@PathVariable("id") Long trainerID, HttpServletRequest request) {
        ApiResponse response = this.assignedGymPackageService.unassignedGymPackage(trainerID);
        return ResponseUtils.buildResponse(request, response);
    }
}
