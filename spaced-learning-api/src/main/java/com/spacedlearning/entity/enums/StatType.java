package com.spacedlearning.entity.enums;

/**
 * Enum representing different types of statistics
 */
public enum StatType {
    DAILY_REVIEWS("daily_reviews"),
    WEEKLY_REVIEWS("weekly_reviews"),
    MONTHLY_REVIEWS("monthly_reviews"),
    AVERAGE_SCORE("average_score"),
    TOTAL_SETS("total_sets"),
    ACTIVE_SETS("active_sets"),
    LEARNING_STREAK("learning_streak"),
    REVIEW_ACCURACY("review_accuracy"),
    TIME_SPENT("time_spent");

    private final String value;

    StatType(String value) {
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
