package com.spacedlearning.entity.enums;

/**
 * Enum representing different types of entities that can be affected by actions
 */
public enum EntityType {
    USER("user"),
    SET("set"),
    CYCLE("cycle"),
    REVIEW("review"),
    REMINDER("reminder"),
    PROFILE("profile"),
    SETTINGS("settings");

    private final String value;

    EntityType(String value) {
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
