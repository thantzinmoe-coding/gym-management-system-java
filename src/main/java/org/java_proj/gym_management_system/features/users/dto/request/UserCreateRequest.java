package org.java_proj.gym_management_system.features.users.dto.request;

import lombok.Data;

@Data
public class UserCreateRequest {
    private String email;
    private String password;
    private String Role;
}
