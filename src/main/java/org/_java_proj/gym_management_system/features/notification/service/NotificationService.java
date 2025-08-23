package org._java_proj.gym_management_system.features.notification.service;


import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.features.notification.dto.NotificationDTO;
import org._java_proj.gym_management_system.model.Notification;

import java.util.List;

public interface NotificationService {

    ApiResponse saveAndSend(NotificationDTO notificationDTO);

    List<Notification> getNotificationsForUser(Long userId);

    ApiResponse markAsRead(Long id, Long userId);
}
