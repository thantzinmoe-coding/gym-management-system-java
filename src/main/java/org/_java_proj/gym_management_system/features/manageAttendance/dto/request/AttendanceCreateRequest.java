package org._java_proj.gym_management_system.features.manageAttendance.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org._java_proj.gym_management_system.common.constant.AttendanceType; // Corrected import

import java.time.LocalDate;

@Data
@Schema(description = "Request to create a new attendance")
public class AttendanceCreateRequest {
    @Schema(description = "User Id", example = "1")
    private Long userId;

    @Schema(description = "Attendance date", format = "date", example = "2025-08-28")
    private LocalDate date;

    @Schema(description = "Attendance time in", example = "3PM")
    private String timeIn;

    @Schema(description = "Attendance type", example = "MEMBER")
    private AttendanceType attendanceType;

    @Schema(description = "Working hours for trainer", example = "3")
    private Double hoursWorked; // For trainers

    @Schema(description = "Package days", example = "1")
    private Integer packageDays; // For users
}
