package org._java_proj.gym_management_system.features.userDetailInfo.service;

import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org._java_proj.gym_management_system.features.userDetailInfo.dto.request.UserDetailInfoCreateRequest;
import org._java_proj.gym_management_system.features.userDetailInfo.dto.response.UserDetailInfoResponseDto;
import org.springframework.data.domain.Pageable;

public interface UserDetailInfoService {

    ApiResponse createUserDetailInfo(UserDetailInfoCreateRequest createRequest);

    ApiResponse getUserDetailInfo(Long id);

    ApiResponse deleteUserDetailInfo(Long id);

    ApiResponse updateUserDetailInfo(Long id, UserDetailInfoCreateRequest updateRequest);

    PaginatedApiResponse<UserDetailInfoResponseDto> getUserDetailInfos(Pageable pageable, Long userId);
}
