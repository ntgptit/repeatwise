package com.spacedlearning.exception;

/**
 * Custom exception for forbidden access errors
 * Used when access is denied due to insufficient permissions
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForbiddenException() {
        super("Access denied");
    }
}
