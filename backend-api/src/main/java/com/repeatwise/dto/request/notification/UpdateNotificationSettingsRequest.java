package com.repeatwise.dto.request.notification;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO for updating notification settings
 *
 * Requirements:
 * - UC-024: Manage Notifications (Step 3, Step 5)
 * - API Endpoints: PUT /api/notifications/settings
 *
 * Validation Rules (UC-024):
 * - dailyReminderTime: HH:MM format (00:00 to 23:59)
 * - dailyReminderDays: At least one day required if enabled
 * - notificationEmail: Valid email format or null (uses user email)
 *
 * @author RepeatWise Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNotificationSettingsRequest {

    /**
     * Enable or disable daily review reminders
     * Default: true
     */
    @NotNull(message = "{error.notification.enabled.required}")
    private Boolean dailyReminderEnabled;

    /**
     * Time to send daily reminders (HH:MM format in user's local time)
     * Example: "09:00", "19:00"
     *
     * Validation: Must match HH:MM format (00:00 to 23:59)
     */
    @NotNull(message = "{error.notification.time.required}")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$",
             message = "{error.notification.time.format}")
    private String dailyReminderTime;

    /**
     * Days of week to send reminders
     * Example: ["MON", "TUE", "WED", "THU", "FRI"]
     *
     * Validation:
     * - At least 1 day required if dailyReminderEnabled = true
     * - Each day must be valid: MON, TUE, WED, THU, FRI, SAT, SUN
     */
    @NotNull(message = "{error.notification.days.required}")
    @Size(min = 1, message = "{error.notification.days.at.least.one}")
    private List<@Pattern(regexp = "^(MON|TUE|WED|THU|FRI|SAT|SUN)$",
                          message = "{error.notification.days.invalid}")
                  String> dailyReminderDays;

    /**
     * Notification delivery method
     * MVP: Only "EMAIL" is supported
     * Future: "PUSH", "SMS"
     *
     * Validation: Must be "EMAIL" for MVP
     */
    @NotNull(message = "{error.notification.method.required}")
    @Pattern(regexp = "^EMAIL$",
             message = "{error.notification.method.only.email}")
    private String notificationMethod;

    /**
     * Custom email address for notifications
     * If null or blank, uses user's primary email
     *
     * Validation: Valid email format if provided
     */
    @Email(message = "{error.notification.email.invalid}")
    @Size(max = 255, message = "{error.notification.email.length}")
    private String notificationEmail;
}
