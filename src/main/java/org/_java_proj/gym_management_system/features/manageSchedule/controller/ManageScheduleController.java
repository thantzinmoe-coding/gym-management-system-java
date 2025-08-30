package org._java_proj.gym_management_system.features.manageSchedule.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org._java_proj.gym_management_system.config.response.util.ResponseUtils;
import org._java_proj.gym_management_system.features.manageSchedule.dto.request.ScheduleBulkRequest;
import org._java_proj.gym_management_system.features.manageSchedule.dto.request.ScheduleUpdateRequest;
import org._java_proj.gym_management_system.features.manageSchedule.dto.response.ScheduleResponseDto;
import org._java_proj.gym_management_system.features.manageSchedule.service.ScheduleService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.base.path}/schedule")
@RequiredArgsConstructor
@Tag(name = "Schedule API", description = "Endpoints for managing gym schedules")
public class ManageScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/bulk")
    @Operation(
            summary = "Create multiple schedules",
            description = "Create multiple schedules for a single gym package in one request",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Gym package ID with multiple schedule requests",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ScheduleBulkRequest.class),
                            examples = @ExampleObject(
                                    name = "Bulk schedule example",
                                    value = """
                                           {
                                              "gymPackageId": 1,
                                              "schedules": [
                                                {
                                                  "day": "Monday",
                                                  "startTime": "08:00",
                                                  "endTime": "10:00"
                                                },
                                                {
                                                  "day": "Wednesday",
                                                  "startTime": "18:00",
                                                  "endTime": "20:00"
                                                },
                                                {
                                                  "day": "Friday",
                                                  "startTime": "07:00",
                                                  "endTime": "09:00"
                                                }
                                              ]
                                           }
                                         """
                            )
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                            description = "Schedules created successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            name = "Success Response",
                                            value = """
                                                {
                                                  "success": 1,
                                                  "code": 200,
                                                  "meta": {
                                                    "endpoint": "/api/v1/schedules/bulk",
                                                    "method": "POST"
                                                  },
                                                  "data": [
                                                    {
                                                      "id": 1,
                                                      "day": "Monday",
                                                      "startTime": "08:00",
                                                      "endTime": "10:00",
                                                      "gymPackageId": 1
                                                    },
                                                    {
                                                      "id": 2,
                                                      "day": "Wednesday",
                                                      "startTime": "18:00",
                                                      "endTime": "20:00",
                                                      "gymPackageId": 1
                                                    },
                                                    {
                                                      "id": 3,
                                                      "day": "Friday",
                                                      "startTime": "07:00",
                                                      "endTime": "09:00",
                                                      "gymPackageId": 1
                                                    }
                                                  ],
                                                  "message": "Schedules created successfully"
                                                }"""
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
                            description = "Invalid input or error creating schedules",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            name = "Error Response",
                                            value = """
                                                {
                                                  "success": 0,
                                                  "code": 400,
                                                  "meta": null,
                                                  "data": null,
                                                  "message": "Invalid input or error creating schedules"
                                                }"""
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<List<ScheduleResponseDto>> createBulk(@RequestBody ScheduleBulkRequest request) {
        return ResponseEntity.ok(scheduleService.createSchedules(request));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get Gym Schedule detail",
            description = "Get Gym Schedule detail by its ID.",
            parameters = {
                    @Parameter(name = "id", description = "Gym Schedule ID", required = true)
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Gym Schedule details retrieved successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Gym Schedule not found")
            }
    )
    public ResponseEntity<ApiResponse> getScheduleById(
            @PathVariable Long id,
            HttpServletRequest httpRequest
    ) {
        ApiResponse response = scheduleService.getScheduleById(id);
        return ResponseUtils.buildResponse(httpRequest, response);
    }

    @GetMapping
    @Operation(
            summary = "Get all Gym schedules.",
            description = "Get gym schedule detail.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Fetched successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Gym schedule not found")
            }
    )
    public ResponseEntity<PaginatedApiResponse<ScheduleResponseDto>> getAllSchedules(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest httpRequest
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PaginatedApiResponse<ScheduleResponseDto> response = scheduleService.getAllSchedules(pageable);
        return ResponseUtils.buildPaginatedResponse(httpRequest, response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a schedule",
            description = "Updates the details of an existing schedule.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Schedule update request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ScheduleUpdateRequest.class))
            ),
            parameters = {
                    @Parameter(name = "id", description = "Schedule ID", required = true)
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Schedule updated successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Schedule not found")
            }
    )
    public ResponseEntity<ApiResponse> updateSchedule(
            @PathVariable Long id,
            @RequestBody ScheduleUpdateRequest request,
            HttpServletRequest httpRequest
    ) {
        ApiResponse response = scheduleService.updateSchedule(id, request);
        return ResponseUtils.buildResponse(httpRequest, response);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a schedule",
            description = "Deletes a schedule from the system by its ID.",
            parameters = {
                    @Parameter(name = "id", description = "Schedule ID", required = true)
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Schedule deleted successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Schedule not found")
            }
    )
    public ResponseEntity<ApiResponse> deleteSchedule(
            @PathVariable Long id,
            HttpServletRequest httpRequest
    ) {
        ApiResponse response = scheduleService.deleteSchedule(id);
        return ResponseUtils.buildResponse(httpRequest, response);
    }
}
