package org.java_proj.gym_management_system.features.assignedGymPackage.dto.response;

import lombok.Data;

@Data
public class AssignedGymPackageResponseDto {
    private Long id;

    private Long trainerId;

    private String trainerName;

    private Long gymPackageId;
}
