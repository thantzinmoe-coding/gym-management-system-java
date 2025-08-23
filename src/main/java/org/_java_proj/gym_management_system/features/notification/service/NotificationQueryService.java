package org._java_proj.gym_management_system.features.notification.service;

import org._java_proj.gym_management_system.features.notification.dto.UserNotificationDTO;

import java.util.List;

public interface NotificationQueryService {
    List<UserNotificationDTO> getNotificationsForUser(Long userId);
}
