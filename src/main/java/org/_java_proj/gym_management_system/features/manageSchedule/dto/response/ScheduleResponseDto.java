package org._java_proj.gym_management_system.features.manageSchedule.dto.response;

import lombok.Data;

@Data
public class ScheduleResponseDto {
    private Long id;
    private String day;
    private String startTime;
    private String endTime;
    private String trainerName;
    private Long packageId;
    private String packageName;
}
