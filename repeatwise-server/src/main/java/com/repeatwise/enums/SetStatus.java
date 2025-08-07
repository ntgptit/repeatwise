package com.repeatwise.enums;

public enum SetStatus {
    NOT_STARTED("not_started"),
    LEARNING("learning"),
    REVIEWING("reviewing"),
    MASTERED("mastered");

    private final String value;

    SetStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
} 