package org._java_proj.gym_management_system.features.message.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypingIndicator {
    private Long userId;
    private String userName;
    private Long studyGroupId; // null for private chat
    private Long recipientId;  // null for group chat
    private Boolean isTyping;
}
