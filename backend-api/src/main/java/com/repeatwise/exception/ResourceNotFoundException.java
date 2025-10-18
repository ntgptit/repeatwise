package com.repeatwise.exception;

/**
 * Exception thrown when a requested resource is not found
 *
 * Requirements:
 * - All use cases: Resource retrieval
 * - HTTP Status: 404 Not Found
 *
 * Use Cases:
 * - Folder not found by ID
 * - User not found by ID
 * - Deck not found by ID
 * - Card not found by ID
 *
 * @author RepeatWise Team
 */
public class ResourceNotFoundException extends BusinessException {

    /**
     * Constructor with error code and message
     *
     * @param errorCode Error code (e.g., "FOLDER_002", "USER_001")
     * @param message Error message from MessageSource
     */
    public ResourceNotFoundException(final String errorCode, final String message) {
        super(errorCode, message);
    }

    /**
     * Constructor with error code, message, and cause
     *
     * @param errorCode Error code
     * @param message Error message
     * @param cause Root cause
     */
    public ResourceNotFoundException(final String errorCode, final String message, final Throwable cause) {
        super(errorCode, message, cause);
    }
}
