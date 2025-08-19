package com.spacedlearning.entity.enums;

import lombok.Getter;

/**
 * Enum representing possible set categories for statistics and filtering.
 */
@Getter
public enum SetCategory {
    VOCABULARY("VOCABULARY"),
    GRAMMAR("GRAMMAR"),
    MIXED("MIXED"),
    OTHER("OTHER");

    private final String value;

    SetCategory(String value) {
        this.value = value;
    }
}
