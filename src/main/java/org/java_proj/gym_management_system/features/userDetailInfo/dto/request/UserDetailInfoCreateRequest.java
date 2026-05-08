package org.java_proj.gym_management_system.features.userDetailInfo.dto.request;

import lombok.Data;

@Data
public class UserDetailInfoCreateRequest {

    private double weight;

    private double height;

    private String goal;

    private String experience;

    private String specialization;
}
