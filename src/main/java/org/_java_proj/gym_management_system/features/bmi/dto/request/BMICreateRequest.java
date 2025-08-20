package org._java_proj.gym_management_system.features.bmi.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BMICreateRequest {

    @NotNull(message = "Body weight is required")
    private double weight;

    @NotNull(message = "Height is required")
    private double height;

    @NotNull(message = "Health info is required")
    private String healthInfo;

    @NotNull(message = "Goal is required")
    private String goal;

    @NotNull(message = "Entity ID is required")
    private Long entityId;
}
