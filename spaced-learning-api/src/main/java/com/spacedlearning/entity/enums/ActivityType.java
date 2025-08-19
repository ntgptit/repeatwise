package com.spacedlearning.entity.enums;

import lombok.Getter;

/**
 * Enum representing possible activity types for logging.
 */
@Getter
public enum ActivityType {
    SET_CREATED("SET_CREATED"),
    SET_UPDATED("SET_UPDATED"),
    SET_DELETED("SET_DELETED"),
    REVIEW_COMPLETED("REVIEW_COMPLETED"),
    REVIEW_SKIPPED("REVIEW_SKIPPED"),
    REVIEW_SCORE_UPDATED("REVIEW_SCORE_UPDATED"),
    REMINDER_CREATED("REMINDER_CREATED"),
    REMINDER_RESCHEDULED("REMINDER_RESCHEDULED"),
    REMINDER_CANCELLED("REMINDER_CANCELLED"),
    CYCLE_COMPLETED("CYCLE_COMPLETED"),
    CYCLE_STARTED("CYCLE_STARTED");

    private final String value;

    ActivityType(String value) {
        this.value = value;
    }
}
