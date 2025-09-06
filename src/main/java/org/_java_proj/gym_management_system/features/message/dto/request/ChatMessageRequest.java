package org._java_proj.gym_management_system.features.message.dto.request;

import lombok.Data;
import org._java_proj.gym_management_system.common.constant.MessageType;
import jakarta.validation.constraints.NotNull;

@Data
public class ChatMessageRequest {
    @NotNull
    private String content;
    @NotNull // Ensure recipientId is required for private messages
    private Long recipientId;
    @NotNull
    private MessageType messageType;
    private String attachmentUrl;
}