package org.java_proj.gym_management_system.config.beans;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String userId = accessor.getFirstNativeHeader("userId");
            if (userId != null && !userId.isEmpty()) {
                try {
                    // Set principal as userId (no UserDetails)
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userId, null, null);
                    accessor.setUser(auth);
                    System.out.println("✅ WebSocket Principal set to userId: " + userId);
                } catch (Exception ex) {
                    System.out.println("❌ Failed to set WebSocket userId: " + ex.getMessage() + " | userId: " + userId);
                    return null;
                }
            } else {
                System.out.println("❌ No userId provided in WebSocket connect");
                return null;
            }
        }

        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
    }
}