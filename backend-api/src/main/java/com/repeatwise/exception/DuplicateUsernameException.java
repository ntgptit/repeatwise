package com.repeatwise.exception;

/**
 * Exception thrown when user tries to register with existing username
 *
 * Requirements:
 * - UC-001: User Registration - A1: Username Already Taken
 *
 * @author RepeatWise Team
 */
public class DuplicateUsernameException extends BusinessException {

    private static final long serialVersionUID = -2898291186354862356L;

    public DuplicateUsernameException(final String errorCode, final String message) {
        super(errorCode, message);
    }
}
