package com.repeatwise.exception;

/**
 * Exception thrown when attempting to create a resource that already exists
 *
 * Requirements:
 * - UC-001: Register - Duplicate email/username
 * - UC-005: Create Folder - Duplicate folder name
 * - HTTP Status: 409 Conflict
 *
 * Use Cases:
 * - Email already registered
 * - Username already taken
 * - Folder name already exists
 * - Deck name already exists
 *
 * @author RepeatWise Team
 */
public class DuplicateResourceException extends BusinessException {

    /**
     * Constructor with error code and message
     *
     * @param errorCode Error code (e.g., "USER_002", "FOLDER_004")
     * @param message Error message from MessageSource
     */
    public DuplicateResourceException(final String errorCode, final String message) {
        super(errorCode, message);
    }

    /**
     * Constructor with error code, message, and cause
     *
     * @param errorCode Error code
     * @param message Error message
     * @param cause Root cause
     */
    public DuplicateResourceException(final String errorCode, final String message, final Throwable cause) {
        super(errorCode, message, cause);
    }
}
