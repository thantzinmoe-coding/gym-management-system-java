package org.java_proj.gym_management_system.features.manageAttendance.service;

import org.java_proj.gym_management_system.config.response.dto.ApiResponse;
import org.java_proj.gym_management_system.features.manageAttendance.dto.request.AttendanceCreateRequest;
import org.java_proj.gym_management_system.features.manageAttendance.dto.request.AttendanceUpdateRequest;

public interface AttendanceService {
    ApiResponse createAttendance(AttendanceCreateRequest request);
    ApiResponse getAttendance(Long id);
    ApiResponse listAllAttendances();
    ApiResponse getAttendancesByUser(Long userId);
    ApiResponse updateAttendance(Long id, AttendanceUpdateRequest request);
    ApiResponse deleteAttendance(Long id);
    ApiResponse getTotalHoursWorkedByUser(Long userId);
}
