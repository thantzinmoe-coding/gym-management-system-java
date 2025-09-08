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
import org._java_proj.gym_management_system.features.userDetailInfo.repository.UserDetailInfoRepository;
import org._java_proj.gym_management_system.features.users.repository.ProfileRepository;
import org._java_proj.gym_management_system.features.users.repository.UserRepository;
import org._java_proj.gym_management_system.model.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org._java_proj.gym_management_system.features.superAdmin.dto.response.TrainerResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuperAdminServiceImpl implements SuperAdminService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BookPackageRepository bookPackageRepository;
    private final UserDetailInfoRepository userDetailInfoRepository;
    private final ProfileRepository profileRepository;


    @Override
    public ApiResponse deleteById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User id " + id + " is not Found"));

        Profile profile = this.profileRepository.findById(user.getProfile().getId()).
                orElseThrow(() -> new EntityNotFoundException("Profile not found"));

        UserDetailInfo userDetailInfo = this.userDetailInfoRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("User detail info not found"));

        this.userDetailInfoRepository.delete(userDetailInfo);

        this.profileRepository.delete(profile);

        this.userRepository.delete(user);


        return ApiResponse.builder().success(1).code(HttpStatus.OK.value())
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
                        .nrc(user.getProfile().getNrc())
                        .dob(String.valueOf(user.getProfile().getDob()))
                        .gender(user.getProfile().getGender())
                        .specialization(user.getUserDetailInfo().getSpecialization())
                        .experience(user.getUserDetailInfo().getExperience())
                        .avatarUrl(user.getProfile().getProfilePic())
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
        dto.setMemberId(booking.getUser().getId());
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
    public ApiResponse acceptTrainer(Long trainerId, String trainerStatus) {
        User trainer = this.userRepository.findById(trainerId)
                .orElseThrow(() -> new EntityNotFoundException("No trainer not found with id "+ trainerId));


        if(Objects.equals(trainerStatus, "ACTIVE")) {
            trainer.setStatus(Status.ACTIVE);
        } else {
            trainer.setStatus(Status.INACTIVE);
        }
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

    @Override
    public PaginatedApiResponse<TrainerResponseDto> getAllTrainers(Pageable pageable) {
        Page<User> trainersPage = userRepository.findByRoleName("TRAINER", pageable);

        List<TrainerResponseDto> trainersDto = trainersPage.getContent().stream()
                .map(trainer -> {
                    TrainerResponseDto dto = modelMapper.map(trainer, TrainerResponseDto.class);
                    dto.setStatus(trainer.getStatus().toString());
                    dto.setName(trainer.getProfile().getName());
                    dto.setPhone(trainer.getProfile().getPhone());
                    dto.setExperience(trainer.getUserDetailInfo().getExperience());
                    dto.setAvatarUrl(trainer.getProfile().getProfilePic());
                    return dto;
                })
                .collect(Collectors.toList());

        PaginationMeta meta = new PaginationMeta();
        meta.setTotalItems(trainersPage.getTotalElements());
        meta.setTotalPages(trainersPage.getTotalPages());
        meta.setCurrentPage(pageable.getPageNumber() + 1);

        return PaginatedApiResponse.<TrainerResponseDto>builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Trainers fetched successfully.")
                .meta(meta)
                .data(trainersDto)
                .build();
    }
    @Override
    public PaginatedApiResponse<TrainerResponseDto> getAllActiveTrainers(Pageable pageable) {
        Page<User> trainersPage = userRepository.findByRoleAndStatus("TRAINER",Status.ACTIVE, pageable);

        List<TrainerResponseDto> trainersDto = trainersPage.getContent().stream()
                .map(trainer -> {
                    TrainerResponseDto dto = modelMapper.map(trainer, TrainerResponseDto.class);
                    dto.setStatus(trainer.getStatus().toString());
                    dto.setName(trainer.getProfile().getName());
                    dto.setPhone(trainer.getProfile().getPhone());
                    dto.setSpecialization(trainer.getUserDetailInfo().getSpecialization());
                    dto.setAvatarUrl(trainer.getProfile().getProfilePic());
                    return dto;
                })
                .collect(Collectors.toList());

        PaginationMeta meta = new PaginationMeta();
        meta.setTotalItems(trainersPage.getTotalElements());
        meta.setTotalPages(trainersPage.getTotalPages());
        meta.setCurrentPage(pageable.getPageNumber() + 1);

        return PaginatedApiResponse.<TrainerResponseDto>builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Active trainers fetched successfully.")
                .meta(meta)
                .data(trainersDto)
                .build();
    }

    @Override
    public PaginatedApiResponse<AvailableTrainersResponse> getAllAvailableTrainers(Pageable pageable) {
        Page<User> trainers = userRepository.findAvailableTrainers(2, Status.ACTIVE,pageable);
        List<AvailableTrainersResponse> availableTrainersResponses =  trainers.stream().map(user -> {
            AvailableTrainersResponse dto = new AvailableTrainersResponse();
            dto.setId(user.getId());
            dto.setName(user.getProfile() != null ? user.getProfile().getName() : null);
            dto.setEmail(user.getEmail());
            dto.setPhone(user.getProfile() != null ? user.getProfile().getPhone() : null);
            dto.setSpecialization(user.getUserDetailInfo() != null ? user.getUserDetailInfo().getSpecialization() : null);
            dto.setExperience(user.getUserDetailInfo() != null ? user.getUserDetailInfo().getExperience() : null);
            dto.setRating(user.getReceivedFeedback().size());

            // ✅ Total Clients Calculation
            Set<Long> clientIds = user.getAssignedGymPackages().stream()
                    .flatMap(agp -> agp.getGymPackage().getBooking().stream())
                    .map(Booking::getUser)
                    .map(User::getId)
                    .collect(Collectors.toSet());

            dto.setTotalClients(clientIds.size());

            dto.setStatus(user.getStatus());
            dto.setPackages(user.getAssignedGymPackages().stream()
                    .map(AssignedGymPackage::getGymPackage)
                    .map(pkg -> {
                        GymPackageResponse gpDto = new GymPackageResponse();
                        gpDto.setId(pkg.getId());
                        gpDto.setName(pkg.getName());
                        gpDto.setPrice(pkg.getPrice());
                        gpDto.setType(pkg.getGymPackageType().name());
                        gpDto.setDescription(pkg.getDescription());
                        return gpDto;
                    })
                    .collect(Collectors.toList()));


            return dto;
        }).collect(Collectors.toList());

        PaginationMeta meta = new PaginationMeta();
        meta.setTotalItems(trainers.getTotalElements());
        meta.setTotalPages(trainers.getTotalPages());
        meta.setCurrentPage(pageable.getPageNumber() + 1);

        return PaginatedApiResponse.<AvailableTrainersResponse>builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Active trainers fetched successfully.")
                .meta(meta)
                .data(availableTrainersResponses)
                .build();
    }

    @Override
    public PaginatedApiResponse<SuperAdminDashBoardResponse> getBookedUsers(Pageable pageable) {
        Page<User> bookedUsersPage = userRepository.findBookedUsers(pageable);

        List<SuperAdminDashBoardResponse> userResponses = bookedUsersPage.getContent().stream()
                .filter(user -> user.getProfile() != null)
                .map(user -> SuperAdminDashBoardResponse.builder()
                        .id(user.getId())
                        .name(user.getProfile().getName())
                        .email(user.getEmail())
                        .phone(user.getProfile().getPhone())
                        .address(user.getProfile().getAddress())
                        .role(user.getRole().getName())
                        .status(user.getStatus())

                        .build())
                .collect(Collectors.toList());

        PaginationMeta meta = new PaginationMeta();
        meta.setTotalItems(bookedUsersPage.getTotalElements());
        meta.setTotalPages(bookedUsersPage.getTotalPages());
        meta.setCurrentPage(pageable.getPageNumber() + 1);

        return PaginatedApiResponse.<SuperAdminDashBoardResponse>builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Booked users fetched successfully")
                .meta(meta)
                .data(userResponses)
                .build();
    }
}


