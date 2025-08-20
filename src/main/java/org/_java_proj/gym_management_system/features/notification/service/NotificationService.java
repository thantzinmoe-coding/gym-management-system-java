package org._java_proj.gym_management_system.features.notification.service;

import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.features.notification.dto.request.NotificationCreateRequest;
import org._java_proj.gym_management_system.features.notification.dto.request.NotificationUpdateRequest;

public interface NotificationService {
    ApiResponse createNotification(NotificationCreateRequest request);
    ApiResponse getNotification(Long id);
    ApiResponse listNotifications();
    ApiResponse updateNotification(Long id, NotificationUpdateRequest request);
    ApiResponse deleteNotification(Long id);
    ApiResponse getNotificationsForUser(Long userId);
}
