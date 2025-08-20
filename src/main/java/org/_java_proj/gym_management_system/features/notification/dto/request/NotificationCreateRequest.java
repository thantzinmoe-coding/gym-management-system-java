package org._java_proj.gym_management_system.features.notification.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationCreateRequest {
    private String title;
    private String content;
    private LocalDateTime time;
    private Long recipientId;
}
