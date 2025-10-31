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

    private static final long serialVersionUID = 1165237777459020693L;

    public ValidationException(final String errorCode, final String message) {
        super(errorCode, message);
    }
}
