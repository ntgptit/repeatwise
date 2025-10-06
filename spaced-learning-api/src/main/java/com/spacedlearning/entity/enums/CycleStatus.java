package com.spacedlearning.entity.enums;

/**
 * Enum representing the status of a learning cycle
 */
public enum CycleStatus {
    ACTIVE("active"),
    COMPLETED("completed"),
    PAUSED("paused");

    private final String value;

    CycleStatus(String value) {
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
