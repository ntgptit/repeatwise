package com.repeatwise.exception;

/**
 * Exception thrown when user tries to access resource they don't own
 *
 * Requirements:
 * - UC-003: User Logout - Verify token ownership
 * - Security: Authorization check
 *
 * HTTP Status: 403 FORBIDDEN
 *
 * @author RepeatWise Team
 */
public class ForbiddenException extends BusinessException {

    public ForbiddenException(final String errorCode, final String message) {
        super(errorCode, message);
    }

    public ForbiddenException(final String errorCode, final String message, final Throwable cause) {
        super(errorCode, message, cause);
    }
}
