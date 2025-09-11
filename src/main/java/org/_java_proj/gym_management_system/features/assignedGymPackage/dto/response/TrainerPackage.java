package org._java_proj.gym_management_system.features.assignedGymPackage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org._java_proj.gym_management_system.common.constant.GymPackageType;
import org._java_proj.gym_management_system.features.managePackage.dto.response.ScheduleSummaryDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainerPackage {
    private String id;
    private String name;
    private String duration;
    private double price;
    private int clientsEnrolled;
    private GymPackageType type;
    private String startDate;
    private String endDate;
    private String description;
    private List<ScheduleSummaryDto> schedule;
    private String status; // 'active', 'completed', or 'upcoming'
}

