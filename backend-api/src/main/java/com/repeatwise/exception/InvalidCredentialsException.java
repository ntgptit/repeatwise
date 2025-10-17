package com.repeatwise.exception;

/**
 * Exception thrown when login credentials are invalid
 *
 * Requirements:
 * - UC-002: User Login - A1: Invalid Username/Email, A2: Incorrect Password
 *
 * @author RepeatWise Team
 */
public class InvalidCredentialsException extends BusinessException {

    public InvalidCredentialsException(final String errorCode, final String message) {
        super(errorCode, message);
    }
}
