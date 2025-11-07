package com.repeatwise.exception.folder;

import org.springframework.http.HttpStatus;

import com.repeatwise.constant.ApiErrorCode;
import com.repeatwise.exception.base.BaseException;

/**
 * Exception thrown when attempting to move folder into its own descendant
 */

public class CircularFolderReferenceException extends BaseException {

    private static final long serialVersionUID = 1L;

    public CircularFolderReferenceException() {
        super("error.folder.circular.reference", ApiErrorCode.CIRCULAR_FOLDER_REFERENCE);
    }

    public CircularFolderReferenceException(String message) {
        super(message, ApiErrorCode.CIRCULAR_FOLDER_REFERENCE);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
