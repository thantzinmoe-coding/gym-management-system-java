package org._java_proj.gym_management_system.features.message.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._java_proj.gym_management_system.common.util.JWTUtil;
import org._java_proj.gym_management_system.features.message.service.OnlineUserService;
import org._java_proj.gym_management_system.features.users.repository.UserRepository;
import org._java_proj.gym_management_system.model.User;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final OnlineUserService onlineUserService;
    private final JWTUtil jwtUtil; // ✅ your existing JWT utility class
    private final UserRepository userRepository;

    /**
     * Handle WebSocket CONNECT event — mark user online.
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = sha.getSessionId();
        String authHeader = sha.getFirstNativeHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("WebSocket connection without Authorization header!");
            return;
        }

        String token = authHeader.substring(7);
        Long userId = jwtUtil.extractUserId(token);

        if (userId == null) {
            String email = jwtUtil.extractEmail(token);
            userId = userRepository.findByEmail(email)
                    .map(User::getId)
                    .orElse(null);
        }


        onlineUserService.setUserOnline(userId, sessionId);
        log.info("✅ User {} connected (sessionId: {})", userId, sessionId);
    }

    /**
     * Handle WebSocket DISCONNECT event — mark user offline.
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = sha.getSessionId();

        // Query OnlineUser table by sessionId to get userId
        onlineUserService.getOnlineUserBySessionId(sessionId).ifPresent(onlineUser -> {
            onlineUserService.setUserOffline(onlineUser.getUserId(), sessionId);
            log.info("❌ User {} disconnected (sessionId: {})", onlineUser.getUserId(), sessionId);
        });
    }
}
