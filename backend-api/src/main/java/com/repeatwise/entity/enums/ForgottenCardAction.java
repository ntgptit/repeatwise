package com.repeatwise.entity.enums;

import lombok.Getter;

/**
 * Action to take when user forgets a card during review
 * Design: Entity Specifications - SrsSettings entity
 *
 * Requirements:
 * - Database Schema: srs_settings.forgotten_card_action constraint
 * - Values: MOVE_TO_BOX_1, MOVE_DOWN_N_BOXES, STAY_IN_BOX
 */
@Getter
public enum ForgottenCardAction {
    MOVE_TO_BOX_1("Move to Box 1"),
    MOVE_DOWN_N_BOXES("Move down N boxes"),
    STAY_IN_BOX("Stay in current box");

    private final String description;

    ForgottenCardAction(final String description) {
        this.description = description;
    }
}
