package org.java_proj.gym_management_system.features.message.controller;

import lombok.RequiredArgsConstructor;
import org.java_proj.gym_management_system.features.message.dto.request.ChatMessageRequest;
import org.java_proj.gym_management_system.features.message.dto.response.ChatMessageResponse;
import org.java_proj.gym_management_system.features.message.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequest request, Principal principal) {
        // 'principal.getName()' gives us the email extracted by the JWT Interceptor!
        String senderEmail = principal.getName();

        // Save and send to recipient
        ChatMessageResponse response = chatService.saveAndSendMessage(senderEmail, request);

        // Echo back to the sender so their UI updates immediately
        messagingTemplate.convertAndSendToUser(
                senderEmail, // Assuming user destination prefix matches email for sender
                "/queue/messages",
                response
        );
    }
}