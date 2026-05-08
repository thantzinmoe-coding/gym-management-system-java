package org.java_proj.gym_management_system.features.assignedGymPackage.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Assigning for gym package with trainer")
public class AssignedGymPackageRequest {
    @NotBlank(message = "Trainer id is required")
    @Schema(description = "Trainer id to assign", example = "1")
    private Long trainerId;

    @NotBlank(message = "Gym package id is required")
    @Schema(description = "Gym package id for assigning", example = "1")
    private Long gymPackageId;
}
