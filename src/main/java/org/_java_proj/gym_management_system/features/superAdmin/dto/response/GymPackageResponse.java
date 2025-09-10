package org._java_proj.gym_management_system.features.superAdmin.dto.response;

import lombok.Data;

@Data
public class GymPackageResponse {
    private Long id;
    private String name;
    private Double price;
    private String type;
    private String description;
}

