package org._java_proj.gym_management_system.features.superAdmin.service.impl;

import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.common.constant.MemberStatus;
import org._java_proj.gym_management_system.common.constant.Status;
import org._java_proj.gym_management_system.config.exceptions.EntityNotFoundException;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginationMeta;
import org._java_proj.gym_management_system.features.bookPackage.repository.BookPackageRepository;
import org._java_proj.gym_management_system.features.superAdmin.dto.response.*;
import org._java_proj.gym_management_system.features.superAdmin.service.SuperAdminService;
import org._java_proj.gym_management_system.features.users.repository.UserRepository;
import org._java_proj.gym_management_system.model.Booking;
import org._java_proj.gym_management_system.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SuperAdminServiceImpl implements SuperAdminService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BookPackageRepository bookPackageRepository;


    @Override
    public ApiResponse deleteById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User id " + id + " is not Found"));

        user.delete();
        user.setEmail(null);
        User updatedUser = userRepository.save(user);

        DeletedUserResponse deletedUserResponse = modelMapper.map(updatedUser, DeletedUserResponse.class);

        deletedUserResponse.setDeletedAt(user.getDeletedAt().toString());
        deletedUserResponse.setRole(user.getRole().getName());

        return ApiResponse.builder().success(1).code(HttpStatus.OK.value())
                .data(Map.of("Deleted User",deletedUserResponse))
                .message("User is successfully deleted").build();
    }


    @Override
    public PaginatedApiResponse<SuperAdminDashBoardResponse> getAllUsersPaginated(
            String keyword, String role, String status, Pageable pageable) {

        // Convert status string to enum if provided
        Status dbStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                dbStatus = Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Handle invalid status value
            }
        }

        // Use a custom repository method that supports filtering
        Page<User> profilePage = userRepository.findUsersWithFilters(keyword, role, dbStatus, pageable);

        List<SuperAdminDashBoardResponse> userResponses = profilePage.getContent().stream()
                .filter(user -> user.getProfile() != null)
                .map(user -> SuperAdminDashBoardResponse.builder()
                        .id(user.getId())
                        .name(user.getProfile().getName())
                        .email(user.getEmail())
                        .phone(user.getProfile().getPhone())
                        .address(user.getProfile().getAddress())
                        .role(user.getRole().getName()) // Add role to response
                        .status(user.getStatus())
                        .build()
                ).toList();

        PaginationMeta meta = new PaginationMeta();
        meta.setTotalItems(profilePage.getTotalElements());
        meta.setTotalPages(profilePage.getTotalPages());
        meta.setCurrentPage(pageable.getPageNumber() + 1);

        return PaginatedApiResponse.<SuperAdminDashBoardResponse>builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Fetched successfully")
                .meta(meta)
                .data(userResponses)
                .build();
    }

    @Override
    public ApiResponse acceptBooking(Long bookingId) {
        Booking booking = (Booking) this.bookPackageRepository.findByIdAndMemberStatus(bookingId, MemberStatus.PENDING)
                .orElseThrow(() -> new EntityNotFoundException("No pending booking package found with id "+ bookingId));

        booking.setUpdatedAt(LocalDateTime.now());
        booking.setMemberStatus(MemberStatus.ACTIVE);
        this.bookPackageRepository.save(booking);

        AcceptedBookingResponse dto =  new AcceptedBookingResponse();
        dto.setBookingId(booking.getId());
        dto.setMemberId(booking.getEntityId());
        dto.setMemberStatus(booking.getMemberStatus());
        dto.setGymPackageId(booking.getGymPackage().getId());
        dto.setGymPackageName(booking.getGymPackage().getName());
        dto.setDescription(booking.getGymPackage().getDescription());

        return ApiResponse.builder().success(1).code(HttpStatus.OK.value())
                .data(Map.of("AcceptedBookingResponse", dto))
                .message("Booking package accepted.").build();

    }


    @Override
    public PaginatedApiResponse<BookingDetailResponse> getAllBookings(
            String keyword, Long memberId, Long packageId, MemberStatus memberStatus, Pageable pageable) {

        Page<BookingDetailResponse> page = bookPackageRepository
                .findPendingBookingsWithDetails(memberStatus, keyword, memberId, packageId, pageable);

        PaginationMeta meta = new PaginationMeta();
        meta.setTotalItems(page.getTotalElements());
        meta.setTotalPages(page.getTotalPages());
        meta.setCurrentPage(pageable.getPageNumber() + 1);

        return PaginatedApiResponse.<BookingDetailResponse>builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Pending bookings fetched successfully")
                .meta(meta)
                .data(page.getContent())
                .build();
    }

    @Override
    public ApiResponse acceptTrainer(Long trainerId) {
        User trainer = this.userRepository.findByIdAndStatus(trainerId, Status.INACTIVE);

        if(trainer == null) {
            throw new EntityNotFoundException("Pending trainer not found with id "+ trainerId);
        }

        trainer.setStatus(Status.ACTIVE);
        trainer.setUpdatedAt(LocalDateTime.now());
        this.userRepository.save(trainer);

        MangeUserResponseDto dto = new MangeUserResponseDto();
        dto.setTrainerId(trainer.getId());
        dto.setTrainerName(trainer.getProfile().getName());
        dto.setTrainerStatus(trainer.getStatus());
        dto.setAcceptedTime(trainer.getUpdatedAt().toString());

        return ApiResponse.builder().success(1).code(HttpStatus.OK.value())
                .data(Map.of("AcceptedTrainerResponse", dto))
                .message("Trainer accepted successfully to use the gym management system.").build();
    }
}
