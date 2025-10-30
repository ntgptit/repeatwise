package com.repeatwise.entity.enums;

import lombok.Getter;

/**
 * User theme preference enum
 * Design: Entity Specifications - User entity, theme field
 *
 * Requirements:
 * - Database Schema: users.theme constraint (LIGHT, DARK, SYSTEM)
 */
@Getter
public enum Theme {
    LIGHT("Light Mode"),
    DARK("Dark Mode"),
    SYSTEM("System (Auto)");

    private final String description;

    Theme(final String description) {
        this.description = description;
    }
}
