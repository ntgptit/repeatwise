package com.repeatwise.exception;

/**
 * Exception thrown when attempting to move folder into itself or its descendant
 *
 * Requirements:
 * - UC-007: Move Folder - A1: Circular Reference Detected
 * - BR-017: Move validation - Cannot move folder into itself or descendants
 *
 * Use Cases:
 * - User tries to move folder A into folder B, where B is a descendant of A
 * - User tries to set folder as its own parent
 *
 * HTTP Status: 400 Bad Request
 *
 * @author RepeatWise Team
 */
public class CircularReferenceException extends BusinessException {

    private static final String ERROR_CODE = "FOLDER_CIRCULAR_REFERENCE";

    public CircularReferenceException(final String message) {
        super(ERROR_CODE, message);
    }

    public CircularReferenceException(final String folderName, final String targetName) {
        super(ERROR_CODE,
            String.format("Cannot move folder '%s' into '%s': This would create a circular reference",
                folderName, targetName));
    }
}
