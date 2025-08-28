package org._java_proj.gym_management_system.features.message.service;

import org._java_proj.gym_management_system.features.message.dto.request.ChatMessageRequest;
import org._java_proj.gym_management_system.features.message.dto.response.ChatMessageResponse;
import org._java_proj.gym_management_system.features.message.dto.response.ChatRoomResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ChatService {
    ChatMessageResponse sendPrivateMessage(Long senderId, ChatMessageRequest request);
    ChatMessageResponse sendGroupMessage(Long senderId, ChatMessageRequest request);
    Page<ChatMessageResponse> getPrivateChatHistory(Long userId, Long otherUserId, int page, int size);
    Page<ChatMessageResponse> getGroupChatHistory(Long studyGroupId, int page, int size);
    List<ChatRoomResponse> getChatRooms(Long userId);
    void markMessagesAsRead(Long userId, Long senderId, Long studyGroupId);
}
