package org._java_proj.gym_management_system.features.message.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._java_proj.gym_management_system.features.message.dto.request.ChatMessageRequest;
import org._java_proj.gym_management_system.features.message.dto.response.ChatMessageResponse;
import org._java_proj.gym_management_system.features.message.dto.response.TypingIndicator;
import org._java_proj.gym_management_system.features.message.dto.response.WebSocketMessage;
import org._java_proj.gym_management_system.features.message.service.ChatService;
import org._java_proj.gym_management_system.features.users.repository.UserRepository;
import org._java_proj.gym_management_system.model.User;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessageSendingOperations messagingTemplate;
    private final UserRepository userRepository;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageRequest request, @AuthenticationPrincipal String userId) {
        try {
            Long senderId = Long.parseLong(userId);
            User sender = userRepository.findById(senderId)
                    .orElseThrow(() -> new IllegalArgumentException("Sender not found: " + userId));
            User recipient = userRepository.findById(request.getRecipientId())
                    .orElseThrow(() -> new IllegalArgumentException("Recipient not found: " + request.getRecipientId()));
            ChatMessageResponse response = chatService.sendPrivateMessage(senderId, request);
            messagingTemplate.convertAndSendToUser(
                    recipient.getEmail(),
                    "/queue/messages",
                    WebSocketMessage.of("NEW_MESSAGE", response)
            );
            // Notify sender
            messagingTemplate.convertAndSendToUser(
                    sender.getEmail(),
                    "/queue/messages",
                    WebSocketMessage.of("NEW_MESSAGE", response)
            );
            log.info("Message sent successfully: {}", response.getId());
        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage());
            messagingTemplate.convertAndSendToUser(
                    userId,
                    "/queue/errors",
                    WebSocketMessage.of("MESSAGE_ERROR", e.getMessage())
            );
        }
    }

    @MessageMapping("/chat.typing")
    public void handleTypingIndicator(@Payload TypingIndicator typingIndicator, @AuthenticationPrincipal String userId) {
        try {
            Long senderId = Long.parseLong(userId);
            User sender = userRepository.findById(senderId)
                    .orElseThrow(() -> new IllegalArgumentException("Sender not found: " + userId));
            User recipient = userRepository.findById(typingIndicator.getRecipientId())
                    .orElseThrow(() -> new IllegalArgumentException("Recipient not found: " + typingIndicator.getRecipientId()));
            typingIndicator.setUserId(senderId);
            typingIndicator.setUserName(sender.getProfile().getName());
            messagingTemplate.convertAndSendToUser(
                    recipient.getEmail(),
                    "/queue/typing",
                    typingIndicator
            );
        } catch (Exception e) {
            log.error("Error handling typing indicator: {}", e.getMessage());
            messagingTemplate.convertAndSendToUser(
                    userId,
                    "/queue/errors",
                    WebSocketMessage.of("TYPING_ERROR", e.getMessage())
            );
        }
    }

    @MessageMapping("/chat.markRead")
    public void markMessagesAsRead(@Payload Long senderId, @AuthenticationPrincipal String userId) {
        try {
            Long recipientId = Long.parseLong(userId);
            chatService.markMessagesAsRead(recipientId, senderId);
            log.info("Messages marked as read by user: {}", recipientId);
        } catch (Exception e) {
            log.error("Error marking messages as read: {}", e.getMessage());
            messagingTemplate.convertAndSendToUser(
                    userId,
                    "/queue/errors",
                    WebSocketMessage.of("MARK_READ_ERROR", e.getMessage())
            );
        }
    }
}