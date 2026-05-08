package org.java_proj.gym_management_system.features.message.controller;

import lombok.RequiredArgsConstructor;
import org.java_proj.gym_management_system.features.message.dto.response.ChatRoomResponse;
import org.java_proj.gym_management_system.features.message.dto.response.ChatMessageResponse;
import org.java_proj.gym_management_system.features.message.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;

    @GetMapping("/rooms/{userId}")
    public ResponseEntity<List<ChatRoomResponse>> getRooms(@PathVariable Long userId) {
        return ResponseEntity.ok(chatService.getUserRooms(userId));
    }

    @GetMapping("/history/{userId}/{otherUserId}")
    public ResponseEntity<List<ChatMessageResponse>> getHistory(
            @PathVariable Long userId, @PathVariable Long otherUserId) {
        return ResponseEntity.ok(chatService.getHistory(userId, otherUserId));
    }
}