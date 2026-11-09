package org.java_proj.gym_management_system.features.assignedGymPackage.service.impl;

import lombok.RequiredArgsConstructor;
import org.java_proj.gym_management_system.common.constant.Status;
import org.java_proj.gym_management_system.config.exceptions.EntityCreationException;
import org.java_proj.gym_management_system.config.exceptions.EntityNotFoundException;
import org.java_proj.gym_management_system.config.response.dto.ApiResponse;
import org.java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org.java_proj.gym_management_system.config.response.dto.PaginationMeta;
import org.java_proj.gym_management_system.features.assignedGymPackage.dto.request.AssignedGymPackageRequest;
import org.java_proj.gym_management_system.features.assignedGymPackage.dto.response.AssignedGymPackageResponseDto;
import org.java_proj.gym_management_system.features.assignedGymPackage.dto.response.TrainerPackage;
import org.java_proj.gym_management_system.features.assignedGymPackage.repository.AssignedGymPackageRepository;
import org.java_proj.gym_management_system.features.assignedGymPackage.service.AssignedGymPackageService;
import org.java_proj.gym_management_system.features.bookPackage.service.BookPackageService;
import org.java_proj.gym_management_system.features.managePackage.dto.response.ScheduleSummaryDto;
import org.java_proj.gym_management_system.features.managePackage.repository.GymPackageRepository;
import org.java_proj.gym_management_system.features.users.repository.UserRepository;
import org.java_proj.gym_management_system.model.AssignedGymPackage;
import org.java_proj.gym_management_system.model.GymPackage;
import org.java_proj.gym_management_system.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignedGymPackageServiceImpl implements AssignedGymPackageService {
    private final AssignedGymPackageRepository assignedGymPackageRepository;
    private final UserRepository userRepository;
    private final GymPackageRepository gymPackageRepository;
    private final BookPackageService bookPackageService;

    @Override
    public ApiResponse assignedGymPackage(AssignedGymPackageRequest request) {
        // Validate trainer exists and is not a MEMBER
        User trainer = this.userRepository.findById(request.getTrainerId())
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found with id " + request.getTrainerId()));
        if (trainer.getRole() != null && "MEMBER".equals(trainer.getRole().getName()) || "ADMIN".equals(Objects.requireNonNull(trainer.getRole()).getName())) {
            throw new EntityCreationException("Member cannot assign to package.");
        }

        GymPackage gymPackage = this.gymPackageRepository.findById(request.getGymPackageId())
                .orElseThrow(() -> new EntityNotFoundException("Gym package not found to assign with id "+ request.getGymPackageId()));

        this.gymPackageRepository.findByIdAndStatus(request.getGymPackageId(), Status.INACTIVE)
                .orElseThrow(() -> new EntityCreationException("This package is active. Can't assign."));

        // Check if trainer already has an ACTIVE assignment
        int activeAssignments = assignedGymPackageRepository.countByTrainerIdAndStatus(request.getTrainerId(), Status.ACTIVE);

        if (activeAssignments >= 2) {
            throw new EntityCreationException("Trainer " + request.getTrainerId() + " is already assigned to 2 active gym packages.");
        }

        // Check if schedule already has an ACTIVE assignment (to any trainer)
        if (assignedGymPackageRepository.existsByGymPackageIdAndStatus(request.getGymPackageId(), Status.ACTIVE)) {
            throw new EntityCreationException("Gym package " + request.getGymPackageId() + " is already assigned to an active trainer.");
        }

        AssignedGymPackage assignedGymPackage = new AssignedGymPackage();
        assignedGymPackage.setTrainer(trainer);
        assignedGymPackage.setGymPackage(gymPackage);

        assignedGymPackageRepository.save(assignedGymPackage);

        AssignedGymPackageResponseDto dto = new AssignedGymPackageResponseDto();
        dto.setId(assignedGymPackage.getId());
        dto.setTrainerId(trainer.getId());
        dto.setTrainerName(trainer.getProfile().getName());
        dto.setGymPackageId(gymPackage.getId());

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.CREATED.value())
                .data(Map.of("AssignedGymPackage", dto))
                .message("Trainer assigned to a gym package successfully.")
                .build();
    }

    @Override
    public ApiResponse unassignedGymPackage(Long trainerID, Long packageId) {
        // Find ACTIVE assignment for this trainer
        AssignedGymPackage assignedGymPackage = this.assignedGymPackageRepository
                .findByTrainerIdAndGymPackageIdAndStatus(trainerID, packageId, Status.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException("Assigned gym package not found with trainer id "+trainerID+ " and gym package id"+ packageId));

        this.assignedGymPackageRepository.delete(assignedGymPackage);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value()) // Use OK (200) for updates, not CREATED (201)
                .message("Trainer unassigned successfully.")
                .build();
    }

    @Override
    public ApiResponse updateAssign(Long trainerId, Long packageId) {
        AssignedGymPackage assignedGymPackage = this.assignedGymPackageRepository
                .findByGymPackageIdAndStatus(packageId, Status.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException("No assigning gym package found."));

        User trainer = this.userRepository.findById(trainerId)
                        .orElseThrow(() -> new EntityNotFoundException("No trainer found with id " + trainerId));

        assignedGymPackage.setTrainer(trainer);
        this.assignedGymPackageRepository.save(assignedGymPackage);

        return ApiResponse.builder()
                .success(1).code(HttpStatus.OK.value())
                .message("Assigning updated successfully.").build();
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedApiResponse<TrainerPackage> getAssignedPackagesByTrainer(Long trainerId, Pageable pageable) {
        // Fetch assigned packages for the trainer with pagination
        Page<AssignedGymPackage> page = assignedGymPackageRepository.findByTrainerId(trainerId, pageable);

        List<TrainerPackage> packagesDto = page.getContent().stream().map(assignedPackage -> {
            GymPackage gymPackage = assignedPackage.getGymPackage();

            TrainerPackage dto = new TrainerPackage();
            dto.setId(gymPackage.getId().toString());
            dto.setName(gymPackage.getName());
            dto.setDuration(gymPackage.getDuration());
            dto.setPrice(gymPackage.getPrice());
            dto.setDescription(gymPackage.getDescription());
            dto.setStartDate(gymPackage.getStartDate().toString());
            dto.setEndDate(gymPackage.getEndDate().toString());
            dto.setType(gymPackage.getGymPackageType());

            List<ScheduleSummaryDto> schedulesDto = gymPackage.getSchedules().stream()
                    .map(schedule -> {
                        ScheduleSummaryDto dtoSchedule = new ScheduleSummaryDto();
                        dtoSchedule.setId(schedule.getId());
                        dtoSchedule.setStartTime(schedule.getStartTime());
                        dtoSchedule.setEndTime(schedule.getEndTime());
                        dtoSchedule.setDay(schedule.getDay());
                        return dtoSchedule;
                    })
                    .toList();

            dto.setSchedule(schedulesDto);

            // Count clients enrolled
            Long clientsCount = bookPackageService.getUserCountByGymPackage(gymPackage.getId());
            dto.setClientsEnrolled(clientsCount != null ? clientsCount.intValue() : 0);

            // Determine status based on dates
            LocalDate now = LocalDate.now();
            if (now.isBefore(gymPackage.getStartDate())) {
                dto.setStatus("upcoming");
            } else if (now.isAfter(gymPackage.getEndDate())) {
                dto.setStatus("completed");
            } else {
                dto.setStatus("active");
            }

            return dto;
        }).collect(Collectors.toList());

        // Prepare pagination meta
        PaginationMeta meta = new PaginationMeta();
        meta.setTotalItems(page.getTotalElements());
        meta.setTotalPages(page.getTotalPages());
        meta.setCurrentPage(pageable.getPageNumber() + 1);

        return PaginatedApiResponse.<TrainerPackage>builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Assigned packages fetched successfully.")
                .meta(meta)
                .data(packagesDto)
                .build();
    }

}
