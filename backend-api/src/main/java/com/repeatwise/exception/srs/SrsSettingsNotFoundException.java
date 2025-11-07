package com.repeatwise.exception.srs;

import org.springframework.http.HttpStatus;

import com.repeatwise.constant.ApiErrorCode;
import com.repeatwise.exception.base.BaseException;

/**
 * Exception thrown when SRS settings are not found
 */

public class SrsSettingsNotFoundException extends BaseException {

    private static final long serialVersionUID = 1L;

    public SrsSettingsNotFoundException(Object userId) {
        super("error.srs.settings.not.found", ApiErrorCode.SRS_SETTINGS_NOT_FOUND, userId);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
