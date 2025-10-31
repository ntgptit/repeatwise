package com.repeatwise.exception;

/**
 * Exception thrown when folder is too large for synchronous copy operation
 *
 * Requirements:
 * - UC-008: Copy Folder - A1: Large Folder - Asynchronous Copy
 * - BR-023: Async threshold (>50 items or >1000 cards)
 *
 * Use Cases:
 * - Folder has >50 total items (folders + decks)
 * - Folder has >1000 cards
 * - Should use async job instead
 *
 * HTTP Status: 400 Bad Request
 * Response should include suggestion to use async endpoint
 *
 * @author RepeatWise Team
 */
public class FolderTooLargeException extends BusinessException {

    private static final long serialVersionUID = 7646585439627104445L;
    private static final String ERROR_CODE = "FOLDER_TOO_LARGE";

    public FolderTooLargeException(final String message) {
        super(ERROR_CODE, message);
    }

    public FolderTooLargeException(final Integer itemCount, final Integer threshold) {
        super(ERROR_CODE,
                String.format("Folder is too large (%d items, max %d for sync copy). Use async copy instead.",
                        itemCount, threshold));
    }

    public FolderTooLargeException(final String folderName, final Long itemCount) {
        super(ERROR_CODE,
                String.format("Folder '%s' is too large (%d items). Use async copy endpoint.",
                        folderName, itemCount));
    }
}
