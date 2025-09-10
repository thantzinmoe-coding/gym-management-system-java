package org._java_proj.gym_management_system.features.users.dto.request;

public interface UserLoginProjection {
    Long getId();
    String getEmail();
    String getRoleName();
    String getPassword();
    String getStatus();
}
