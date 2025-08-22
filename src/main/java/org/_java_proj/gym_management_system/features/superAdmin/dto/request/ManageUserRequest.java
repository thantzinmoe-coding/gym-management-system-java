package org._java_proj.gym_management_system.features.superAdmin.dto.request;

import lombok.Data;

@Data
public class ManageUserRequest {
    private Long userId;
    private String userEmail;
}
