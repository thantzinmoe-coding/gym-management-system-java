package org._java_proj.gym_management_system.features.message.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org._java_proj.gym_management_system.common.constant.MessageType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private Long id;
    private Long senderId;
    private String senderName;
    private String senderAvatarUrl;
    private Long recipientId;
    private Long studyGroupId;
    private String content;
    private MessageType messageType;
    private String attachmentUrl;
    private Boolean isRead;
    private Boolean isEdited;
    private LocalDateTime editedAt;
    private LocalDateTime createdAt;
}
