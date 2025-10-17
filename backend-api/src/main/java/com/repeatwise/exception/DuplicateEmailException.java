package com.repeatwise.exception;

/**
 * Exception thrown when user tries to register with existing email
 *
 * Requirements:
 * - UC-001: User Registration - A1: Email Already Registered
 *
 * @author RepeatWise Team
 */
public class DuplicateEmailException extends BusinessException {

    public DuplicateEmailException(final String errorCode, final String message) {
        super(errorCode, message);
    }
}
