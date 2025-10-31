package com.repeatwise.exception;

import lombok.Getter;

/**
 * Base business exception for all custom exceptions
 *
 * Requirements:
 * - Coding Convention: Exception handling with error codes
 * - MessageSource integration
 *
 * @author RepeatWise Team
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final String errorCode;
    private final String message;

    public BusinessException(final String errorCode, final String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public BusinessException(final String errorCode, final String message, final Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.message = message;
    }
}
