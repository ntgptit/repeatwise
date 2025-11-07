package com.repeatwise.exception.user;

import org.springframework.http.HttpStatus;

import com.repeatwise.constant.ApiErrorCode;
import com.repeatwise.exception.base.BaseException;

/**
 * Exception thrown when email already exists
 */

public class EmailAlreadyExistsException extends BaseException {

    private static final long serialVersionUID = 1L;

    public EmailAlreadyExistsException(String email) {
        super("error.user.email.already.exists", ApiErrorCode.EMAIL_ALREADY_EXISTS, email);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
