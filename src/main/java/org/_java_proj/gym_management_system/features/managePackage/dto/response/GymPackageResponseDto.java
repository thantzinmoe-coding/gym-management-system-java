package org._java_proj.gym_management_system.features.managePackage.dto.response;

import lombok.Data;
import org._java_proj.gym_management_system.model.Schedule;

import java.util.List;

@Data
public class GymPackageResponseDto {
    private String name;

    private String description;

    private double price;

    private String duration;

}
