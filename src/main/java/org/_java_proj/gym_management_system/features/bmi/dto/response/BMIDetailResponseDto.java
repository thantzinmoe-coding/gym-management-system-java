package org._java_proj.gym_management_system.features.bmi.dto.response;

import lombok.Data;

@Data
public class BMIDetailResponseDto {
    private double weight;

    private double height;

    private String healthInfo;

    private String goal;

    private Long bmiId;
}
