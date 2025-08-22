package org._java_proj.gym_management_system.config.beans;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Enables STOMP message handling
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enables an in-memory message broker. "/topic" and "/queue" are prefixes for destinations that a client can subscribe to.
        config.enableSimpleBroker("/topic", "/queue");

        // This prefix is used to filter destinations handled by @Controller methods.
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registers the "/ws" endpoint. Clients can connect here for the WebSocket handshake.
        registry.addEndpoint("/ws").withSockJS(); // withSockJS() provides a fallback if WebSockets are not supported.
    }
}
