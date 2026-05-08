package org.java_proj.gym_management_system.features.manageAttendance.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.java_proj.gym_management_system.common.constant.Status;
import org.java_proj.gym_management_system.config.response.dto.ApiResponse;
import org.java_proj.gym_management_system.features.manageAttendance.dto.request.AttendanceCreateRequest;
import org.java_proj.gym_management_system.features.manageAttendance.dto.request.AttendanceUpdateRequest;
import org.java_proj.gym_management_system.features.manageAttendance.dto.response.AttendanceResponseDto;
import org.java_proj.gym_management_system.features.manageAttendance.repository.AttendanceRepository;
import org.java_proj.gym_management_system.features.manageAttendance.service.AttendanceService;
import org.java_proj.gym_management_system.features.salary.repository.SalaryRepository;
import org.java_proj.gym_management_system.model.Attendance;
import org.java_proj.gym_management_system.model.Salary;
import org.java_proj.gym_management_system.model.User;
import org.java_proj.gym_management_system.model.Profile;
import org.java_proj.gym_management_system.common.constant.AttendanceType; // Corrected import
import org.java_proj.gym_management_system.features.users.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final SalaryRepository salaryRepository;

    @Override
    @Transactional
    public ApiResponse createAttendance(AttendanceCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + request.getUserId()));

        Attendance attendance = getAttendance(request, user);

        Attendance savedAttendance = attendanceRepository.save(attendance);
        AttendanceResponseDto dto = mapToDto(savedAttendance);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.CREATED.value())
                .data(Map.of("attendance", dto))
                .message("Attendance recorded successfully.")
                .build();
    }

    private static Attendance getAttendance(AttendanceCreateRequest request, User user) {
        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setDate(request.getDate());
        attendance.setTimeIn(request.getTimeIn());
        attendance.setAttendanceType(request.getAttendanceType());

        if (request.getAttendanceType() == AttendanceType.TRAINER) {
            attendance.setHoursWorked(request.getHoursWorked());
        } else if (request.getAttendanceType() == AttendanceType.MEMBER) {
            attendance.setPackageDays(request.getPackageDays());
        }

        if(Objects.equals(request.getStatus(), "ACTIVE")) {
            attendance.setStatus(Status.ACTIVE);
        } else {
            attendance.setStatus(Status.INACTIVE);
        }
        return attendance;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ApiResponse getAttendance(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attendance not found with ID: " + id));

        AttendanceResponseDto dto = mapToDto(attendance);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of("attendance", dto))
                .message("Attendance fetched successfully.")
                .build();
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    @Override
    public ApiResponse listAllAttendances() {
        List<Attendance> attendances = attendanceRepository.findAll();
        List<AttendanceResponseDto> dto = attendances.stream()
                .map(this::mapToDto)
                .toList();

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of("attendances", dto))
                .message("All attendance records fetched.")
                .build();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ApiResponse getAttendancesByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        List<Attendance> userAttendances = attendanceRepository.findByUser(user);
        List<AttendanceResponseDto> dto = userAttendances.stream()
                .map(this::mapToDto)
                .toList();

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of("attendances", dto))
                .message("Attendance records for user fetched.")
                .build();
    }

    @Override
    @Transactional
    public ApiResponse updateAttendance(Long id, AttendanceUpdateRequest request) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attendance not found with ID: " + id));

        if (request.getTimeOut() != null && attendance.getTimeOut() == null) {
            attendance.setTimeOut(request.getTimeOut());
            attendance.setHoursWorked(request.getHoursWorked());
            Attendance updatedAttendance = attendanceRepository.save(attendance);
            AttendanceResponseDto dto = mapToDto(updatedAttendance);

            LocalDate now = LocalDate.now();
            int month = now.getMonthValue();
            int year = now.getYear();

            User trainer = attendance.getUser();

            boolean recordExists = salaryRepository
                    .findByTrainerIdAndSalaryYearAndSalaryMonth(trainer.getId(), year, month)
                    .isPresent();

            Salary pendingSalary = new Salary();
            if (!recordExists) {

                pendingSalary.setTrainer(trainer);
                pendingSalary.setAmount(0.0);
                pendingSalary.setPaymentDate(LocalDate.now());
                pendingSalary.setSalaryMonth(month);
                pendingSalary.setSalaryYear(year);
                pendingSalary.setStatus(Status.PENDING);
                pendingSalary.setNotes("Pending payment for " + now.getMonth().name() + " " + year);

                salaryRepository.save(pendingSalary);
                System.out.println("Created PENDING salary for " + trainer.getProfile().getName());
            }

            return ApiResponse.builder()
                    .success(1)
                    .code(HttpStatus.OK.value())
                    .data(Map.of("attendance", dto))
                    .message("Attendance updated successfully (time-out recorded).")
                    .build();
        } else if (request.getTimeOut() == null) {
            AttendanceResponseDto dto = mapToDto(attendance);
            return ApiResponse.builder()
                    .success(1)
                    .code(HttpStatus.OK.value())
                    .data(Map.of("attendance", dto))
                    .message("No update performed: time-out was not provided or already set.")
                    .build();
        } else {
            return ApiResponse.builder()
                    .success(0)
                    .code(HttpStatus.BAD_REQUEST.value())
                    .data(Map.of("attendance", mapToDto(attendance)))
                    .message("Attendance time-out already recorded.")
                    .build();
        }
    }

    @Override
    @Transactional
    public ApiResponse deleteAttendance(Long id) {
        attendanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attendance not found with ID: " + id));

        attendanceRepository.deleteById(id);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Attendance deleted successfully.")
                .build();
    }

    @Override
    public ApiResponse getTotalHoursWorkedByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        Double totalHours = attendanceRepository.getTotalHoursWorkedByUserForCurrentMonth(userId);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of(
                        "userId", userId,
                        "totalHoursWorked", totalHours
                ))
                .message("Total hours worked calculated successfully.")
                .build();
    }

    private AttendanceResponseDto mapToDto(Attendance attendance) {
        AttendanceResponseDto dto = new AttendanceResponseDto();
        dto.setId(attendance.getId());
        dto.setDate(attendance.getDate());
        dto.setTimeIn(attendance.getTimeIn());
        dto.setTimeOut(attendance.getTimeOut());

        if (attendance.getUser() != null) {
            dto.setUserId(attendance.getUser().getId());

            Profile userProfile = attendance.getUser().getProfile();

            if (userProfile != null) {
                dto.setUserName(userProfile.getName());
            } else {
                dto.setUserName("No Profile");
            }

            if (attendance.getUser().getRole() != null) {
                dto.setUserRole(attendance.getUser().getRole().getName());
            }
        }
        dto.setPackageDays(attendance.getPackageDays());
        dto.setAttendanceType(attendance.getAttendanceType());
        dto.setStatus(attendance.getStatus());

        return dto;
    }
}
