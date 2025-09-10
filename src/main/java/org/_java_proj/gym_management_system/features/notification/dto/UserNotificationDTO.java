package org._java_proj.gym_management_system.features.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserNotificationDTO {
    private Long id;           // notification ID
    private String title;
    private String content;
    private LocalDateTime time;
    private Boolean isRead;
}
