package com.repeatwise.exception.folder;

import org.springframework.http.HttpStatus;

import com.repeatwise.constant.ApiErrorCode;
import com.repeatwise.exception.base.BaseException;

/**
 * Exception thrown when folder name already exists in parent folder
 */

public class FolderNameAlreadyExistsException extends BaseException {

    private static final long serialVersionUID = 1L;

    public FolderNameAlreadyExistsException(String folderName) {
        super("error.folder.name.exists", ApiErrorCode.FOLDER_NAME_ALREADY_EXISTS, folderName);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
