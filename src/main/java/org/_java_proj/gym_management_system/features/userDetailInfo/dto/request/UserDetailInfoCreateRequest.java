package org._java_proj.gym_management_system.features.userDetailInfo.dto.request;

import lombok.Data;

@Data
public class UserDetailInfoCreateRequest {

    private double weight;

    private double height;

    private String healthInfo;

    private String goal;

    private Integer experience;

    private String specialization;

    private Long entityId;
}
