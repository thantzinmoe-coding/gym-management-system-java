package org._java_proj.gym_management_system.features.manageAttendance.dto.response;

import lombok.Data;
// Corrected import

import java.time.LocalDate;

@Data
public class AttendanceResponseDto {
    private Long id;
    private Long userId;
    private String userName;
    private String userRole;
    private LocalDate date;
    private String timeIn;
    private String timeOut;
    private Double hoursWorked;
}
