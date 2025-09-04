package org._java_proj.gym_management_system.config.beans;

import org._java_proj.gym_management_system.common.util.JWTUtil;
import org._java_proj.gym_management_system.features.users.service.impl.UserDetailServiceImpl;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import org.springframework.security.core.userdetails.UserDetails;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JWTUtil jwtUtil;
    private final UserDetailServiceImpl userDetailsService;

    public WebSocketAuthInterceptor(JWTUtil jwtUtil, UserDetailServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (accessor.getCommand() != null && accessor.getCommand().getMessageType().name().equals("CONNECT")) {
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                String jwt = token.substring(7);
                try {
                    if (jwtUtil.validateToken(jwt)) {
                        String email = jwtUtil.extractEmail(jwt);
                        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        accessor.setUser(auth); // ✅ sets Principal for WebSocket session
                        System.out.println("✅ WebSocket Principal set to: " + email);
                    } else {
                        System.out.println("❌ Invalid JWT during WebSocket connect");
                    }
                } catch (Exception ex) {
                    System.out.println("❌ Failed to authenticate WebSocket token: " + ex.getMessage());
                }
            }
        }

        return message;
    }
}


