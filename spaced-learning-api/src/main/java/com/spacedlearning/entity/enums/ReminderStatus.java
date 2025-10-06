package com.spacedlearning.entity.enums;

/**
 * Enum representing the status of a reminder schedule
 */
public enum ReminderStatus {
    PENDING("pending"),
    SENT("sent"),
    DONE("done"),
    COMPLETED("completed"),
    SKIPPED("skipped"),
    RESCHEDULED("rescheduled"),
    CANCELLED("cancelled");

    private final String value;

    ReminderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
