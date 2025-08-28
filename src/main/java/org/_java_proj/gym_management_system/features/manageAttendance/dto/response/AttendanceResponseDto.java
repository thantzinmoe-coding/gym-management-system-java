package org._java_proj.gym_management_system.features.manageAttendance.dto.response;

import lombok.Data;
import org._java_proj.gym_management_system.common.constant.AttendanceType; // Corrected import

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AttendanceResponseDto {
    private Long id;
    private Long userId;
    private String userName;
    private String userRole;
    private LocalDate date;
    private LocalDateTime timeIn;
    private LocalDateTime timeOut;
    private AttendanceType attendanceType;
    private Double hoursWorked;
    private Integer packageDays;
    private Long durationInMinutes;
}
