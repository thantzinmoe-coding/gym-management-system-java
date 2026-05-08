package org.java_proj.gym_management_system.features.superAdmin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeletedUserResponse {
    private Long id;
    private String email;
    private String role;
    private String status;
    private String deletedAt;
}