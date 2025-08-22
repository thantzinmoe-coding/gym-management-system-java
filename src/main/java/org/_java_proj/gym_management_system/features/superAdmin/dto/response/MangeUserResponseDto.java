package org._java_proj.gym_management_system.features.superAdmin.dto.response;

import lombok.Data;
import org._java_proj.gym_management_system.common.constant.Status;

@Data
public class MangeUserResponseDto {
    private Long trainerId;
    private String trainerName;
    private Status trainerStatus;
    private String acceptedTime;
}
