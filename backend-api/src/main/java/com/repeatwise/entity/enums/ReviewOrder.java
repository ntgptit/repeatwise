package com.repeatwise.entity.enums;

import lombok.Getter;

/**
 * Card review order enum for SRS settings
 * Design: Entity Specifications - SrsSettings entity
 */
@Getter
public enum ReviewOrder {
    RANDOM("Random order"),
    OLDEST_FIRST("Oldest cards first"),
    NEWEST_FIRST("Newest cards first");

    private final String description;

    ReviewOrder(final String description) {
        this.description = description;
    }
}
