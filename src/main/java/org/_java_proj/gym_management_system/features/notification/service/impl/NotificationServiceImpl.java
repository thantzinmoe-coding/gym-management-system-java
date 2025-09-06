package org._java_proj.gym_management_system.features.notification.service.impl;

import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.features.notification.dto.NotificationDTO;
import org._java_proj.gym_management_system.features.notification.repository.NotificationRepository;
import org._java_proj.gym_management_system.features.notification.repository.UserNotificationStatusRepository;
import org._java_proj.gym_management_system.features.notification.service.NotificationService;
import org._java_proj.gym_management_system.features.users.repository.UserRepository;
import org._java_proj.gym_management_system.model.Notification;
import org._java_proj.gym_management_system.model.User;
import org._java_proj.gym_management_system.model.UserNotificationStatus;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate; // For sending WebSocket messages
    private final UserRepository userRepository; // Assume this exists
    private final UserNotificationStatusRepository userNotificationStatusRepository;


    @Override
    public ApiResponse saveAndSend(NotificationDTO notificationDTO) {
        Notification notification = new Notification();
        notification.setTitle(notificationDTO.getTitle());
        notification.setContent(notificationDTO.getContent());
        notification.setTime(LocalDateTime.now());

        User sender = userRepository.findById(notificationDTO.getSenderId()).orElse(null);
        notification.setSender(sender);

        Notification savedNotification = notificationRepository.save(notification);

        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            UserNotificationStatus status = new UserNotificationStatus();
            status.setUser(user);
            status.setNotification(savedNotification);
            status.setIsRead(false);
            userNotificationStatusRepository.save(status);
        }

        messagingTemplate.convertAndSend("/topic/notifications", mapToDTO(savedNotification));

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of("Notification", mapToDTO(savedNotification)))
                .message("Broadcast sent to all users")
                .build();
    }

    // Convert Notification to DTO for WebSocket
    private NotificationDTO mapToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setContent(notification.getContent());
        dto.setTime(notification.getTime().toString());
        dto.setSenderId(notification.getSender().getId());
        return dto;
    }

    @Override
    public List<Notification> getNotificationsForUser(Long userId) {
        List<Notification> personalNotifications = notificationRepository.findByRecipientIdOrderByTimeDesc(userId);
        List<Notification> broadcastNotifications = notificationRepository.findByRecipientIsNullOrderByTimeDesc();

        personalNotifications.addAll(broadcastNotifications);

        return personalNotifications.stream()
                .sorted((n1, n2) -> n2.getTime().compareTo(n1.getTime()))
                .collect(Collectors.toList());
    }

    @Override
    public ApiResponse markAsRead(Long id, Long userId) {
        UserNotificationStatus status = userNotificationStatusRepository
                .findByUserIdAndNotificationId(userId, id)
                .orElseThrow(() -> new RuntimeException("Notification not found for user"));

        status.setIsRead(true);
        userNotificationStatusRepository.save(status);

        return ApiResponse.builder().success(1).code(HttpStatus.OK.value())
                .message("User have read the notification").build();
    }

    @Override
    public ApiResponse markAllAsRead(Long userId) {
        List<UserNotificationStatus> statuses = userNotificationStatusRepository.findAllByUserId(userId);

        for (UserNotificationStatus status : statuses) {
            status.setIsRead(true);
        }

        userNotificationStatusRepository.saveAll(statuses);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("All notifications marked as read for user")
                .build();
    }

}
