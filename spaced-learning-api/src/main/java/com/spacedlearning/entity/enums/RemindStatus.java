package com.spacedlearning.entity.enums;

import lombok.Getter;

/**
 * Enum representing possible reminder statuses.
 */
@Getter
public enum RemindStatus {
    PENDING("PENDING"),
    SENT("SENT"),
    DONE("DONE"),
    SKIPPED("SKIPPED"),
    RESCHEDULED("RESCHEDULED"),
    CANCELLED("CANCELLED");

    private final String value;

    RemindStatus(String value) {
        this.value = value;
    }
}
