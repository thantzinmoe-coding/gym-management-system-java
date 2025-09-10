package org._java_proj.gym_management_system.features.message.service;

import org._java_proj.gym_management_system.config.response.dto.PaginatedApiResponse;
import org._java_proj.gym_management_system.features.message.dto.request.ChatMessageRequest;
import org._java_proj.gym_management_system.features.message.dto.response.ChatMessageResponse;
import org._java_proj.gym_management_system.features.message.dto.response.ChatRoomResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ChatService {
    ChatMessageResponse sendPrivateMessage(Long senderId, ChatMessageRequest request);

    PaginatedApiResponse<ChatMessageResponse> getPrivateChatHistory(
            Long userId, Long otherUserId, int page, int size);

    List<ChatRoomResponse> getChatRooms(Long userId);
    void markMessagesAsRead(Long userId, Long senderId);
}