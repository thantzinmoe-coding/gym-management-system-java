package org.java_proj.gym_management_system.features.message.controller;

import lombok.RequiredArgsConstructor;
import org.java_proj.gym_management_system.features.message.dto.request.ChatMessageRequest;
import org.java_proj.gym_management_system.features.message.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequest request, Principal principal) {
        String senderEmail = principal != null ? principal.getName() : null;
        chatService.saveAndSendMessage(senderEmail, request);
    }
}
