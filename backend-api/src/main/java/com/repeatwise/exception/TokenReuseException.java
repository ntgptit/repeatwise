package com.repeatwise.exception;

/**
 * Exception thrown when token reuse is detected (security issue)
 *
 * Requirements:
 * - UC-003: Refresh Access Token - Token reuse detection
 * - Security: When revoked token is reused, all user tokens are revoked
 *
 * HTTP Status: 401 UNAUTHORIZED
 *
 * @author RepeatWise Team
 */
public class TokenReuseException extends BusinessException {

    private static final long serialVersionUID = -4132185868894082136L;

    public TokenReuseException(final String errorCode, final String message) {
        super(errorCode, message);
    }

    public TokenReuseException(final String errorCode, final String message, final Throwable cause) {
        super(errorCode, message, cause);
    }
}

