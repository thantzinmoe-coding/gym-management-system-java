package org._java_proj.gym_management_system.features.managePackage.dto.request;

import lombok.Data;

@Data
public class GymPackageUpdateRequest {
    private String name;

    private String description;

    private double price;

    private String duration;
}
