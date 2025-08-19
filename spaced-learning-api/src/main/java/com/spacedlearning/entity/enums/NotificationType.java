package com.spacedlearning.entity.enums;

import lombok.Getter;

/**
 * Enum representing possible notification types.
 */
@Getter
public enum NotificationType {
    PUSH("PUSH"),
    EMAIL("EMAIL"),
    IN_APP("IN_APP");

    private final String value;

    NotificationType(String value) {
        this.value = value;
    }
}
