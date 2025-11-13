package com.repeatwise.enums;

/**
 * Trạng thái job import/export.
 */
public enum AsyncJobStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED,
    TIMEOUT;

    public boolean isTerminal() {
        return switch (this) {
        case COMPLETED, FAILED, TIMEOUT -> true;
        default -> false;
        };
    }
}

