package org.java_proj.gym_management_system.config.beans;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.java_proj.gym_management_system.common.util.JWTUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtWebSocketAuthInterceptor implements ChannelInterceptor {

    private final JWTUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                try {
                    String userEmail = jwtUtil.extractEmailAllowExpired(token);

                    if (userEmail != null && !userEmail.isBlank()) {
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(userEmail, null, Collections.emptyList());
                        accessor.setUser(auth);

                        log.info("WebSocket connection established for user: {}", userEmail);
                    } else {
                        log.warn("Invalid JWT token provided for WebSocket connection");
                    }
                } catch (Exception e) {
                    log.error("Failed to parse JWT in WebSocket connection: {}", e.getMessage());
                }
            } else {
                log.warn("No JWT token found in WebSocket CONNECT headers");
            }
        }
        return message;
    }
}
