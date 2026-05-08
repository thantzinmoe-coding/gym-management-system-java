package org.java_proj.gym_management_system.features.message.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatRoomResponse {
    private Long id;
    private Long otherUserId;
    private String otherUserName;
    private ChatMessageResponse lastMessage;
}