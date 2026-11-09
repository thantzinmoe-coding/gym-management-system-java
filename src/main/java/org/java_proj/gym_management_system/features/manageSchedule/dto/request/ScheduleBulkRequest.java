package org.java_proj.gym_management_system.features.manageSchedule.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ScheduleBulkRequest {
    private Long gymPackageId;               // one package
    private List<ScheduleCreateRequest> schedules; // many schedules
}
