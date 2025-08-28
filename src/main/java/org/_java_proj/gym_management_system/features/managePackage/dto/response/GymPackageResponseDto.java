package org._java_proj.gym_management_system.features.managePackage.dto.response;

import lombok.Data;
import org._java_proj.gym_management_system.common.constant.GymPackageType;

@Data
public class GymPackageResponseDto {

    private Long id;

    private String name;

    private String description;

    private GymPackageType gymPackageType;

    private double price;

    private String duration;

}
