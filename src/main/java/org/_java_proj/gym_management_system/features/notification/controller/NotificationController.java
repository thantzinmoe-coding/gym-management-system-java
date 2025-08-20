package org._java_proj.gym_management_system.features.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.config.response.dto.ApiResponse;
import org._java_proj.gym_management_system.config.response.util.ResponseUtils;
import org._java_proj.gym_management_system.features.notification.dto.request.NotificationCreateRequest;
import org._java_proj.gym_management_system.features.notification.dto.request.NotificationUpdateRequest;
import org._java_proj.gym_management_system.features.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base.path}/notifications")
@Tag(name = "Notification API", description = "Endpoints for managing notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @Operation(summary = "Create a new notification")
    public ResponseEntity<ApiResponse> createNotification(
            @RequestBody NotificationCreateRequest request,
            HttpServletRequest servletRequest
    ) {
        ApiResponse response = notificationService.createNotification(request);
        return ResponseUtils.buildResponse(servletRequest, response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Notification by ID")
    public ResponseEntity<ApiResponse> getNotification(
            @PathVariable Long id,
            HttpServletRequest servletRequest
    ) {
        ApiResponse response = notificationService.getNotification(id);
        return ResponseUtils.buildResponse(servletRequest, response);
    }
    @GetMapping("/my")
    public ResponseEntity<ApiResponse> getMyNotifications(
            @RequestParam Long userId,
            HttpServletRequest servletRequest) {
        ApiResponse response = notificationService.getNotificationsForUser(userId);
        return ResponseUtils.buildResponse(servletRequest, response);
    }

    @GetMapping
    @Operation(summary = "List all notifications")
    public ResponseEntity<ApiResponse> listNotifications(HttpServletRequest servletRequest) {
        ApiResponse response = notificationService.listNotifications();
        return ResponseUtils.buildResponse(servletRequest, response);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update a notification")
    public ResponseEntity<ApiResponse> updateNotification(
            @PathVariable Long id,
            @RequestBody NotificationUpdateRequest request,
            HttpServletRequest servletRequest
    ) {
        ApiResponse response = notificationService.updateNotification(id, request);
        return ResponseUtils.buildResponse(servletRequest, response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a notification")
    public ResponseEntity<ApiResponse> deleteNotification(
            @PathVariable Long id,
            HttpServletRequest servletRequest
    ) {
        ApiResponse response = notificationService.deleteNotification(id);
        return ResponseUtils.buildResponse(servletRequest, response);
    }
}