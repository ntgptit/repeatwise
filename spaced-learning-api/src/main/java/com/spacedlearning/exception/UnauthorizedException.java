package com.spacedlearning.exception;

/**
 * Custom exception for unauthorized access errors
 * Used when authentication is required but not provided
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedException() {
        super("Authentication required");
    }
}
