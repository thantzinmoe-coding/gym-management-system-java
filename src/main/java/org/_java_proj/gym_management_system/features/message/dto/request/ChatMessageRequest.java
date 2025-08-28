package org._java_proj.gym_management_system.features.message.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org._java_proj.gym_management_system.common.constant.MessageType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {
    private String content;
    private Long recipientId; // For private messages
    private Long studyGroupId; // For group messages
    private MessageType messageType;
    private String attachmentUrl;
}
