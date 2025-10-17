package com.repeatwise.entity.enums;

import lombok.Getter;

/**
 * User language preference enum
 * Design: Entity Specifications - User entity, language field
 */
@Getter
public enum Language {
    VI("Vietnamese"),
    EN("English");

    private final String description;

    Language(final String description) {
        this.description = description;
    }
}
