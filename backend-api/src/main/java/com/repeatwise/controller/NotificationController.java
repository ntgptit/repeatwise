package com.repeatwise.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.repeatwise.dto.request.notification.UpdateNotificationSettingsRequest;
import com.repeatwise.dto.response.notification.NotificationLogResponse;
import com.repeatwise.dto.response.notification.NotificationSettingsResponse;
import com.repeatwise.dto.response.notification.TestNotificationResponse;
import com.repeatwise.security.SecurityUtils;
import com.repeatwise.service.INotificationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.repeatwise.log.LogEvent;

/**
 * Notification Controller - REST API for notification management
 *
 * Requirements:
 * - UC-024: Manage Notifications (all steps)
 * - API Endpoints Summary lines 719-811
 *
 * Endpoints:
 * - GET /api/notifications/settings: Get user notification preferences
 * - PUT /api/notifications/settings: Update notification preferences
 * - POST /api/notifications/test: Send test notification
 * - GET /api/notifications/logs: Get notification delivery history
 *
 * Security:
 * - All endpoints require JWT authentication
 * - Users can only access their own notifications
 *
 * @author RepeatWise Team
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final INotificationService notificationService;

    /**
     * Get notification settings for current user
     * Endpoint: GET /api/notifications/settings
     *
     * Use Case: UC-024 Step 1-2 - Navigate to Settings, View Current Settings
     *
     * Response 200 OK:
     * {
     *   "dailyReminderEnabled": true,
     *   "dailyReminderTime": "09:00",
     *   "dailyReminderDays": ["MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"],
     *   "notificationMethod": "EMAIL",
     *   "notificationEmail": null,
     *   "nextReminderAt": "2025-01-10T09:00:00Z"
     * }
     *
     * Error Responses:
     * - 401 Unauthorized: User not authenticated
     * - 404 Not Found: User not found
     *
     * @return NotificationSettingsResponse with current settings
     */
    @GetMapping("/settings")
    public ResponseEntity<NotificationSettingsResponse> getNotificationSettings() {
        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("event={} GET /api/notifications/settings - userId: {}", LogEvent.NOTIF_SETTINGS_GET, userId);

        final NotificationSettingsResponse settings = notificationService
            .getNotificationSettings(userId);

        return ResponseEntity.ok(settings);
    }

    /**
     * Update notification settings for current user
     * Endpoint: PUT /api/notifications/settings
     *
     * Use Case: UC-024 Step 3-5 - Modify Settings, Save Settings
     *
     * Request Body:
     * {
     *   "dailyReminderEnabled": true,
     *   "dailyReminderTime": "19:00",
     *   "dailyReminderDays": ["MON", "TUE", "WED", "THU", "FRI"],
     *   "notificationMethod": "EMAIL",
     *   "notificationEmail": "custom@example.com"
     * }
     *
     * Validation Rules (UC-024):
     * - dailyReminderTime: HH:MM format (00:00 to 23:59)
     * - dailyReminderDays: At least 1 day required if enabled
     * - notificationEmail: Valid email format or null
     * - notificationMethod: Only "EMAIL" supported in MVP
     *
     * Response 200 OK: Updated settings with nextReminderAt calculated
     *
     * Error Responses:
     * - 400 Bad Request: Validation error
     * - 401 Unauthorized: User not authenticated
     * - 404 Not Found: User not found
     *
     * @param request UpdateNotificationSettingsRequest with new settings
     * @return NotificationSettingsResponse with updated settings
     */
    @PutMapping("/settings")
    public ResponseEntity<NotificationSettingsResponse> updateNotificationSettings(
        @Valid @RequestBody final UpdateNotificationSettingsRequest request
    ) {
        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("event={} PUT /api/notifications/settings - userId: {} - request: {}",
            LogEvent.NOTIF_SETTINGS_UPDATE, userId, request);

        final NotificationSettingsResponse settings = notificationService
            .updateNotificationSettings(userId, request);

        return ResponseEntity.ok(settings);
    }

    /**
     * Send test notification to current user
     * Endpoint: POST /api/notifications/test
     *
     * Use Case: UC-024 Step 4 - Test Notification (Optional)
     *
     * Purpose:
     * - Verify email delivery works
     * - Preview notification content
     * - Confirm notification email address
     *
     * Response 202 Accepted:
     * {
     *   "message": "Test notification sent to your-email@example.com",
     *   "sentAt": "2025-01-10T15:30:00Z"
     * }
     *
     * Error Responses:
     * - 401 Unauthorized: User not authenticated
     * - 404 Not Found: User not found
     * - 500 Internal Server Error: Email sending failed
     *
     * @return TestNotificationResponse with confirmation message
     */
    @PostMapping("/test")
    public ResponseEntity<TestNotificationResponse> sendTestNotification() {
        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("event={} POST /api/notifications/test - userId: {}", LogEvent.NOTIF_TEST_SEND, userId);

        final TestNotificationResponse response = notificationService
            .sendTestNotification(userId);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * Get notification delivery history for current user
     * Endpoint: GET /api/notifications/logs
     *
     * Use Case: UC-024 - View notification history
     *
     * Query Parameters:
     * - page: Page number (0-indexed, default 0)
     * - size: Page size (default 20, max 100)
     * - sort: Sort field and direction (default: sentAt,desc)
     *
     * Response 200 OK:
     * {
     *   "content": [
     *     {
     *       "id": "uuid",
     *       "type": "DAILY_REMINDER",
     *       "recipient": "user@example.com",
     *       "status": "DELIVERED",
     *       "sentAt": "2025-01-10T09:00:00Z",
     *       "deliveredAt": "2025-01-10T09:00:05Z",
     *       "errorMessage": null,
     *       "metadata": {
     *         "dueCardsCount": 20,
     *         "streakDays": 15,
     *         "retryCount": 0
     *       }
     *     }
     *   ],
     *   "page": 0,
     *   "size": 20,
     *   "totalElements": 45,
     *   "totalPages": 3
     * }
     *
     * Error Responses:
     * - 401 Unauthorized: User not authenticated
     * - 404 Not Found: User not found
     *
     * @param pageable Pagination parameters
     * @return Page of NotificationLogResponse
     */
    @GetMapping("/logs")
    public ResponseEntity<Page<NotificationLogResponse>> getNotificationLogs(
        @PageableDefault(size = 20, sort = "sentAt", direction = Sort.Direction.DESC)
        final Pageable pageable
    ) {
        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("event={} GET /api/notifications/logs - userId: {} - page: {}, size: {}",
            LogEvent.START, userId, pageable.getPageNumber(), pageable.getPageSize());

        final Page<NotificationLogResponse> logs = notificationService
            .getNotificationLogs(userId, pageable);

        return ResponseEntity.ok(logs);
    }
}
