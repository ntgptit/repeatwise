package com.repeatwise.exception.user;

import org.springframework.http.HttpStatus;

import com.repeatwise.constant.ApiErrorCode;
import com.repeatwise.exception.base.BaseException;

/**
 * Exception thrown when username already exists
 */

public class UsernameAlreadyExistsException extends BaseException {

    private static final long serialVersionUID = 1L;

    public UsernameAlreadyExistsException(String username) {
        super("error.user.username.already.exists", ApiErrorCode.USERNAME_ALREADY_EXISTS, username);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
