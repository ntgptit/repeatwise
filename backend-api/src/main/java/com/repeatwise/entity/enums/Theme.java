package com.repeatwise.entity.enums;

import lombok.Getter;

/**
 * User theme preference enum
 * Design: Entity Specifications - User entity, theme field
 */
@Getter
public enum Theme {
    LIGHT("Light Mode"),
    DARK("Dark Mode"),
    AUTO("Auto (System)");

    private final String description;

    Theme(final String description) {
        this.description = description;
    }
}
