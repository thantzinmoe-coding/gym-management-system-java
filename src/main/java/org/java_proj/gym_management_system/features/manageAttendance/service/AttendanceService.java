package org.java_proj.gym_management_system.features.manageAttendance.service;

import org.java_proj.gym_management_system.config.response.dto.ApiResponse;
import org.java_proj.gym_management_system.features.manageAttendance.dto.request.AttendanceCreateRequest;
import org.java_proj.gym_management_system.features.manageAttendance.dto.request.AttendanceUpdateRequest;

public interface AttendanceService {
    ApiResponse createAttendance(AttendanceCreateRequest request);
    ApiResponse getAttendance(Long id);
    org.java_proj.gym_management_system.config.response.dto.PaginatedApiResponse<
            org.java_proj.gym_management_system.features.manageAttendance.dto.response.AttendanceResponseDto> listAllAttendances(
            org.java_proj.gym_management_system.common.constant.AttendanceType type, int days, String keyword,
            org.springframework.data.domain.Pageable pageable);
    ApiResponse getAttendancesByUser(Long userId);
    ApiResponse updateAttendance(Long id, AttendanceUpdateRequest request);
    ApiResponse deleteAttendance(Long id);
    ApiResponse getTotalHoursWorkedByUser(Long userId);
}
