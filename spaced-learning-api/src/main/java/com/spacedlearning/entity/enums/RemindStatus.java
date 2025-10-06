package com.spacedlearning.entity.enums;

import lombok.Getter;

/**
 * Enum representing possible reminder statuses.
 */
@Getter
public enum RemindStatus {
    PENDING("pending"),
    SENT("sent"),
    DONE("done"),
    SKIPPED("skipped"),
    RESCHEDULED("rescheduled"),
    CANCELLED("cancelled");

    private final String value;

    RemindStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
