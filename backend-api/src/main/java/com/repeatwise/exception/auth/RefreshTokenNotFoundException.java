package com.repeatwise.exception.auth;

import org.springframework.http.HttpStatus;

import com.repeatwise.constant.ApiErrorCode;
import com.repeatwise.exception.base.BaseException;

/**
 * Exception thrown when refresh token is not found or revoked
 */
public class RefreshTokenNotFoundException extends BaseException {

    private static final long serialVersionUID = 1L;

    public RefreshTokenNotFoundException() {
        super("error.auth.refresh.token.missing", ApiErrorCode.REFRESH_TOKEN_NOT_FOUND);
    }

    public RefreshTokenNotFoundException(String message) {
        super(message, ApiErrorCode.REFRESH_TOKEN_NOT_FOUND);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
