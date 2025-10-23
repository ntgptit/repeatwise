package com.repeatwise.entity.enums;

import lombok.Getter;

/**
 * Type of notification sent to users
 *
 * Requirements: UC-024 BR-076 - Notification Types
 * - DAILY_REMINDER: Sent at user-configured time if cards are due (MVP)
 * - STREAK_REMINDER: Sent if user hasn't reviewed today and has active streak (Future)
 * - ACHIEVEMENT: Sent when milestone reached (100 cards, 30-day streak, etc.) (Future)
 * - SYSTEM: System announcements and maintenance notifications (Future)
 */
@Getter
public enum NotificationType {

    DAILY_REMINDER("Daily Review Reminder"),
    STREAK_REMINDER("Streak Reminder"),
    ACHIEVEMENT("Achievement Notification"),
    SYSTEM("System Notification");

    private final String description;

    NotificationType(final String description) {
        this.description = description;
    }

    /**
     * Check if this notification type is enabled in MVP
     * @return true if supported (only DAILY_REMINDER in MVP)
     */
    public boolean isMvpEnabled() {
        return this == DAILY_REMINDER;
    }
}
