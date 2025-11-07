package com.repeatwise.exception.folder;

import org.springframework.http.HttpStatus;

import com.repeatwise.constant.ApiErrorCode;
import com.repeatwise.exception.base.BaseException;

/**
 * Exception thrown when folder depth exceeds maximum limit (10 levels)
 */

public class MaxFolderDepthExceededException extends BaseException {

    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_MAX_DEPTH = 10;

    public MaxFolderDepthExceededException() {
        this(DEFAULT_MAX_DEPTH);
    }

    public MaxFolderDepthExceededException(int maxDepth) {
        super("error.folder.max.depth", ApiErrorCode.MAX_FOLDER_DEPTH_EXCEEDED, maxDepth);
    }

    public MaxFolderDepthExceededException(int currentDepth, int maxDepth) {
        super("error.folder.move.max.depth.exceeded", ApiErrorCode.MAX_FOLDER_DEPTH_EXCEEDED,
                currentDepth, maxDepth);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
