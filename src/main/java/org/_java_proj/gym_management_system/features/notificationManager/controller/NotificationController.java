package org._java_proj.gym_management_system.features.notificationManager.controller;

import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.features.notificationManager.dto.NotificationDTO;
import org._java_proj.gym_management_system.features.notificationManager.service.NotificationService;
import org._java_proj.gym_management_system.model.Notification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification API", description = "Endpoints for managing notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/send")
    @Operation(summary = "Send a new notification", description = "Allows an admin to send a new notification to a specific user or all users.")
    public ResponseEntity<Notification> sendNotification(@RequestBody NotificationDTO notificationDTO) {
        Notification savedNotification = notificationService.saveAndSend(notificationDTO);
        return ResponseEntity.ok(savedNotification);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get a user's notifications", description = "Fetches all notifications (personal and broadcast) for a given user.")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }
}
