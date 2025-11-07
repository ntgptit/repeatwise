package com.repeatwise.exception.user;

import org.springframework.http.HttpStatus;

import com.repeatwise.constant.ApiErrorCode;
import com.repeatwise.exception.base.BaseException;

/**
 * Exception thrown when current password is incorrect
 */

public class IncorrectPasswordException extends BaseException {

    private static final long serialVersionUID = 1L;

    public IncorrectPasswordException() {
        super("error.user.password.current.incorrect", ApiErrorCode.INCORRECT_PASSWORD);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
