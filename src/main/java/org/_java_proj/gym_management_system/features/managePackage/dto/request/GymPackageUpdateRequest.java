package org._java_proj.gym_management_system.features.managePackage.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GymPackageUpdateRequest {
    @NotNull(message = "Package name is required")
    private String name;

    @NotNull(message = "Description is required")
    private String description;

    @NotNull(message = "Package price is required")
    private double price;

    @NotNull(message = "Duration is required")
    private String duration;
}
