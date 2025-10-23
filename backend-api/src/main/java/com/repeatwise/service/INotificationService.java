package com.repeatwise.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.repeatwise.dto.request.notification.UpdateNotificationSettingsRequest;
import com.repeatwise.dto.response.notification.NotificationLogResponse;
import com.repeatwise.dto.response.notification.NotificationSettingsResponse;
import com.repeatwise.dto.response.notification.TestNotificationResponse;

/**
 * Notification Service Interface - Manages user notification preferences and delivery
 *
 * Requirements:
 * - UC-024: Manage Notifications (all steps)
 * - API Endpoints: GET/PUT /api/notifications/settings, POST /api/notifications/test, GET /api/notifications/logs
 * - Coding Convention: Service layer with interface + implementation pattern
 *
 * Business Rules (UC-024):
 * - BR-076: Notification Types (DAILY_REMINDER for MVP)
 * - BR-077: Notification Timing (�5 min window, skip if no due cards)
 * - BR-078: Notification Methods (EMAIL only in MVP)
 * - BR-079: Frequency Limits (max 1 daily reminder per day)
 * - BR-080: Opt-Out (user can disable notifications)
 *
 * @author RepeatWise Team
 */
public interface INotificationService {

    /**
     * Get notification settings for current user
     *
     * UC-024 Step 1-2: Navigate to Notification Settings, View Current Settings
     * Used by: GET /api/notifications/settings
     *
     * If settings don't exist (first time), creates default settings
     * Default values defined in NotificationSettings entity
     *
     * @param userId Authenticated user ID from JWT token
     * @return NotificationSettingsResponse with current settings
     */
    NotificationSettingsResponse getNotificationSettings(UUID userId);

    /**
     * Update notification settings for current user
     *
     * UC-024 Step 3-5: Modify Settings, Save Settings
     * Used by: PUT /api/notifications/settings
     *
     * Validation Rules (UC-024):
     * - dailyReminderTime: HH:MM format (00:00 to 23:59)
     * - dailyReminderDays: At least 1 day required if enabled
     * - notificationEmail: Valid email format or null
     * - notificationMethod: Only EMAIL supported in MVP
     *
     * @param userId Authenticated user ID from JWT token
     * @param request UpdateNotificationSettingsRequest with new settings
     * @return NotificationSettingsResponse with updated settings and nextReminderAt
     */
    NotificationSettingsResponse updateNotificationSettings(
        UUID userId,
        UpdateNotificationSettingsRequest request
    );

    /**
     * Send a test notification to user immediately
     *
     * UC-024 Step 4: Test Notification (Optional)
     * Used by: POST /api/notifications/test
     *
     * Purpose:
     * - Verify email delivery works
     * - Preview notification content
     * - Confirm notification email address
     *
     * Email format:
     * - Subject: "[RepeatWise] Test Notification"
     * - Body: "This is a test notification. You have X cards due for review."
     *
     * @param userId Authenticated user ID from JWT token
     * @return TestNotificationResponse with confirmation message and sent timestamp
     */
    TestNotificationResponse sendTestNotification(UUID userId);

    /**
     * Get notification delivery history (logs) for current user
     *
     * UC-024: View notification history
     * Used by: GET /api/notifications/logs?page=0&size=20
     *
     * Returns paginated list of all notifications sent to user
     * Sorted by sentAt DESC (most recent first)
     *
     * @param userId Authenticated user ID from JWT token
     * @param pageable Pagination parameters (page, size, sort)
     * @return Page of NotificationLogResponse
     */
    Page<NotificationLogResponse> getNotificationLogs(UUID userId, Pageable pageable);

    /**
     * Process daily reminder notifications for all enabled users
     *
     * UC-024 Step 6: Receive Daily Notification
     * Called by: NotificationScheduler (cron job every minute)
     *
     * Process:
     * 1. Find all users with reminders enabled at current time
     * 2. For each user, calculate due cards count
     * 3. If due cards > 0, send email notification
     * 4. If due cards = 0, skip notification (Alternative A2)
     * 5. Log all notification attempts (success/failure)
     * 6. Retry on failure (max 3 attempts) (Alternative A3)
     *
     * Business Rules:
     * - Skip if no cards due (BR-077)
     * - Max 1 daily reminder per day (BR-079)
     * - Send within �5 minutes of scheduled time
     *
     * @return Number of notifications sent successfully
     */
    int processDailyReminders();

    /**
     * Retry failed notifications
     *
     * UC-024 Alternative A3: Email Delivery Failure
     * Called by: Scheduled job (hourly)
     *
     * Process:
     * 1. Find FAILED/BOUNCED notifications with retry_count < 3
     * 2. Attempt to resend each notification
     * 3. Update status and increment retry_count
     * 4. If all retries exhausted, flag user's email as invalid
     *
     * @return Number of notifications retried
     */
    int retryFailedNotifications();
}
