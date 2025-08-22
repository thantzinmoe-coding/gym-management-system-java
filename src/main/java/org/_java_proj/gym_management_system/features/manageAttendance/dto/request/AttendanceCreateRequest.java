package org._java_proj.gym_management_system.features.manageAttendance.dto.request;

import lombok.Data;
import org._java_proj.gym_management_system.common.constant.AttendanceType; // Corrected import

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AttendanceCreateRequest {
    private Long userId;
    private LocalDate date;
    private LocalDateTime timeIn;
    private AttendanceType attendanceType;
    private Double hoursWorked; // For trainers
    private Integer packageDays; // For users
}
