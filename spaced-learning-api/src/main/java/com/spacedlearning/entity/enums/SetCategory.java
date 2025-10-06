package com.spacedlearning.entity.enums;

import lombok.Getter;

/**
 * Enum representing possible set categories for statistics and filtering.
 */
@Getter
public enum SetCategory {
    VOCABULARY("vocabulary"),
    GRAMMAR("grammar"),
    MIXED("mixed"),
    OTHER("other");

    private final String value;

    SetCategory(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
