package com.repeatwise.entity.enums;

import lombok.Getter;

/**
 * Notification delivery method
 *
 * Requirements: UC-024 BR-078 - Notification Methods
 * - EMAIL: MVP (standard, works for all users)
 * - PUSH: Future (requires app installation and permission)
 * - SMS: Future (premium feature, requires phone number)
 */
@Getter
public enum NotificationMethod {

    EMAIL("Email"),
    PUSH("Push Notification"),
    SMS("SMS");

    private final String description;

    NotificationMethod(final String description) {
        this.description = description;
    }

    /**
     * Check if this method is currently supported in MVP
     * @return true if supported (only EMAIL in MVP)
     */
    public boolean isSupported() {
        return this == EMAIL;
    }
}
