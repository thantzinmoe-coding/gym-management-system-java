package org._java_proj.gym_management_system.features.notificationManager.service.impl;

import org._java_proj.gym_management_system.features.notificationManager.dto.NotificationDTO;
import org._java_proj.gym_management_system.features.notificationManager.repository.NotificationRepository;
import org._java_proj.gym_management_system.features.notificationManager.service.NotificationService;
import org._java_proj.gym_management_system.features.users.repository.UserRepository;
import org._java_proj.gym_management_system.model.Notification;
import org._java_proj.gym_management_system.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // For sending WebSocket messages

    @Autowired
    private UserRepository userRepository; // Assume this exists

    @Override
    public Notification saveAndSend(NotificationDTO notificationDTO) {
        Notification notification = new Notification();
        notification.setTitle(notificationDTO.getTitle());
        notification.setContent(notificationDTO.getContent());
        notification.setTime(LocalDateTime.now());

        // Fetch sender and recipient users
        User sender = userRepository.findById(notificationDTO.getSenderId()).orElse(null);
        notification.setSender(sender);

        User recipient = null;
        if (notificationDTO.getRecipientId() != null) {
            recipient = userRepository.findById(notificationDTO.getRecipientId()).orElse(null);
            notification.setRecipient(recipient);
        }

        Notification savedNotification = notificationRepository.save(notification);

        // Send a WebSocket message
        if (recipient != null) {
            // Send to a specific user
            messagingTemplate.convertAndSendToUser(
                    recipient.getProfile().getName(), // or some unique identifier
                    "/queue/notifications",
                    savedNotification
            );
        } else {
            // Broadcast to all users
            messagingTemplate.convertAndSend("/topic/notifications", savedNotification);
        }

        return savedNotification;
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
}
