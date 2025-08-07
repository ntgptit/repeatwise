package com.repeatwise.controller;

import com.repeatwise.dto.NotificationDto;
import com.repeatwise.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Management", description = "APIs for notification management operations")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get notifications by user", description = "Retrieves all notifications for a specific user")
    public ResponseEntity<List<NotificationDto>> getNotificationsByUser(@PathVariable UUID userId) {
        List<NotificationDto> notifications = notificationService.findByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/user/{userId}/unread")
    @Operation(summary = "Get unread notifications", description = "Retrieves unread notifications for a user")
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications(@PathVariable UUID userId) {
        List<NotificationDto> notifications = notificationService.findUnreadByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get notification by ID", description = "Retrieves notification information by notification ID")
    public ResponseEntity<NotificationDto> getNotificationById(@PathVariable UUID id) {
        return notificationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/mark-read")
    @Operation(summary = "Mark notification as read", description = "Marks a notification as read")
    public ResponseEntity<NotificationDto> markAsRead(@PathVariable UUID id, @RequestParam UUID userId) {
        NotificationDto notification = notificationService.markAsRead(id, userId);
        return ResponseEntity.ok(notification);
    }

    @PostMapping("/user/{userId}/mark-all-read")
    @Operation(summary = "Mark all notifications as read", description = "Marks all notifications as read for a user")
    public ResponseEntity<Void> markAllAsRead(@PathVariable UUID userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete notification", description = "Deletes a notification")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID id, @RequestParam UUID userId) {
        notificationService.deleteNotification(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/send-scheduled")
    @Operation(summary = "Send scheduled notifications", description = "Sends all scheduled notifications that are due")
    public ResponseEntity<List<NotificationDto>> sendScheduledNotifications() {
        List<NotificationDto> sentNotifications = notificationService.sendScheduledNotifications();
        return ResponseEntity.ok(sentNotifications);
    }

    @GetMapping("/user/{userId}/statistics")
    @Operation(summary = "Get notification statistics", description = "Retrieves notification statistics for a user")
    public ResponseEntity<NotificationService.NotificationStatistics> getNotificationStatistics(@PathVariable UUID userId) {
        NotificationService.NotificationStatistics stats = notificationService.getNotificationStatistics(userId);
        return ResponseEntity.ok(stats);
    }
} 
