package com.spacedlearning.entity.enums;

import lombok.Getter;

/**
 * Enum representing possible set statuses.
 */
@Getter
public enum SetStatus {
    NOT_STARTED("not_started"),
    LEARNING("learning"),
    REVIEWING("reviewing"),
    MASTERED("mastered");

    private final String value;

    SetStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
