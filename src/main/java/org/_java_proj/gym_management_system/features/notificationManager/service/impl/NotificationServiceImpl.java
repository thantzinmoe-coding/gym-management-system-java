package org._java_proj.gym_management_system.features.notificationManager.service.impl;

import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.features.notificationManager.dto.NotificationDTO;
import org._java_proj.gym_management_system.features.notificationManager.repository.NotificationRepository;
import org._java_proj.gym_management_system.features.notificationManager.repository.UserNotificationStatusRepository;
import org._java_proj.gym_management_system.features.notificationManager.service.NotificationService;
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

        if (notificationDTO.getRecipientId() != null) {
            // Personal
            User recipient = userRepository.findById(notificationDTO.getRecipientId()).orElse(null);
            notification.setRecipient(recipient);
            notificationRepository.save(notification);

            // Create read-tracking
            UserNotificationStatus status = new UserNotificationStatus();
            status.setUser(recipient);
            status.setNotification(savedNotification);
            status.setIsRead(false);
            userNotificationStatusRepository.save(status);

            assert recipient != null;
            messagingTemplate.convertAndSendToUser(
                    recipient.getProfile().getName(),
                    "/queue/notifications",
                    mapToDTO(savedNotification)
            );
        } else {
            // Broadcast
            List<User> allUsers = userRepository.findAll();

            for (User user : allUsers) {
                UserNotificationStatus status = new UserNotificationStatus();
                status.setUser(user);
                status.setNotification(savedNotification);
                status.setIsRead(false);
                userNotificationStatusRepository.save(status);
            }

            messagingTemplate.convertAndSend("/topic/notifications", mapToDTO(savedNotification));
        }

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of("Notification", mapToDTO(savedNotification)))
                .message(notificationDTO.getRecipientId() == null ? "Broadcast sent" : "Sent to user")
                .build();
    }

    // Convert Notification to DTO for WebSocket
    private NotificationDTO mapToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
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
}
