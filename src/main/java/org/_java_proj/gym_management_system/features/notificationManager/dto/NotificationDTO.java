package org._java_proj.gym_management_system.features.notificationManager.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDTO {

    private String title;
    private String content;
    private LocalDateTime time;
    private Long recipientId; // Or username
    private Long senderId; // Or username

    // Constructors, Getters, and Setters
}
