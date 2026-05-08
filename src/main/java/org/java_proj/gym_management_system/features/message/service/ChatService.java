package org.java_proj.gym_management_system.features.message.service;

import org.java_proj.gym_management_system.features.message.dto.request.ChatMessageRequest;
import org.java_proj.gym_management_system.features.message.dto.response.ChatMessageResponse;
import org.java_proj.gym_management_system.features.message.dto.response.ChatRoomResponse;

import java.util.List;

public interface ChatService {
    ChatMessageResponse saveAndSendMessage(String senderEmail, ChatMessageRequest request);
    List<ChatMessageResponse> getHistory(Long userId, Long otherUserId);
    List<ChatRoomResponse> getUserRooms(Long userId);
}