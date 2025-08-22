package org._java_proj.gym_management_system.features.userDetailInfo.dto.response;

import lombok.Data;

@Data
public class UserDetailInfoResponseDto {
    private double weight;

    private double height;

    private String healthInfo;

    private String goal;

    private Integer experience;

    private String specialization;

    private Long detailInfoId;
}
