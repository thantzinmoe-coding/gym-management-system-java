package org._java_proj.gym_management_system.features.message.dto.response;

import lombok.Builder;
import lombok.Data;
import org._java_proj.gym_management_system.common.constant.MessageType;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageResponse {
    private Long id;
    private Long senderId;
    private String senderName;
    private String senderAvatarUrl;
    private Long recipientId;
    private String content;
    private MessageType messageType;
    private String attachmentUrl;
    private boolean isRead;
    private boolean isEdited;
    private LocalDateTime editedAt;
    private LocalDateTime createdAt;
}