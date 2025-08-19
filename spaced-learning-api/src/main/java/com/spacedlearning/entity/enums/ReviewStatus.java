package com.spacedlearning.entity.enums;

import lombok.Getter;

/**
 * Enum representing possible review statuses.
 */
@Getter
public enum ReviewStatus {
    COMPLETED("COMPLETED"),
    SKIPPED("SKIPPED");

    private final String value;

    ReviewStatus(String value) {
        this.value = value;
    }
}
