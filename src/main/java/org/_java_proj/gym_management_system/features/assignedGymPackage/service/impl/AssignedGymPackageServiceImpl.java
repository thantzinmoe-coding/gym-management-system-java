package org._java_proj.gym_management_system.features.assignedGymPackage.service.impl;

import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.common.constant.Status;
import org._java_proj.gym_management_system.config.exceptions.EntityCreationException;
import org._java_proj.gym_management_system.config.exceptions.EntityNotFoundException;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.features.assignedGymPackage.dto.request.AssignedGymPackageRequest;
import org._java_proj.gym_management_system.features.assignedGymPackage.dto.response.AssignedGymPackageResponseDto;
import org._java_proj.gym_management_system.features.assignedGymPackage.repository.AssignedGymPackageRepository;
import org._java_proj.gym_management_system.features.assignedGymPackage.service.AssignedGymPackageService;
import org._java_proj.gym_management_system.features.managePackage.repository.GymPackageRepository;
import org._java_proj.gym_management_system.features.users.repository.UserRepository;
import org._java_proj.gym_management_system.model.AssignedGymPackage;
import org._java_proj.gym_management_system.model.GymPackage;
import org._java_proj.gym_management_system.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AssignedGymPackageServiceImpl implements AssignedGymPackageService {
    private final AssignedGymPackageRepository assignedGymPackageRepository;
    private final UserRepository userRepository;
    private final GymPackageRepository gymPackageRepository;

    @Override
    public ApiResponse assignedGymPackage(AssignedGymPackageRequest request) {
        // Validate trainer exists and is not a MEMBER
        User trainer = this.userRepository.findById(request.getTrainerId())
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found with id " + request.getTrainerId()));
        if (trainer.getRole() != null && "MEMBER".equals(trainer.getRole().getName()) || "ADMIN".equals(Objects.requireNonNull(trainer.getRole()).getName())) {
            throw new EntityCreationException("Member cannot assign to package.");
        }

        GymPackage gymPackage = this.gymPackageRepository.findById(request.getGymPackageId())
                .orElseThrow(() -> new EntityNotFoundException("Gym package not found to assign with is "+ request.getGymPackageId()));

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
        assignedGymPackage.setStatus(Status.INACTIVE);
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
}
