package com.repeatwise.exception.access;

import org.springframework.http.HttpStatus;

import com.repeatwise.constant.ApiErrorCode;
import com.repeatwise.exception.base.BaseException;

/**
 * Exception thrown when user attempts unauthorized access to a resource
 */
public class UnauthorizedAccessException extends BaseException {

    private static final long serialVersionUID = 1L;

    public UnauthorizedAccessException() {
        super("error.auth.forbidden", ApiErrorCode.UNAUTHORIZED_ACCESS);
    }

    public UnauthorizedAccessException(String resourceType) {
        super("error.auth.forbidden.resource", ApiErrorCode.UNAUTHORIZED_ACCESS, resourceType);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
