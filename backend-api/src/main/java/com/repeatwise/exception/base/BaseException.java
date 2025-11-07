package com.repeatwise.exception.base;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * Base exception class for all custom exceptions
 */
@Getter
public abstract class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String errorCode;
    private final String messageKey;
    private final transient Object[] messageArgs;
    private final String fallbackMessage;

    protected BaseException(String messageKey, String errorCode, Object... messageArgs) {
        super(messageKey);
        this.errorCode = errorCode;
        this.messageKey = messageKey;
        this.messageArgs = messageArgs == null ? new Object[0] : messageArgs;
        this.fallbackMessage = messageKey;
    }

    protected BaseException(String messageKey, String errorCode, Throwable cause,
            Object... messageArgs) {
        super(messageKey, cause);
        this.errorCode = errorCode;
        this.messageKey = messageKey;
        this.messageArgs = messageArgs == null ? new Object[0] : messageArgs;
        this.fallbackMessage = messageKey;
    }

    protected BaseException(String fallbackMessage, String errorCode) {
        super(fallbackMessage);
        this.errorCode = errorCode;
        this.messageKey = null;
        this.messageArgs = new Object[0];
        this.fallbackMessage = fallbackMessage;
    }

    protected BaseException(String fallbackMessage, String errorCode, Throwable cause) {
        super(fallbackMessage, cause);
        this.errorCode = errorCode;
        this.messageKey = null;
        this.messageArgs = new Object[0];
        this.fallbackMessage = fallbackMessage;
    }

    public abstract HttpStatus getHttpStatus();
}
