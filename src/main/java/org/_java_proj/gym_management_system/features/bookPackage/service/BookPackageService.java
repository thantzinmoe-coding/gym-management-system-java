package org._java_proj.gym_management_system.features.bookPackage.service;

import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org._java_proj.gym_management_system.features.bookPackage.dto.request.BookPackageRequest;
import org._java_proj.gym_management_system.features.bookPackage.dto.response.BookPackageResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

public interface BookPackageService {
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    ApiResponse bookPackage(BookPackageRequest request);

    @PreAuthorize("hasRole('ROLE_MEMBER')")
    ApiResponse cancelPackage(Long id);

//    @PreAuthorize("hasRole('ROLE_ADMIN')"
    PaginatedApiResponse<BookPackageResponseDto> getAllBookingPackages(Pageable pageable);
}
