package org._java_proj.gym_management_system.features.notification.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationResponseDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime time;
    private Long recipientId;
}
