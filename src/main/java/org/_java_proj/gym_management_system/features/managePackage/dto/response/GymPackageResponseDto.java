package org._java_proj.gym_management_system.features.managePackage.dto.response;

import lombok.Data;
import org._java_proj.gym_management_system.common.constant.GymPackageType;

import java.time.LocalDate;

@Data
public class GymPackageResponseDto {

    private Long id;

    private String name;

    private String description;

    private GymPackageType gymPackageType;

    private double price;

    private LocalDate startDate;

    private LocalDate endDate;

    private String duration;

}
