package com.repeatwise.exception.auth;

import org.springframework.http.HttpStatus;

import com.repeatwise.constant.ApiErrorCode;
import com.repeatwise.exception.base.BaseException;

/**
 * Exception thrown when user credentials are invalid
 */
public class InvalidCredentialsException extends BaseException {

    private static final long serialVersionUID = 1L;

    public InvalidCredentialsException() {
        super("error.user.invalid.credentials", ApiErrorCode.INVALID_CREDENTIALS);
    }

    public InvalidCredentialsException(String message) {
        super(message, ApiErrorCode.INVALID_CREDENTIALS);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
