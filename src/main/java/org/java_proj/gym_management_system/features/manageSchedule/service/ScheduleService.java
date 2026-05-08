package org.java_proj.gym_management_system.features.manageSchedule.service;

import org.java_proj.gym_management_system.config.response.dto.ApiResponse;
import org.java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org.java_proj.gym_management_system.features.manageSchedule.dto.request.ScheduleBulkRequest;
import org.java_proj.gym_management_system.features.manageSchedule.dto.request.ScheduleUpdateRequest;
import org.java_proj.gym_management_system.features.manageSchedule.dto.response.ScheduleResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ScheduleService {
    List<ScheduleResponseDto> createSchedules(ScheduleBulkRequest request);
    ApiResponse getScheduleById(Long id);
    PaginatedApiResponse<ScheduleResponseDto> getAllSchedules(Pageable pageable);
    ApiResponse updateSchedule(Long id, ScheduleUpdateRequest request);
    ApiResponse deleteSchedule(Long id);
}
