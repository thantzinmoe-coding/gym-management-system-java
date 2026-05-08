package org.java_proj.gym_management_system.features.users.dto.response;

import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    private String email;
    private String status;
    private String roleName;
}
