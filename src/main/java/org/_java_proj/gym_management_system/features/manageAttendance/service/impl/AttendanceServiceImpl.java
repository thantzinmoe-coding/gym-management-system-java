package org._java_proj.gym_management_system.features.manageAttendance.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.features.manageAttendance.dto.request.AttendanceCreateRequest;
import org._java_proj.gym_management_system.features.manageAttendance.dto.request.AttendanceUpdateRequest;
import org._java_proj.gym_management_system.features.manageAttendance.dto.response.AttendanceResponseDto;
import org._java_proj.gym_management_system.features.manageAttendance.repository.AttendanceRepository;
import org._java_proj.gym_management_system.features.manageAttendance.service.AttendanceService;
import org._java_proj.gym_management_system.model.Attendance;
import org._java_proj.gym_management_system.model.User;
import org._java_proj.gym_management_system.model.Profile;
// Corrected import
import org._java_proj.gym_management_system.features.users.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ApiResponse createAttendance(AttendanceCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + request.getUserId()));

        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setDate(request.getDate());
        attendance.setTimeIn(request.getTimeIn());
        attendance.setHoursWorked(request.getHoursWorked());

        Attendance savedAttendance = attendanceRepository.save(attendance);
        AttendanceResponseDto dto = mapToDto(savedAttendance);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.CREATED.value())
                .data(Map.of("attendance", dto))
                .message("Attendance recorded successfully.")
                .build();
    }

    @Override
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
            Attendance updatedAttendance = attendanceRepository.save(attendance);
            AttendanceResponseDto dto = mapToDto(updatedAttendance);

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

        Double totalHours = attendanceRepository.getTotalHoursWorkedByUser(userId);

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

        return dto;
    }
}
