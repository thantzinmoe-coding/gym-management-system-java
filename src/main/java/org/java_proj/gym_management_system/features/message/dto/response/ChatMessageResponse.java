package org.java_proj.gym_management_system.features.message.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageResponse {
    private Long id;
    private Long senderId;
    private String senderName;
    private Long recipientId;
    private String content;
    private LocalDateTime createdAt;
}