package com.spacedlearning.entity.enums;

import lombok.Getter;

/**
 * Enum representing possible review statuses.
 */
@Getter
public enum ReviewStatus {
    COMPLETED("completed"),
    SKIPPED("skipped");

    private final String value;

    ReviewStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
