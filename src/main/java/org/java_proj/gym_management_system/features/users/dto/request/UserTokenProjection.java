package org.java_proj.gym_management_system.features.users.dto.request;

import java.time.LocalDateTime;

public interface UserTokenProjection {
    String getToken();
    LocalDateTime getCreatedAt();
}
