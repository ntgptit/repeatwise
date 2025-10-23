package com.repeatwise.entity.enums;

import lombok.Getter;

/**
 * Status of a notification delivery
 *
 * Requirements: UC-024 - Notification Logs
 * - PENDING: Queued for sending
 * - SENT: Successfully sent to mail server
 * - DELIVERED: Confirmed delivery (email opened/received)
 * - FAILED: Failed to send (SMTP error, invalid email, etc.)
 * - BOUNCED: Email bounced (mailbox not found, full, etc.)
 */
@Getter
public enum NotificationStatus {

    PENDING("Pending"),
    SENT("Sent"),
    DELIVERED("Delivered"),
    FAILED("Failed"),
    BOUNCED("Bounced");

    private final String description;

    NotificationStatus(final String description) {
        this.description = description;
    }

    /**
     * Check if this status indicates a successful delivery
     * @return true if SENT or DELIVERED
     */
    public boolean isSuccess() {
        return this == SENT || this == DELIVERED;
    }

    /**
     * Check if this status indicates a failure (retry eligible)
     * @return true if FAILED or BOUNCED
     */
    public boolean isFailure() {
        return this == FAILED || this == BOUNCED;
    }

    /**
     * Check if notification is still being processed
     * @return true if PENDING
     */
    public boolean isPending() {
        return this == PENDING;
    }
}
