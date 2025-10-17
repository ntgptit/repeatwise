package com.repeatwise.entity.enums;

import lombok.Getter;

/**
 * Card review order enum for SRS settings
 *
 * Requirements:
 * - Database Schema: review_order constraint (ASCENDING, DESCENDING, RANDOM)
 * - UC-022: Configure SRS Settings
 *
 * @author RepeatWise Team
 */
@Getter
public enum ReviewOrder {
    ASCENDING("Box 1 to Box 7 (easiest first)"),
    DESCENDING("Box 7 to Box 1 (hardest first)"),
    RANDOM("Random order");

    private final String description;

    ReviewOrder(final String description) {
        this.description = description;
    }
}
