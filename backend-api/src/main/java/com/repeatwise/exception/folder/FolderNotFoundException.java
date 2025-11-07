package com.repeatwise.exception.folder;

import org.springframework.http.HttpStatus;

import com.repeatwise.constant.ApiErrorCode;
import com.repeatwise.exception.base.BaseException;

/**
 * Exception thrown when folder is not found
 */

public class FolderNotFoundException extends BaseException {

    private static final long serialVersionUID = 1L;

    public FolderNotFoundException(Object folderId) {
        super("error.folder.not.found", ApiErrorCode.FOLDER_NOT_FOUND, folderId);
    }

    public FolderNotFoundException(String message) {
        super(message, ApiErrorCode.FOLDER_NOT_FOUND);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
