package org._java_proj.gym_management_system.features.superAdmin.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class GetAllTrainersResponse {
    private List<TrainerResponseDto> trainers;
}
