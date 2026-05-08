package org.java_proj.gym_management_system.features.feedback.dto.response;

import lombok.Data;
import lombok.Builder;
import org.java_proj.gym_management_system.model.User;

@Data
@Builder


public class UserSummaryDto {
    private Long id;
    private String email;
    private String roleName;

   // public UserSummaryDto() {}

    public static UserSummaryDto fromUser(User user) {
        if (user == null) return null;
        return UserSummaryDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .roleName(user.getRole() != null ? user.getRole().getName() : null)
                .build();
    }
}
