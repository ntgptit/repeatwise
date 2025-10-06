package com.spacedlearning.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * UserSettings entity representing user preferences and notification settings
 * Maps to the 'user_settings' table in the database
 */
@Entity
@Table(name = "user_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(onlyExplicitlyIncluded = true)
public class UserSettings extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "notification_enabled", nullable = false)
    @Builder.Default
    private Boolean notificationEnabled = true;

    @Column(name = "email_notifications", nullable = false)
    @Builder.Default
    private Boolean emailNotifications = true;

    @Column(name = "push_notifications", nullable = false)
    @Builder.Default
    private Boolean pushNotifications = true;

    @Min(value = 1, message = "Daily reminder limit must be at least 1")
    @Max(value = 10, message = "Daily reminder limit must be at most 10")
    @Column(name = "daily_reminder_limit", nullable = false)
    @Builder.Default
    private Integer dailyReminderLimit = 3;

    @Column(name = "learning_preferences", columnDefinition = "JSONB")
    private String learningPreferences;

    // Helper methods
    public void setUser(User user) {
        this.user = user;
        if (user != null && !user.getSettings().contains(this)) {
            user.addSettings(this);
        }
    }

    /**
     * Check if notifications are enabled
     */
    public boolean isNotificationEnabled() {
        return Boolean.TRUE.equals(notificationEnabled);
    }

    /**
     * Check if email notifications are enabled
     */
    public boolean isEmailNotificationEnabled() {
        return Boolean.TRUE.equals(emailNotifications);
    }

    /**
     * Check if push notifications are enabled
     */
    public boolean isPushNotificationEnabled() {
        return Boolean.TRUE.equals(pushNotifications);
    }
}
