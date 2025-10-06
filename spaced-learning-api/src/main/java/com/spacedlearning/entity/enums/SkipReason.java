package com.spacedlearning.entity.enums;

/**
 * Enum representing the reason for skipping a review
 */
public enum SkipReason {
    FORGOT("forgot"),
    BUSY("busy"),
    OTHER("other");

    private final String value;

    SkipReason(String value) {
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
