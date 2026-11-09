package org.java_proj.gym_management_system.features.message.controller;

import org.java_proj.gym_management_system.common.util.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.java_proj.gym_management_system.features.message.dto.request.ChatMessageRequest;
import org.java_proj.gym_management_system.features.message.dto.response.ChatRoomResponse;
import org.java_proj.gym_management_system.features.message.dto.response.ChatMessageResponse;
import org.java_proj.gym_management_system.features.message.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping({"/api/chat", "${api.base.path}/chat"})
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;
    private final CurrentUser currentUser;

    @GetMapping("/rooms/{userId}")
    public ResponseEntity<List<ChatRoomResponse>> getRooms(@PathVariable Long userId) {
        currentUser.requireSelf(userId); // chats are private, even from admins
        return ResponseEntity.ok(chatService.getUserRooms(userId));
    }

    @GetMapping("/contacts/{userId}")
    public ResponseEntity<List<ChatRoomResponse>> getContacts(
            @PathVariable Long userId,
            @RequestParam(required = false) String role) {
        currentUser.requireSelf(userId);
        return ResponseEntity.ok(chatService.getChatContacts(userId, role));
    }

    @GetMapping("/history/{userId}/{otherUserId}")
    public ResponseEntity<List<ChatMessageResponse>> getHistory(
            @PathVariable Long userId, @PathVariable Long otherUserId) {
        currentUser.requireSelf(userId);
        return ResponseEntity.ok(chatService.getHistory(userId, otherUserId));
    }

    @PostMapping("/send")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @RequestBody ChatMessageRequest request,
            Principal principal) {
        String senderEmail = principal != null ? principal.getName() : null;
        return ResponseEntity.ok(chatService.saveAndSendMessage(senderEmail, request));
    }
}
