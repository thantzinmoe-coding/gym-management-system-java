package org._java_proj.gym_management_system.features.superAdmin.service;

import org._java_proj.gym_management_system.common.constant.MemberStatus;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org._java_proj.gym_management_system.features.superAdmin.dto.request.RejectBookingRequest;
import org._java_proj.gym_management_system.features.superAdmin.dto.response.AvailableTrainersResponse;
import org._java_proj.gym_management_system.features.superAdmin.dto.response.BookingDetailResponse;
import org._java_proj.gym_management_system.features.superAdmin.dto.response.SuperAdminDashBoardResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org._java_proj.gym_management_system.features.superAdmin.dto.response.TrainerResponseDto;

public interface SuperAdminService {

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    ApiResponse deleteById(final Long id);

    PaginatedApiResponse<SuperAdminDashBoardResponse> getAllUsersPaginated(
            String keyword, String role, String status, Pageable pageable);

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    ApiResponse acceptBooking(Long bookingId);

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    PaginatedApiResponse<BookingDetailResponse> getAllBookings(
            String keyword, Long memberId, Long packageId, MemberStatus memberStatus, Pageable pageable);

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    ApiResponse changeTrainerStatus(Long trainerId, String trainerStatus);

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    ApiResponse acceptTrainer(Long trainerId);

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    ApiResponse rejectTrainer(Long trainerId);
//
    ApiResponse rejectBooking(Long bookingId, RejectBookingRequest request);

    PaginatedApiResponse<TrainerResponseDto> getAllTrainers(Pageable pageable);

    PaginatedApiResponse<TrainerResponseDto> getAllActiveTrainers(Pageable pageable);

    PaginatedApiResponse<AvailableTrainersResponse> getAllAvailableTrainers(Pageable pageable);
}
