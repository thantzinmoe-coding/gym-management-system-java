package org._java_proj.gym_management_system.features.notification.service.impl;

import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.features.notification.dto.UserNotificationDTO;
import org._java_proj.gym_management_system.features.notification.repository.UserNotificationStatusRepository;
import org._java_proj.gym_management_system.features.notification.service.NotificationQueryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationQueryServiceImpl implements NotificationQueryService {

    private final UserNotificationStatusRepository userNotificationStatusRepository;

    @Override
    public List<UserNotificationDTO> getNotificationsForUser(Long userId) {
        List<UserNotificationDTO> statuses = userNotificationStatusRepository.findUserNotifications(userId);

        return statuses.stream().map(uns -> {
            UserNotificationDTO dto = new UserNotificationDTO();
            dto.setId(uns.getId());
            dto.setTitle(uns.getTitle());
            dto.setContent(uns.getContent());
            dto.setTime(uns.getTime());
            dto.setIsRead(Boolean.TRUE.equals(uns.getIsRead()));
            return dto;
        }).toList();
    }
}
