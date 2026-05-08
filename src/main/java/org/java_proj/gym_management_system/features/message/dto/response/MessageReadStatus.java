package org.java_proj.gym_management_system.features.message.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageReadStatus {
    private Long messageId;
    private Integer userId;
    private LocalDateTime readAt;
}
