package org._java_proj.gym_management_system.features.managePackage.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org._java_proj.gym_management_system.common.constant.GymPackageType;

import java.time.LocalDate;

@Data
public class GymPackageCreateRequest {
    @NotNull(message = "Package name is required")
    private String name;

    @NotNull(message = "Description is required")
    private String description;

    @NotNull(message = "Gym package type is required")
    private GymPackageType gymPackageType;

    @NotNull(message = "Package price is required")
    private double price;

    @NotNull(message = "Package price is required")
    private LocalDate startDate;

    @NotNull(message = "Package price is required")
    private LocalDate endDate;

    @NotNull(message = "Duration is required")
    private String duration;

}
