package org.java_proj.gym_management_system.features.manageSchedule.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScheduleCreateRequest {

    @NotNull(message = "Schedule day is required")
    private String day;

    @NotNull(message = "Start time is required")
    private String startTime;

    @NotNull(message = "End time is required")
    private String endTime;

}
