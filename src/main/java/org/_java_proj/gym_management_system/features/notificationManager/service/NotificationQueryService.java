package org._java_proj.gym_management_system.features.notificationManager.service;

import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.features.notificationManager.dto.UserNotificationDTO;

import java.util.List;

public interface NotificationQueryService {
    List<UserNotificationDTO> getNotificationsForUser(Long userId);
}
