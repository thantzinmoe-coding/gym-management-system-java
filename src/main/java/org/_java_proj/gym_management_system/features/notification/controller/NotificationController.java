package org._java_proj.gym_management_system.features.notification.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.util.ResponseUtils;
import org._java_proj.gym_management_system.features.notification.dto.NotificationDTO;
import org._java_proj.gym_management_system.features.notification.dto.UserNotificationDTO;
import org._java_proj.gym_management_system.features.notification.service.NotificationQueryService;
import org._java_proj.gym_management_system.features.notification.service.NotificationService;
import org._java_proj.gym_management_system.model.UserDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("${api.base.path}/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification API", description = "Endpoints for managing notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationQueryService queryService;

    @PostMapping("/send")
    @Operation(summary = "Send a new notification", description = "Allows an admin to send a new notification to all users.")
    public ResponseEntity<ApiResponse> sendNotification(
            @RequestBody NotificationDTO notificationDTO,
            @AuthenticationPrincipal UserDetail userDetails,
            HttpServletRequest request) {

        // Correctly get the user's ID from the UserDetail object
        Long senderId = userDetails.getUser().getId();

        ApiResponse response = notificationService.saveAndSend(notificationDTO.withSenderId(senderId));
        return ResponseUtils.buildResponse(request, response);
    }

    @GetMapping("{userId}")
    @Operation(summary = "Get a user's notifications",
            description = "Fetches all notifications (personal and broadcast) for a given user.")
    public ResponseEntity<List<UserNotificationDTO>> getNotifications(@PathVariable("userId") Long userId) {
        List<UserNotificationDTO> notifications = queryService.getNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<ApiResponse> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetail userDetails,
            HttpServletRequest request
            ) {
        Long userId = userDetails.getUser().getId();

        ApiResponse response = this.notificationService.markAsRead(userId, id);

        return ResponseUtils.buildResponse(request, response);
    }


    @GetMapping("/unread/{userId}")
    public ResponseEntity<List<UserNotificationDTO>> getUnreadNotifications(@PathVariable Long userId) {
        List<UserNotificationDTO> notifications = queryService.getNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }


    @PostMapping("/{userId}/read-all")
    public ResponseEntity<ApiResponse> markAllAsRead(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.markAllAsRead(userId));
    }



}
