package org._java_proj.gym_management_system.features.assignedSchedule.service.impl;

import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.common.constant.Status;
import org._java_proj.gym_management_system.config.exceptions.EntityCreationException;
import org._java_proj.gym_management_system.config.exceptions.EntityNotFoundException;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.features.assignedSchedule.dto.request.AssignedScheduleRequest;
import org._java_proj.gym_management_system.features.assignedSchedule.dto.response.AssignedScheduleResponseDto;
import org._java_proj.gym_management_system.features.assignedSchedule.repository.AssignedScheduleRepository;
import org._java_proj.gym_management_system.features.assignedSchedule.service.AssignedScheduleService;
import org._java_proj.gym_management_system.features.manageSchedule.repository.ScheduleRepository;
import org._java_proj.gym_management_system.features.users.repository.UserRepository;
import org._java_proj.gym_management_system.model.AssignedGymSchedule;
import org._java_proj.gym_management_system.model.Schedule;
import org._java_proj.gym_management_system.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssignedScheduleServiceImpl implements AssignedScheduleService {
    private final AssignedScheduleRepository assignedScheduleRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final ModelMapper modelMapper;

    @Override
    public ApiResponse assignedSchedule(AssignedScheduleRequest request) {
        // Validate trainer exists and is not a MEMBER
        User trainer = this.userRepository.findById(request.getTrainerID())
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found with id " + request.getTrainerID()));
        if (trainer.getRole() != null && "MEMBER".equals(trainer.getRole().getName())) {
            throw new EntityCreationException("Member cannot assign to schedule.");
        }

        // Validate schedule exists
        Schedule schedule = this.scheduleRepository.findById(request.getScheduleID())
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found with id " + request.getScheduleID()));

        // Check if trainer already has an ACTIVE assignment
        if (assignedScheduleRepository.existsByTrainerIdAndStatus(request.getTrainerID(), Status.ACTIVE)) {
            throw new EntityCreationException("Trainer " + request.getTrainerID() + " is already assigned to an active schedule.");
        }

        // Check if schedule already has an ACTIVE assignment (to any trainer)
        if (assignedScheduleRepository.existsByAssignedGymScheduleIdAndStatus(request.getScheduleID(), Status.ACTIVE)) {
            throw new EntityCreationException("Schedule " + request.getScheduleID() + " is already assigned to an active trainer.");
        }

        // Check for existing INACTIVE assignment for this exact trainer and schedule
        Optional<AssignedGymSchedule> existingInactive = assignedScheduleRepository
                .findByTrainerIdAndAssignedGymScheduleIdAndStatus(request.getTrainerID(), request.getScheduleID(), Status.INACTIVE);

        AssignedGymSchedule assignedGymSchedule;
        String message;
        if (existingInactive.isPresent()) {
            // Reactivate the existing inactive assignment
            assignedGymSchedule = existingInactive.get();
            assignedGymSchedule.setStatus(Status.ACTIVE);
            // Reset deletedAt if your delete() sets it
            assignedGymSchedule.setDeletedAt(null); // Adjust if needed
            message = "Trainer reassigned to previously assigned schedule successfully";
        } else {
            // Create a new assignment
            assignedGymSchedule = new AssignedGymSchedule();
            assignedGymSchedule.setTrainer(trainer);
            assignedGymSchedule.setAssignedGymSchedule(schedule);
            assignedGymSchedule.setStatus(Status.ACTIVE);
            message = "Trainer assigned to schedule successfully";
        }

        assignedScheduleRepository.save(assignedGymSchedule);

        AssignedScheduleResponseDto dto = modelMapper.map(assignedGymSchedule, AssignedScheduleResponseDto.class);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.CREATED.value())
                .data(Map.of("AssignedGymSchedule", dto))
                .message(message)
                .build();
    }

    @Override
    public ApiResponse unassignedSchedule(Long trainerID) {
        // Find ACTIVE assignment for this trainer
        AssignedGymSchedule assignedGymSchedule = this.assignedScheduleRepository
                .findByTrainerIdAndStatus(trainerID, Status.ACTIVE);

        if(assignedGymSchedule == null){
            throw new EntityNotFoundException("Trainer not assigned to any active schedule with id " + trainerID);
        }

        // Soft delete (set inactive)
        assignedGymSchedule.delete(); // Assuming this sets status = INACTIVE and possibly deletedAt

        this.assignedScheduleRepository.save(assignedGymSchedule);
        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value()) // Use OK (200) for updates, not CREATED (201)
                .message("Trainer unassigned successfully.")
                .build();
    }
}
