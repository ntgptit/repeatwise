package com.repeatwise.exception.auth;

import org.springframework.http.HttpStatus;

import com.repeatwise.constant.ApiErrorCode;
import com.repeatwise.exception.base.BaseException;

/**
 * Exception thrown when JWT token is invalid
 */
public class InvalidTokenException extends BaseException {

    private static final long serialVersionUID = 1L;

    public InvalidTokenException() {
        super("error.auth.token.invalid", ApiErrorCode.INVALID_TOKEN);
    }

    public InvalidTokenException(String message) {
        super(message, ApiErrorCode.INVALID_TOKEN);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
