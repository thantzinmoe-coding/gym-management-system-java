package org._java_proj.gym_management_system.features.notificationManager.service.impl;

import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.features.notificationManager.dto.UserNotificationDTO;
import org._java_proj.gym_management_system.features.notificationManager.repository.UserNotificationStatusRepository;
import org._java_proj.gym_management_system.features.notificationManager.service.NotificationQueryService;
import org._java_proj.gym_management_system.model.UserNotificationStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationQueryServiceImpl implements NotificationQueryService {

    private final UserNotificationStatusRepository userNotificationStatusRepository;

    @Override
    public List<UserNotificationDTO> getNotificationsForUser(Long userId) {
        List<UserNotificationStatus> statuses = userNotificationStatusRepository.findAllByUserIdOrderByNotificationTimeDesc(userId);

        return statuses.stream().map(uns -> {
            UserNotificationDTO dto = new UserNotificationDTO();
            dto.setId(uns.getNotification().getId());
            dto.setTitle(uns.getNotification().getTitle());
            dto.setContent(uns.getNotification().getContent());
            dto.setTime(uns.getNotification().getTime().toString());
            dto.setSenderId(uns.getNotification().getSender().getId());
            dto.setSenderName(uns.getNotification().getSender().getProfile().getName());
            dto.setRead(Boolean.TRUE.equals(uns.getIsRead()));
            return dto;
        }).toList();
    }
}
