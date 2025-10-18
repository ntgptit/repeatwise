package com.repeatwise.exception;

/**
 * Exception thrown when refresh token is invalid, expired, or revoked
 *
 * Requirements:
 * - UC-003: User Logout - Validate refresh token
 * - API Spec: POST /api/auth/logout - 401 if token invalid
 *
 * HTTP Status: 401 UNAUTHORIZED
 *
 * @author RepeatWise Team
 */
public class InvalidTokenException extends BusinessException {

    public InvalidTokenException(final String errorCode, final String message) {
        super(errorCode, message);
    }

    public InvalidTokenException(final String errorCode, final String message, final Throwable cause) {
        super(errorCode, message, cause);
    }
}
