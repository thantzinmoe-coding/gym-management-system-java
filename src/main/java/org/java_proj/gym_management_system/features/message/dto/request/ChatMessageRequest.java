package org.java_proj.gym_management_system.features.message.dto.request;

import lombok.Data;

@Data
public class ChatMessageRequest {
    private Long recipientId;
    private String content;
}