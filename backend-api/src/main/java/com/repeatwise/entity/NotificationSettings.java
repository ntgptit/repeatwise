package com.repeatwise.entity;

import java.time.LocalTime;

import org.hibernate.annotations.SQLRestriction;

import com.repeatwise.entity.base.BaseEntity;
import com.repeatwise.entity.enums.NotificationMethod;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * NotificationSettings entity - User notification preferences
 *
 * Requirements:
 * - UC-024: Manage Notifications
 * - Database Schema V7: notification_settings table
 * - API Endpoints: GET/PUT /api/notifications/settings
 *
 * Business Rules (UC-024):
 * - BR-076: Notification Types (DAILY_REMINDER for MVP)
 * - BR-077: Notification Timing (HH:MM format, �5 min window)
 * - BR-078: Notification Methods (EMAIL for MVP, PUSH/SMS future)
 * - BR-079: Frequency Limits (max 1 daily reminder per day)
 * - BR-080: Opt-Out (user can disable any notification type)
 *
 * @author RepeatWise Team
 */
@Entity
@Table(name = "notification_settings")
@EntityListeners(org.springframework.data.jpa.domain.support.AuditingEntityListener.class)
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettings extends BaseEntity {

    /**
     * User who owns these notification settings
     * One-to-one relationship with User entity
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /**
     * Master switch for daily review reminders
     * Default: true (enabled on user registration)
     */
    @NotNull
    @Column(name = "daily_reminder_enabled", nullable = false)
    private Boolean dailyReminderEnabled = true;

    /**
     * Time to send daily reminder (user local time)
     * Format: HH:MM (00:00 to 23:59)
     * Default: 09:00
     *
     * UC-024: System converts to UTC for storage, sends within �5 minutes
     */
    @NotNull
    @Column(name = "daily_reminder_time", nullable = false)
    private LocalTime dailyReminderTime = LocalTime.of(9, 0);

    /**
     * CSV of days to send reminders
     * Format: MON,TUE,WED,THU,FRI,SAT,SUN
     * Default: All 7 days
     *
     * Validation: Must match regex in V7 migration
     * At least one day required if dailyReminderEnabled = true
     */
    @NotNull
    @Size(max = 50)
    @Pattern(regexp = "^(MON|TUE|WED|THU|FRI|SAT|SUN)(,(MON|TUE|WED|THU|FRI|SAT|SUN))*$",
             message = "Invalid day format. Use comma-separated: MON,TUE,WED,THU,FRI,SAT,SUN")
    @Column(name = "daily_reminder_days", nullable = false, length = 50)
    private String dailyReminderDays = "MON,TUE,WED,THU,FRI,SAT,SUN";

    /**
     * Notification delivery method
     * MVP: EMAIL only
     * Future: PUSH, SMS
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_method", nullable = false, length = 20)
    private NotificationMethod notificationMethod = NotificationMethod.EMAIL;

    /**
     * Custom email address for notifications
     * If null, use user.email from User entity
     *
     * Validation: Valid email format if provided
     */
    @Email(message = "Invalid email format")
    @Size(max = 255)
    @Column(name = "notification_email", length = 255)
    private String notificationEmail;

    /**
     * FCM/APNs device token for push notifications
     * Future feature - not used in MVP
     * Max 500 chars to accommodate various push services
     */
    @Size(max = 500)
    @Column(name = "push_token", length = 500)
    private String pushToken;

    /**
     * Get effective email address for notifications
     * Returns custom email if set, otherwise user's primary email
     *
     * @return email address to send notifications to
     */
    public String getEffectiveEmail() {
        if (notificationEmail != null && !notificationEmail.isBlank()) {
            return notificationEmail;
        }
        return user != null ? user.getEmail() : null;
    }

    /**
     * Check if notifications should be sent today
     * Based on current day of week and dailyReminderDays setting
     *
     * @param dayOfWeek Day of week (MON, TUE, etc.)
     * @return true if notifications enabled for this day
     */
    public boolean isEnabledForDay(final String dayOfWeek) {
        if (!dailyReminderEnabled || dailyReminderDays == null) {
            return false;
        }
        return dailyReminderDays.contains(dayOfWeek);
    }

    /**
     * Check if daily reminders are currently active
     * @return true if enabled and at least one day is configured
     */
    public boolean isDailyReminderActive() {
        return dailyReminderEnabled != null
            && dailyReminderEnabled
            && dailyReminderDays != null
            && !dailyReminderDays.isBlank();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationSettings)) {
            return false;
        }
        final NotificationSettings that = (NotificationSettings) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
