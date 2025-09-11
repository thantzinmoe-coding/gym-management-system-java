package org._java_proj.gym_management_system.features.bookPackage.service;

import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org._java_proj.gym_management_system.features.bookPackage.dto.request.BookPackageRequest;
import org._java_proj.gym_management_system.features.bookPackage.dto.response.BookPackageDetailResponseDto;
import org._java_proj.gym_management_system.features.bookPackage.dto.response.BookedUsersDetailResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

public interface BookPackageService {
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    ApiResponse bookPackage(BookPackageRequest request);

    @PreAuthorize("hasRole('ROLE_MEMBER')")
    ApiResponse cancelPackage(Long id);

    PaginatedApiResponse<BookPackageDetailResponseDto> getBookingDetail(Long memberId, Pageable pageable);

    ApiResponse getBookingById(Long id);

    PaginatedApiResponse<BookPackageDetailResponseDto> getAllBookingsByPackage(Long packageId, Pageable pageable);

    PaginatedApiResponse<BookedUsersDetailResponse> getActiveUsersByTrainer(Long trainerId, Pageable pageable);

    Long getUserCountByTrainer(Long trainerId);

    Long getUserCountByGymPackage(Long gymPackageId);
}
