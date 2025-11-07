package com.repeatwise.entity.enums;

/**
 * Action to take when a card is forgotten (AGAIN rating)
 */
public enum ForgottenCardAction {
    MOVE_TO_BOX_1,      // Move card back to box 1
    MOVE_DOWN_N_BOXES,  // Move down by N boxes
    STAY_IN_BOX         // Keep in current box
}
