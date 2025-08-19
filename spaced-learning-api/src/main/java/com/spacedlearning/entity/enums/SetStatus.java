package com.spacedlearning.entity.enums;

import lombok.Getter;

/**
 * Enum representing possible set statuses.
 */
@Getter
public enum SetStatus {
    NOT_STARTED("NOT_STARTED"),
    LEARNING("LEARNING"),
    REVIEWING("REVIEWING"),
    MASTERED("MASTERED");

    private final String value;

    SetStatus(String value) {
        this.value = value;
    }
}
