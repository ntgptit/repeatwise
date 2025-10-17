package com.repeatwise.exception;

/**
 * Exception thrown for validation errors
 *
 * Requirements:
 * - Coding Convention: Validation error handling
 *
 * @author RepeatWise Team
 */
public class ValidationException extends BusinessException {

    public ValidationException(final String errorCode, final String message) {
        super(errorCode, message);
    }
}
