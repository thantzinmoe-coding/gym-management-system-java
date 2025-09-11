package org._java_proj.gym_management_system.features.superAdmin.dto.response;

import lombok.Builder;
import lombok.Data;
import org._java_proj.gym_management_system.common.constant.MemberStatus;
import org._java_proj.gym_management_system.common.constant.Status;

@Data
@Builder
public class SuperAdminDashBoardResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String nrc;
    private String dob;
    private String address;
    private String gender;
    private Double weight;
    private Double height;
    private String goal;
    private String specialization;
    private String experience;
    private String role;
    private String packageName;
    private String avatarUrl;
    private Status status;
    private MemberStatus memberStatus;
}
