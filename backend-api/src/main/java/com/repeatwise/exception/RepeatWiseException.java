package com.repeatwise.exception;

import org.springframework.http.HttpStatus;

import com.repeatwise.exception.base.BaseException;

import lombok.Getter;

/**
 * Generic business exception for RepeatWise with centralized error definition.
 */
@Getter
public class RepeatWiseException extends BaseException {

    private static final long serialVersionUID = 1L;

    private final RepeatWiseError error;

    public RepeatWiseException(RepeatWiseError error, Object... messageArgs) {
        super(error.getMessageKey(), error.getErrorCode(), messageArgs);
        this.error = error;
    }

    public RepeatWiseException(RepeatWiseError error, String messageKey, Object... messageArgs) {
        super(messageKey, error.getErrorCode(), messageArgs);
        this.error = error;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.error.getHttpStatus();
    }
}

