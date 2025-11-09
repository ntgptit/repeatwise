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
        super(buildFallbackMessage(messageKey, messageArgs));
        this.errorCode = errorCode;
        this.messageKey = messageKey;
        this.messageArgs = messageArgs == null ? new Object[0] : messageArgs;
        this.fallbackMessage = buildFallbackMessage(messageKey, messageArgs);
    }

    protected BaseException(String messageKey, String errorCode, Throwable cause,
            Object... messageArgs) {
        super(buildFallbackMessage(messageKey, messageArgs), cause);
        this.errorCode = errorCode;
        this.messageKey = messageKey;
        this.messageArgs = messageArgs == null ? new Object[0] : messageArgs;
        this.fallbackMessage = buildFallbackMessage(messageKey, messageArgs);
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

    /**
     * Build a fallback message by appending arguments to the message key.
     * This provides a more meaningful error message when MessageSource resolution fails.
     * Format: "messageKey: arg1, arg2, ..."
     */
    private static String buildFallbackMessage(String messageKey, Object... messageArgs) {
        if (messageKey == null) {
            return "An error occurred";
        }

        if (messageArgs == null || messageArgs.length == 0) {
            return messageKey;
        }

        final var builder = new StringBuilder(messageKey);
        builder.append(": ");
        for (int i = 0; i < messageArgs.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(messageArgs[i]);
        }
        return builder.toString();
    }

    public abstract HttpStatus getHttpStatus();
}
