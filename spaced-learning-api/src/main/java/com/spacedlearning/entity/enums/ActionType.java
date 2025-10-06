package com.spacedlearning.entity.enums;

/**
 * Enum representing different types of actions that can be logged
 */
public enum ActionType {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    LOGIN("login"),
    LOGOUT("logout"),
    VIEW_SET("view_set"),
    VIEW_PROFILE("view_profile"),
    START_CYCLE("start_cycle"),
    COMPLETE_REVIEW("complete_review"),
    SKIP_REVIEW("skip_review"),
    ARCHIVE("archive"),
    RESCHEDULE("reschedule");

    private final String value;

    ActionType(String value) {
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
