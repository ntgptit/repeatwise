package com.repeatwise.exception;

/**
 * Exception thrown when folder name already exists in the same parent
 *
 * Requirements:
 * - UC-005: Create Folder - A1: Duplicate Folder Name
 * - UC-006: Rename Folder - A1: Duplicate Folder Name
 * - UC-007: Move Folder - A3: Duplicate Name in Target
 * - BR-013: Unique name within same parent folder
 * - BR-014: Rename validation
 *
 * Use Cases:
 * - Creating folder with name that already exists in parent
 * - Renaming folder to name that already exists in parent
 * - Moving folder to parent that already has folder with same name
 *
 * HTTP Status: 409 Conflict
 *
 * @author RepeatWise Team
 */
public class FolderNameExistsException extends BusinessException {

    private static final long serialVersionUID = 3869043314332104018L;

    /**
     * Constructor with error code and message
     *
     * @param errorCode Error code (e.g., "FOLDER_004")
     * @param message   Error message from MessageSource
     */
    public FolderNameExistsException(final String errorCode, final String message) {
        super(errorCode, message);
    }
}
