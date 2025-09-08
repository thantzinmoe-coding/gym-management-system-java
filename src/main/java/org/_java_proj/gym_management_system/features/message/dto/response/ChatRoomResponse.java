package org._java_proj.gym_management_system.features.message.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatRoomResponse {
    private Long id;
    private Long otherUserId; // Renamed from targetId
    private String otherUserName;
    private String otherUserAvatarUrl;
    private boolean otherUserOnline;
    private ChatMessageResponse lastMessage;
    private int unreadCount;
    private LocalDateTime lastMessageAt;
    private LocalDateTime createdAt;
}