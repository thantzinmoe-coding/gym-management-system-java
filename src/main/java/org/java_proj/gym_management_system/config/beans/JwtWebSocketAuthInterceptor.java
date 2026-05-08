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

@Slf4j
@Component
@RequiredArgsConstructor // Automatically creates a constructor for 'final' fields
public class JwtWebSocketAuthInterceptor implements ChannelInterceptor {

    // 1. Inject your JWT Utility class here
    private final JWTUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                try {
                    // 2. Extract the actual email dynamically from the token
                    String userEmail = jwtUtil.extractEmail(token);

                    // 3. Verify the token is still valid (not expired, signature matches)
                    if (userEmail != null && jwtUtil.validateToken(token)) {

                        // 4. Attach the real user to the WebSocket session
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(userEmail, null, null);
                        accessor.setUser(auth);

                        log.info("WebSocket connection established for user: {}", userEmail);
                    } else {
                        log.warn("Invalid JWT token provided for WebSocket connection");
                    }
                } catch (Exception e) {
                    log.error("Failed to parse JWT in WebSocket connection: {}", e.getMessage());
                    // If parsing fails, we do not set the user. The connection will be rejected.
                }
            } else {
                log.warn("No JWT token found in WebSocket CONNECT headers");
            }
        }
        return message;
    }
}