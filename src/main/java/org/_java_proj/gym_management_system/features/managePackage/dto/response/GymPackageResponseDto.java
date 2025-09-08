package org._java_proj.gym_management_system.features.managePackage.dto.response;

import lombok.Data;
import org._java_proj.gym_management_system.common.constant.GymPackageType;
import org._java_proj.gym_management_system.common.constant.Status;

import java.time.LocalDate;
import java.util.List;

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

    private String trainerName;

    private Status status;

    private List<ScheduleSummaryDto> schedules;
}
