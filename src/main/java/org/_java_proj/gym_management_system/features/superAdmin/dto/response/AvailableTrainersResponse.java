package org._java_proj.gym_management_system.features.superAdmin.dto.response;

import lombok.Data;
import org._java_proj.gym_management_system.common.constant.Status;

import java.util.List;

@Data
public class AvailableTrainersResponse {
    private Long id;

    private String name;

    private String email;

    private  String phone;

    private String specialization;

    private String experience;

    private Integer rating;

    private Integer totalClients;

    private Status status;

    private List<GymPackageResponse> packages;
}
