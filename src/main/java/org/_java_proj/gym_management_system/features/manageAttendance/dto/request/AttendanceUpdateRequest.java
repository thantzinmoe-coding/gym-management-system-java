package org._java_proj.gym_management_system.features.manageAttendance.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AttendanceUpdateRequest {
    private LocalDateTime timeOut;
}
