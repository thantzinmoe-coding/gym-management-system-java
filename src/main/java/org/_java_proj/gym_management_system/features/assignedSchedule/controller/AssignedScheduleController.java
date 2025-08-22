package org._java_proj.gym_management_system.features.assignedSchedule.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.util.ResponseUtils;
import org._java_proj.gym_management_system.features.assignedSchedule.dto.request.AssignedScheduleRequest;
import org._java_proj.gym_management_system.features.assignedSchedule.service.AssignedScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base.path}/assign-schedule")
@RequiredArgsConstructor
@Tag(name = "Assign Schedule API", description = "Endpoints for assigning schedule for trainer")
public class AssignedScheduleController {
    private final AssignedScheduleService assignedScheduleService;

    @PostMapping
    @Operation(
            summary = "Trainer assign to schedule",
            description = "Trainer assign schedule to the gym management system.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Assign schedule request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AssignedScheduleRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Trainer assigned successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    public ResponseEntity<ApiResponse> assignedGymSchedule(@RequestBody AssignedScheduleRequest assignedScheduleRequest, HttpServletRequest request) {
        ApiResponse response = this.assignedScheduleService.assignedSchedule(assignedScheduleRequest);
        return ResponseUtils.buildResponse(request, response);
    }

    @PostMapping("{id}")
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
        ApiResponse response = this.assignedScheduleService.unassignedSchedule(trainerID);
        return ResponseUtils.buildResponse(request, response);
    }
}
