package org._java_proj.gym_management_system.features.manageAttendance.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.util.ResponseUtils;
import org._java_proj.gym_management_system.features.manageAttendance.dto.request.AttendanceCreateRequest;
import org._java_proj.gym_management_system.features.manageAttendance.dto.request.AttendanceUpdateRequest;
import org._java_proj.gym_management_system.features.manageAttendance.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base.path}/attendance")
@Tag(name = "Attendance API", description = "Endpoints for managing user and trainer attendance")
public class ManageAttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    @Operation(summary = "Record attendance entry")
    public ResponseEntity<ApiResponse> createAttendance(
            @RequestBody AttendanceCreateRequest request,
            HttpServletRequest servletRequest
    ) {
        ApiResponse response = attendanceService.createAttendance(request);
        return ResponseUtils.buildResponse(servletRequest, response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific attendance record by ID")
    public ResponseEntity<ApiResponse> getAttendance(
            @PathVariable Long id,
            HttpServletRequest servletRequest
    ) {
        ApiResponse response = attendanceService.getAttendance(id);
        return ResponseUtils.buildResponse(servletRequest, response);
    }

    @GetMapping("/trainer/{trainerId}")
    @Operation(summary = "Get all attendance records for a specific trainer")
    public ResponseEntity<ApiResponse> getAttendancesByUser(
            @PathVariable Long trainerId,
            HttpServletRequest servletRequest
    ) {
        ApiResponse response = attendanceService.getAttendancesByUser(trainerId);
        return ResponseUtils.buildResponse(servletRequest, response);
    }

    @GetMapping
    @Operation(summary = "List all attendance records")
    public ResponseEntity<ApiResponse> listAllAttendances(HttpServletRequest servletRequest) {
        ApiResponse response = attendanceService.listAllAttendances();
        return ResponseUtils.buildResponse(servletRequest, response);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update attendance record (e.g., record time-out)")
    public ResponseEntity<ApiResponse> updateAttendance(
            @PathVariable Long id,
            @RequestBody AttendanceUpdateRequest request,
            HttpServletRequest servletRequest
    ) {
        ApiResponse response = attendanceService.updateAttendance(id, request);
        return ResponseUtils.buildResponse(servletRequest, response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an attendance record")
    public ResponseEntity<ApiResponse> deleteAttendance(
            @PathVariable Long id,
            HttpServletRequest servletRequest
    ) {
        ApiResponse response = attendanceService.deleteAttendance(id);
        return ResponseUtils.buildResponse(servletRequest, response);
    }

    @GetMapping("/trainer/{trainerId}/total-hours")
    @Operation(summary = "Get total hours worked by a specific trainer")
    public ResponseEntity<ApiResponse> getTotalHoursWorkedByUser(
            @PathVariable Long trainerId,
            HttpServletRequest servletRequest
    ) {
        ApiResponse response = attendanceService.getTotalHoursWorkedByUser(trainerId);
        return ResponseUtils.buildResponse(servletRequest, response);
    }

}