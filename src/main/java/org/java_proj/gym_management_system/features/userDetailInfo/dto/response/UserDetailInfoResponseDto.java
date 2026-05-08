package org.java_proj.gym_management_system.features.userDetailInfo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailInfoResponseDto {
    private double weight;

    private double height;

    private String goal;

    private String experience;

    private String specialization;

    private Long detailInfoId;

    public UserDetailInfoResponseDto(double weight, double height, String goal, String experience, String specialization) {
        this.weight = weight;
        this.height = height;
        this.goal = goal;
        this.experience = experience;
        this.specialization = specialization;
    }
}
