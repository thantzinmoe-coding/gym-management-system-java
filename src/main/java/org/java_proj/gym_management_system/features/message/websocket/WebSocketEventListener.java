package org.java_proj.gym_management_system.features.message.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.java_proj.gym_management_system.common.util.JWTUtil;
import org.java_proj.gym_management_system.features.message.service.OnlineUserService;
import org.java_proj.gym_management_system.features.users.repository.UserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final OnlineUserService onlineUserService;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String authHeader = sha.getFirstNativeHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return;

        String token = authHeader.substring(7);

        // 1. Extract directly from the token claims (No DB hit)
        Long userId = jwtUtil.extractUserId(token);

        if (userId != null) {
            String sessionId = sha.getSessionId();
            onlineUserService.setUserOnline(userId, sessionId);

            // 2. Broadcast online status using the ID
            messagingTemplate.convertAndSend("/topic/online-status", Map.of(userId, true));

            log.info("✅ User {} connected via JWT claim", userId);
        } else {
            log.error("❌ Token valid but 'userId' claim missing!");
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = StompHeaderAccessor.wrap(event.getMessage()).getSessionId();
        onlineUserService.getOnlineUserBySessionId(sessionId).ifPresent(onlineUser -> {
            onlineUserService.setUserOffline(onlineUser.getUserId(), sessionId);

            // ✅ Broadcast offline status to all clients
            messagingTemplate.convertAndSend("/topic/online-status", Map.of(onlineUser.getUserId(), false));

            log.info("❌ User {} disconnected (sessionId: {})", onlineUser.getUserId(), sessionId);
        });
    }
}

