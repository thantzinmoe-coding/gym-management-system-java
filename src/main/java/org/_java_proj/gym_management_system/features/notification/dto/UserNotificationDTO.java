package org._java_proj.gym_management_system.features.notification.dto;

import lombok.Data;

@Data
public class UserNotificationDTO {
    private Long id;           // notification ID
    private String title;
    private String content;
    private String time;
    private Long senderId;
    private String senderName;
    private boolean isRead;
}
