package com.repeatwise.enums;

public enum CycleStatus {
    ACTIVE("active"),
    FINISHED("finished");

    private final String value;

    CycleStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
} 