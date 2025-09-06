package org._java_proj.gym_management_system.features.superAdmin.dto.response;

import lombok.Builder;
import lombok.Data;
import org._java_proj.gym_management_system.common.constant.Status;

@Data
@Builder
public class SuperAdminDashBoardResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String role;
    private String avatarUrl;
    private Status status;
}
