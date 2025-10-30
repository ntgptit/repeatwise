package com.repeatwise.entity.enums;

import lombok.Getter;

/**
 * Card rating enum for review logs
 *
 * Requirements:
 * - Database Schema: review_logs.rating constraint (AGAIN, HARD, GOOD, EASY)
 * - UC-024: Rate Card
 *
 * @author RepeatWise Team
 */
@Getter
public enum CardRating {
    AGAIN("Forgot - Move back"),
    HARD("Hard - Slow progress"),
    GOOD("Good - Normal progress"),
    EASY("Easy - Fast progress");

    private final String description;

    CardRating(final String description) {
        this.description = description;
    }
}

