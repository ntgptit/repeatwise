package com.repeatwise.entity.enums;

import lombok.Getter;

/**
 * Action to take when user forgets a card during review
 * Design: Entity Specifications - SrsSettings entity
 */
@Getter
public enum ForgottenCardAction {
    MOVE_TO_BOX_1("Move to Box 1"),
    MOVE_DOWN_N_BOXES("Move down N boxes");

    private final String description;

    ForgottenCardAction(final String description) {
        this.description = description;
    }
}
