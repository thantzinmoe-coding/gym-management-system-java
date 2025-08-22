package org._java_proj.gym_management_system.features.notificationManager.service;


import org._java_proj.gym_management_system.features.notificationManager.dto.NotificationDTO;
import org._java_proj.gym_management_system.model.Notification;

import java.util.List;

public interface NotificationService {

    Notification saveAndSend(NotificationDTO notificationDTO);
    List<Notification> getNotificationsForUser(Long userId);
}
