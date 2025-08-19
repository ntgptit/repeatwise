package com.spacedlearning.entity.enums;

import lombok.Getter;

/**
 * Enum representing possible notification statuses.
 */
@Getter
public enum NotificationStatus {
    PENDING("PENDING"),
    SENT("SENT"),
    DELIVERED("DELIVERED"),
    FAILED("FAILED");

    private final String value;

    NotificationStatus(String value) {
        this.value = value;
    }
}
