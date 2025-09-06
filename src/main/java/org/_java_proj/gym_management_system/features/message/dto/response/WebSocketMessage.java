package org._java_proj.gym_management_system.features.message.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage<T> {
    private String type;
    private T data;
    private String timestamp;
    private String sessionId;

    public static <T> WebSocketMessage<T> of(String type, T data) {
        return WebSocketMessage.<T>builder()
                .type(type)
                .data(data)
                .timestamp(java.time.Instant.now().toString())
                .build();
    }
}