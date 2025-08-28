package org._java_proj.gym_management_system.features.bookPackage.service.impl;

import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.common.constant.MemberStatus;
import org._java_proj.gym_management_system.common.constant.Status;
import org._java_proj.gym_management_system.config.exceptions.EntityCreationException;
import org._java_proj.gym_management_system.config.exceptions.EntityNotFoundException;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org._java_proj.gym_management_system.config.response.dto.PaginationMeta;
import org._java_proj.gym_management_system.features.bookPackage.dto.request.BookPackageRequest;
import org._java_proj.gym_management_system.features.bookPackage.dto.response.BookPackageDetailResponseDto;
import org._java_proj.gym_management_system.features.bookPackage.dto.response.BookPackageResponseDto;
import org._java_proj.gym_management_system.features.bookPackage.repository.BookPackageRepository;
import org._java_proj.gym_management_system.features.bookPackage.service.BookPackageService;
import org._java_proj.gym_management_system.features.managePackage.repository.GymPackageRepository;
import org._java_proj.gym_management_system.features.manageSchedule.repository.ScheduleRepository;
import org._java_proj.gym_management_system.features.users.repository.UserRepository;
import org._java_proj.gym_management_system.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class BookPackageServiceImpl implements BookPackageService {
    private final BookPackageRepository bookPackageRepository;
    private final UserRepository userRepository;
    private final GymPackageRepository gymPackageRepository;
    private final ScheduleRepository scheduleRepository;

    @Override
    public ApiResponse bookPackage(BookPackageRequest request) {
        User member = this.userRepository.findById(request.getMemberID())
                .orElseThrow(() -> new EntityNotFoundException("Member not found with id " + request.getMemberID()));

        if (Objects.equals(member.getRole().getName(), "TRAINER")) {
            throw new EntityNotFoundException("Member not found with id " + request.getMemberID());
        }

        GymPackage gymPackage = this.gymPackageRepository.findByIdAndStatus(request.getGymPackageID(), Status.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException("Gym package not found with id " + request.getGymPackageID()));

        Schedule schedule = this.scheduleRepository.findById(gymPackage.getId())
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found with id "+ gymPackage.getId()));

        // Business Rule: Check if user has any active booking
        if (bookPackageRepository.existsByUserIdAndMemberStatus(request.getMemberID(), MemberStatus.PENDING)) {
            throw new EntityCreationException("Member with id " + request.getMemberID() + " already has an pending booking.");
        }

        if (bookPackageRepository.existsByUserIdAndMemberStatus(request.getMemberID(), MemberStatus.ACTIVE)) {
            throw new EntityCreationException("Member with id " + request.getMemberID() + " already has an active booking.");
        }

        // Create new booking
        Booking booking = new Booking();
        booking.setUser(member);
        booking.setGymPackage(gymPackage);
        booking.setMemberStatus(MemberStatus.PENDING);

        booking = bookPackageRepository.save(booking);

        // Build response
        BookPackageResponseDto dto = new BookPackageResponseDto();
        dto.setBookingPackageID(booking.getId());
        dto.setBookingDate(booking.getCreatedAt().toString());
        dto.setMemberID(booking.getUser().getId());
        dto.setMemberName(member.getProfile().getName());
        dto.setMemberEmail(member.getEmail());
        dto.setGymPackageName(gymPackage.getName());
        dto.setGymPackageDescription(gymPackage.getDescription());
        dto.setPrice(gymPackage.getPrice());
        dto.setDuration(gymPackage.getDuration());
        dto.setStartTime(schedule.getStartTime());
        dto.setEndTime(schedule.getEndTime());
        dto.setDay(schedule.getDay());
        dto.setStatus(booking.getMemberStatus());

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.CREATED.value())
                .data(Map.of("BookPackage", dto))
                .message("Booked package successfully")
                .build();
    }

    @Override
    public ApiResponse cancelPackage(Long id) {
        Booking booking = bookPackageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id " + id));

        if (booking.getMemberStatus() == MemberStatus.CANCELLED) {
            throw new EntityCreationException("Booking is already cancelled");
        }

        if (booking.getMemberStatus() == MemberStatus.ACTIVE) {
            throw new EntityCreationException("Cannot cancel a active booking");
        }

        booking.setMemberStatus(MemberStatus.CANCELLED);
        booking.setDeletedAt(LocalDateTime.now());
        bookPackageRepository.save(booking);

        BookPackageResponseDto dto = new BookPackageResponseDto();
        dto.setCancelDate(booking.getDeletedAt().toString());

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of("CancelTime", dto))
                .message("Booking package canceled successfully. You can now book a new package.")
                .build();
    }

    @Override
    public PaginatedApiResponse<BookPackageDetailResponseDto> getBookingDetail(Long memberId, Pageable pageable) {
        Page<Booking> page = this.bookPackageRepository.findByUserId(memberId, pageable);

        List<BookPackageDetailResponseDto> data = page.getContent().stream()
                .map(this::mapToDto)
                .toList();

        PaginationMeta meta = new PaginationMeta();
        meta.setTotalItems(page.getTotalElements());
        meta.setTotalPages(page.getTotalPages());
        meta.setCurrentPage(pageable.getPageNumber() + 1);

        return PaginatedApiResponse.<BookPackageDetailResponseDto>builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Fetched successfully")
                .meta(meta)
                .data(data)
                .build();
    }

    @Override
    public ApiResponse getBookingById(Long id) {
        Booking booking = this.bookPackageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id "+ id));

        User member = this.userRepository.findById(booking.getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found with id "+ booking.getUser().getId()));

        GymPackage gymPackage = this.gymPackageRepository.findById(booking.getGymPackage().getId())
                .orElseThrow(() -> new EntityNotFoundException("Gym package not found with id "+ booking.getGymPackage().getId()));

        Schedule schedule = this.scheduleRepository.findById(gymPackage.getId())
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found with id "+ gymPackage.getId()));

        BookPackageResponseDto dto = new BookPackageResponseDto();
        dto.setBookingPackageID(booking.getId());
        dto.setBookingDate(booking.getCreatedAt().toString());
        dto.setMemberID(booking.getUser().getId());
        dto.setMemberName(member.getProfile().getName());
        dto.setMemberEmail(member.getEmail());
        dto.setGymPackageName(gymPackage.getName());
        dto.setGymPackageDescription(gymPackage.getDescription());
        dto.setPrice(gymPackage.getPrice());
        dto.setDuration(gymPackage.getDuration());
        dto.setStartTime(schedule.getStartTime());
        dto.setEndTime(schedule.getEndTime());
        dto.setDay(schedule.getDay());
        dto.setStatus(booking.getMemberStatus());

        return ApiResponse.builder()
                .success(1).code(HttpStatus.OK.value())
                .data(Map.of("BookPackageResponseDto", dto))
                .message("Booking package retrieved successfully.").build();
    }

    @Override
    public PaginatedApiResponse<BookPackageDetailResponseDto> getAllBookingsByPackage(Long packageId, Pageable pageable) {
        Page<Booking> page = this.bookPackageRepository.findByGymPackageId(packageId, pageable);

        List<BookPackageDetailResponseDto> data = page.getContent().stream()
                .map(this::mapToDto).toList();

        PaginationMeta meta = new PaginationMeta();
        meta.setTotalItems(page.getTotalElements());
        meta.setTotalPages(page.getTotalPages());
        meta.setCurrentPage(pageable.getPageNumber() + 1);

        return PaginatedApiResponse.<BookPackageDetailResponseDto>builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Fetched successfully")
                .meta(meta)
                .data(data)
                .build();
    }


    @Override
    public Long getUserCountByTrainer(Long trainerId) {
        return bookPackageRepository.countDistinctUsersByTrainer(trainerId);
    }

    private BookPackageDetailResponseDto mapToDto(Booking booking) {

        GymPackage gymPackage = booking.getGymPackage();
        Optional<Schedule> schedule = this.scheduleRepository.findById(booking.getGymPackage().getId());

        BookPackageDetailResponseDto dto = new BookPackageDetailResponseDto();
        dto.setBookPackageId(booking.getId());
        dto.setBookingDate(booking.getCreatedAt().toString());
        dto.setMemberStatus(booking.getMemberStatus());
        dto.setGymPackageName(gymPackage.getName());
        dto.setGymPackageDescription(gymPackage.getDescription());
        dto.setPrice(gymPackage.getPrice());
        dto.setDuration(gymPackage.getDuration());
        if(schedule.isPresent()) {
            dto.setStartTime(schedule.get().getStartTime());
            dto.setEndTime(schedule.get().getEndTime());
            dto.setDay(schedule.get().getDay());
        }
        return dto;
    }

}
