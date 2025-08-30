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
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    @Override
    public ApiResponse assignedGymPackage(AssignedGymPackageRequest request) {
        // Validate trainer exists and is not a MEMBER
        User trainer = this.userRepository.findById(request.getTrainerID())
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found with id " + request.getTrainerID()));
        if (trainer.getRole() != null && "MEMBER".equals(trainer.getRole().getName()) || "ADMIN".equals(Objects.requireNonNull(trainer.getRole()).getName())) {
            throw new EntityCreationException("Member cannot assign to package.");
        }

        GymPackage gymPackage = this.gymPackageRepository.findById(request.getGymPackageID())
                .orElseThrow(() -> new EntityNotFoundException("Gym package not found to assign with is "+ request.getGymPackageID()));

        this.gymPackageRepository.findByIdAndStatus(request.getGymPackageID(), Status.ACTIVE)
                .orElseThrow(() -> new EntityCreationException("This package is not active to assign"));

        // Check if trainer already has an ACTIVE assignment
        if (assignedGymPackageRepository.existsByTrainerIdAndStatus(request.getTrainerID(), Status.ACTIVE)) {
            throw new EntityCreationException("Trainer " + request.getTrainerID() + " is already assigned to an active gym package.");
        }

        // Check if schedule already has an ACTIVE assignment (to any trainer)
        if (assignedGymPackageRepository.existsByAssignedGymPackageIdAndStatus(request.getGymPackageID(), Status.ACTIVE)) {
            throw new EntityCreationException("Gym package " + request.getGymPackageID() + " is already assigned to an active trainer.");
        }

        AssignedGymPackage assignedGymPackage = new AssignedGymPackage();
        assignedGymPackage.setTrainer(trainer);
        assignedGymPackage.setAssignedGymPackage(gymPackage);

        assignedGymPackageRepository.save(assignedGymPackage);

        AssignedGymPackageResponseDto dto = modelMapper.map(assignedGymPackage, AssignedGymPackageResponseDto.class);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.CREATED.value())
                .data(Map.of("AssignedGymPackage", dto))
                .message("Trainer assigned to a gym package successfully.")
                .build();
    }

    @Override
    public ApiResponse unassignedGymPackage(Long trainerID) {
        // Find ACTIVE assignment for this trainer
        AssignedGymPackage assignedGymPackage = this.assignedGymPackageRepository
                .findByTrainerIdAndStatus(trainerID, Status.ACTIVE);

        if(assignedGymPackage == null){
            throw new EntityNotFoundException("Trainer not assigned to any active schedule with id " + trainerID);
        }

        this.assignedGymPackageRepository.delete(assignedGymPackage);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value()) // Use OK (200) for updates, not CREATED (201)
                .message("Trainer unassigned successfully.")
                .build();
    }
}
