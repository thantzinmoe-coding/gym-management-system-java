package org._java_proj.gym_management_system.features.message.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._java_proj.gym_management_system.features.message.dto.request.ChatMessageRequest;
import org._java_proj.gym_management_system.features.message.dto.response.ChatMessageResponse;
import org._java_proj.gym_management_system.features.message.dto.response.MessageReadStatus;
import org._java_proj.gym_management_system.features.message.dto.response.TypingIndicator;
import org._java_proj.gym_management_system.features.message.dto.response.WebSocketMessage;
import org._java_proj.gym_management_system.features.message.service.ChatService;
import org._java_proj.gym_management_system.model.User;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        try {
            ChatMessageResponse response;
            if (request.getStudyGroupId() != null) {
                // Group message
                response = chatService.sendGroupMessage(user.getId(), request);
            } else {
                // Private message
                response = chatService.sendPrivateMessage(user.getId(), request);
            }
            log.info("Message sent successfully: {}", response.getId());
        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage());
            // Send error back to sender
            messagingTemplate.convertAndSendToUser(
                    user.getEmail(),
                    "/queue/errors",
                    WebSocketMessage.of("MESSAGE_ERROR", e.getMessage())
            );
        }
    }

    @MessageMapping("/chat.typing")
    public void handleTypingIndicator(@Payload TypingIndicator typingIndicator, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        typingIndicator.setUserId(user.getId());
        typingIndicator.setUserName(user.getProfile().getName());

        if (typingIndicator.getStudyGroupId() != null) {
            // Group typing indicator
            messagingTemplate.convertAndSend(
                    "/topic/group/" + typingIndicator.getStudyGroupId() + "/typing",
                    typingIndicator
            );
        } else if (typingIndicator.getRecipientId() != null) {
            // Private typing indicator
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(typingIndicator.getRecipientId()),
                    "/queue/typing",
                    typingIndicator
            );
        }
    }

    @MessageMapping("/chat.markRead")
    public void markMessagesAsRead(@Payload MessageReadStatus readStatus, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        try {
            // Implementation depends on your business logic
            // This could mark specific messages or all messages in a conversation as read
            log.info("Messages marked as read by user: {}", user.getId());
        } catch (Exception e) {
            log.error("Error marking messages as read: {}", e.getMessage());
        }
    }
}
