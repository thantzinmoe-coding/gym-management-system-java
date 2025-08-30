package org._java_proj.gym_management_system.features.message.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponse {
    private Long id;
    private Long otherUserId;
    private String otherUserName;
    private String otherUserAvatarUrl;
    private Boolean otherUserOnline;
    private ChatMessageResponse lastMessage;
    private Integer unreadCount;
    private LocalDateTime lastMessageAt;
    private LocalDateTime createdAt;
}
