package com.repeatwise.exception.auth;

import org.springframework.http.HttpStatus;

import com.repeatwise.constant.ApiErrorCode;
import com.repeatwise.exception.base.BaseException;

/**
 * Exception thrown when JWT token is expired
 */
public class TokenExpiredException extends BaseException {

    private static final long serialVersionUID = 1L;

    public TokenExpiredException() {
        super("error.auth.token.expired", ApiErrorCode.TOKEN_EXPIRED);
    }

    public TokenExpiredException(String message) {
        super(message, ApiErrorCode.TOKEN_EXPIRED);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
