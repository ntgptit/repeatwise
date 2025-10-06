package com.spacedlearning.controller;

import com.spacedlearning.dto.common.DataResponse;
import com.spacedlearning.dto.notification.NotificationResponse;
import com.spacedlearning.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for Notification operations
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notification API", description = "Endpoints for notification management")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get user notifications", 
               description = "Get notifications for the current user")
    public ResponseEntity<DataResponse<List<NotificationResponse>>> getUserNotifications(
            @Parameter(description = "Maximum number of notifications to return") 
            @RequestParam(defaultValue = "20") int limit,
            Authentication authentication) {
        
        UUID userId = getCurrentUserId(authentication);
        log.debug("Getting notifications for user {} with limit {}", userId, limit);
        
        List<NotificationResponse> notifications = notificationService.getUserNotifications(userId, limit);
        return ResponseEntity.ok(DataResponse.of(notifications));
    }

    @PutMapping("/{notificationId}/read")
    @Operation(summary = "Mark notification as read", 
               description = "Mark a specific notification as read")
    public ResponseEntity<DataResponse<NotificationResponse>> markAsRead(
            @Parameter(description = "Notification ID") @PathVariable UUID notificationId,
            Authentication authentication) {
        
        UUID userId = getCurrentUserId(authentication);
        log.info("Marking notification {} as read for user {}", notificationId, userId);
        
        NotificationResponse response = notificationService.markAsRead(notificationId, userId);
        return ResponseEntity.ok(DataResponse.of(response));
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all notifications as read", 
               description = "Mark all notifications as read for the current user")
    public ResponseEntity<DataResponse<Integer>> markAllAsRead(Authentication authentication) {
        UUID userId = getCurrentUserId(authentication);
        log.info("Marking all notifications as read for user {}", userId);
        
        int count = notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(DataResponse.of(count));
    }

    @DeleteMapping("/{notificationId}")
    @Operation(summary = "Delete notification", 
               description = "Delete a specific notification")
    public ResponseEntity<Void> deleteNotification(
            @Parameter(description = "Notification ID") @PathVariable UUID notificationId,
            Authentication authentication) {
        
        UUID userId = getCurrentUserId(authentication);
        log.info("Deleting notification {} for user {}", notificationId, userId);
        
        notificationService.deleteNotification(notificationId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread notification count", 
               description = "Get the number of unread notifications for the current user")
    public ResponseEntity<DataResponse<Integer>> getUnreadCount(Authentication authentication) {
        UUID userId = getCurrentUserId(authentication);
        log.debug("Getting unread notification count for user {}", userId);
        
        int count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(DataResponse.of(count));
    }

    /**
     * Extract current user ID from authentication
     */
    private UUID getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("No authentication found");
        }
        
        String userIdString = authentication.getName();
        try {
            return UUID.fromString(userIdString);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid user ID in authentication: " + userIdString);
        }
    }
}
