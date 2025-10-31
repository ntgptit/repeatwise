package com.repeatwise.exception;

/**
 * Exception thrown when folder operation would exceed maximum depth limit
 *
 * Requirements:
 * - UC-005: Create Folder - A2: Max Depth Exceeded
 * - UC-007: Move Folder - A2: Max Depth Exceeded
 * - UC-008: Copy Folder - A3: Max Depth Exceeded
 * - BR-011: Max depth = 10 levels
 *
 * Use Cases:
 * - Creating folder at depth 11
 * - Moving folder would cause descendants to exceed depth 10
 * - Copying folder would result in descendants beyond depth 10
 *
 * HTTP Status: 400 Bad Request
 *
 * @author RepeatWise Team
 */
public class MaxDepthExceededException extends BusinessException {

    private static final long serialVersionUID = -1230553216309323214L;
    private static final String ERROR_CODE = "FOLDER_MAX_DEPTH_EXCEEDED";
    private static final int MAX_DEPTH = 10;

    public MaxDepthExceededException(final String message) {
        super(ERROR_CODE, message);
    }

    public MaxDepthExceededException(final Integer resultingDepth) {
        super(ERROR_CODE,
                String.format("Cannot perform operation: Would result in depth %d (max is %d)",
                        resultingDepth, MAX_DEPTH));
    }

    public MaxDepthExceededException(final String folderName, final Integer currentDepth,
            final Integer maxDescendantDepth, final Integer targetDepth) {
        super(ERROR_CODE,
                String.format("Cannot move folder '%s': Would exceed maximum depth. " +
                        "Current depth: %d, Deepest descendant: %d, Target depth: %d, Max allowed: %d",
                        folderName, currentDepth, maxDescendantDepth, targetDepth, MAX_DEPTH));
    }
}
