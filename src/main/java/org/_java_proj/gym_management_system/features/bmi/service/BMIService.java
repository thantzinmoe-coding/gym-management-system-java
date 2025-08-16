package org._java_proj.gym_management_system.features.bmi.service;

import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org._java_proj.gym_management_system.features.bmi.dto.request.BMICreateRequest;
import org._java_proj.gym_management_system.features.bmi.dto.response.BMIDetailResponseDto;
import org.springframework.data.domain.Pageable;

public interface BMIService {

    ApiResponse createBMI(BMICreateRequest createRequest);

    ApiResponse getBMI(Long id);

    ApiResponse deleteBMI(Long id);

    ApiResponse updateBMI(Long id, BMICreateRequest updateRequest);

    PaginatedApiResponse<BMIDetailResponseDto> getBMIsDetail(Pageable pageable, Long userId);
}
