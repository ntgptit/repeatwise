package com.repeatwise.dto.response.notification;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO for notification settings
 *
 * Requirements:
 * - UC-024: Manage Notifications (Step 2)
 * - API Endpoints: GET /PUT /api/notifications/settings
 * - API Spec lines 726-758
 *
 * Response Format (UC-024):
 * {
 *   "dailyReminderEnabled": true,
 *   "dailyReminderTime": "19:00",
 *   "dailyReminderDays": ["MON", "TUE", "WED", "THU", "FRI"],
 *   "notificationMethod": "EMAIL",
 *   "notificationEmail": "custom@example.com",
 *   "nextReminderAt": "2025-01-10T19:00:00Z"
 * }
 *
 * @author RepeatWise Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingsResponse {

    /**
     * Whether daily review reminders are enabled
     */
    private Boolean dailyReminderEnabled;

    /**
     * Time to send daily reminders (HH:MM format)
     * Example: "09:00", "19:00"
     */
    private String dailyReminderTime;

    /**
     * Days of week to send reminders
     * Example: ["MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"]
     */
    private List<String> dailyReminderDays;

    /**
     * Notification delivery method
     * MVP: "EMAIL"
     * Future: "PUSH", "SMS"
     */
    private String notificationMethod;

    /**
     * Custom email address for notifications
     * null if using user's primary email
     */
    private String notificationEmail;

    /**
     * Timestamp of next scheduled reminder (UTC)
     * Calculated based on current settings and time
     * null if reminders are disabled
     *
     * Example: "2025-01-10T19:00:00Z"
     */
    private Instant nextReminderAt;
}
