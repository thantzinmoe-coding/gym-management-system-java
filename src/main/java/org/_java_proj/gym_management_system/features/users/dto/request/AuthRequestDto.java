package org._java_proj.gym_management_system.features.users.dto.request;

import lombok.Data;

@Data
public class AuthRequestDto {
    private String email;
    private String password;
}
