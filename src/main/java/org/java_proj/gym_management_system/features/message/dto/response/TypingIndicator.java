package org.java_proj.gym_management_system.features.message.dto.response;

import lombok.Data;

@Data
public class TypingIndicator {
    private Long userId;
    private String userName;
    private Long recipientId;
}