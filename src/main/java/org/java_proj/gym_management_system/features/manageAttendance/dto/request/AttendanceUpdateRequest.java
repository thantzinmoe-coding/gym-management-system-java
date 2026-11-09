package org.java_proj.gym_management_system.features.manageAttendance.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request to update a existing attendance")
public class AttendanceUpdateRequest {
    @Schema(description = "Work hours for trainer", example = "5")
    private Double hoursWorked;

    @Schema(description = "Attendance time out", example = "17:00")
    private String timeOut;
}
