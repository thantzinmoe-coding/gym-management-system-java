package org.java_proj.gym_management_system.features.message.dto.response;


import java.time.LocalDateTime;

public record GroupMemberResponse(
        Long id,
        Long userId,
        String userName,
        String role,
        LocalDateTime joinedAt
) {}
