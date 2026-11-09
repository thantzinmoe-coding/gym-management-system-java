package org.java_proj.gym_management_system.features.managePackage.dto.response;

import lombok.Data;

@Data
public class ScheduleSummaryDto {
    private Long id;
    private String day;
    private String startTime;
    private String endTime;
}
