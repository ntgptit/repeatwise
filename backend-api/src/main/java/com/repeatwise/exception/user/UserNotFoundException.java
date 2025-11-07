package com.repeatwise.exception.user;

import org.springframework.http.HttpStatus;

import com.repeatwise.constant.ApiErrorCode;
import com.repeatwise.exception.base.BaseException;

/**
 * Exception thrown when user is not found
 */

public class UserNotFoundException extends BaseException {

    private static final long serialVersionUID = 1L;

    public UserNotFoundException(Object userId) {
        super("error.user.not.found", ApiErrorCode.USER_NOT_FOUND, userId);
    }

    public UserNotFoundException(String message) {
        super(message, ApiErrorCode.USER_NOT_FOUND);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
