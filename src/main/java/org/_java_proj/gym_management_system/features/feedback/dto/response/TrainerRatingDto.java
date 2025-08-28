package org._java_proj.gym_management_system.features.feedback.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrainerRatingDto {
    private Long trainerId;
    private String trainerName;
    private Double averageRating;
}
