package org._java_proj.gym_management_system.features.notification.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.features.notification.dto.request.NotificationCreateRequest;
import org._java_proj.gym_management_system.features.notification.dto.request.NotificationUpdateRequest;
import org._java_proj.gym_management_system.features.notification.dto.response.NotificationResponseDto;
import org._java_proj.gym_management_system.features.notification.repository.NotificationRepository;
import org._java_proj.gym_management_system.features.notification.service.NotificationService;
import org._java_proj.gym_management_system.features.users.repository.UserRepository;
import org._java_proj.gym_management_system.model.Notification;
import org._java_proj.gym_management_system.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ApiResponse createNotification(NotificationCreateRequest request) {
        Notification notification = new Notification();
        notification.setTitle(request.getTitle());
        notification.setContent(request.getContent());
        notification.setTime(request.getTime());

        // Find recipient
        User recipient = userRepository.findById(request.getRecipientId())
                .orElseThrow(() -> new EntityNotFoundException("Recipient not found"));

        notification.setRecipient(recipient);

        notificationRepository.save(notification);

        NotificationResponseDto dto = mapToDto(notification);
        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.CREATED.value())
                .data(Map.of("notification", dto))
                .message("Notification sent to user.")
                .build();
    }

    @Override
    public ApiResponse getNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
        NotificationResponseDto dto = mapToDto(notification);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of("notification", dto))
                .message("Notification fetched.")
                .build();
    }
    public ApiResponse getNotificationsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        List<NotificationResponseDto> dtos = notificationRepository.findByRecipient(user)
                .stream().map(this::mapToDto).toList();

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of("notifications", dtos))
                .message("Notifications for user fetched.")
                .build();
    }

    @Override
    public ApiResponse listNotifications() {
        List<NotificationResponseDto> dtos = notificationRepository.findAll()
                .stream().map(this::mapToDto).toList();

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of("notifications", dtos))
                .message("All notifications fetched.")
                .build();
    }

    @Override
    @Transactional
    public ApiResponse updateNotification(Long id, NotificationUpdateRequest request) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
        if (request.getTitle() != null) notification.setTitle(request.getTitle());
        if (request.getContent() != null) notification.setContent(request.getContent());
        if (request.getTime() != null) notification.setTime(request.getTime());
        notificationRepository.save(notification);

        NotificationResponseDto dto = mapToDto(notification);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of("notification", dto))
                .message("Notification updated successfully.")
                .build();
    }

    @Override
    @Transactional
    public ApiResponse deleteNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
        notificationRepository.delete(notification);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Notification deleted successfully.")
                .build();
    }

    // Helper
    private NotificationResponseDto mapToDto(Notification notification) {
        NotificationResponseDto dto = new NotificationResponseDto();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setContent(notification.getContent());
        dto.setTime(notification.getTime());
        if (notification.getRecipient() != null)
            dto.setRecipientId(notification.getRecipient().getId());
        return dto;
    }
}
