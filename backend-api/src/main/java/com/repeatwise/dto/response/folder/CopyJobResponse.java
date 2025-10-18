package com.repeatwise.dto.response.folder;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for async folder copy job status
 *
 * Requirements:
 * - UC-008: Copy Folder (Async for large folders)
 * - BR-023: Async threshold (>50 items)
 * - API Endpoints: POST /api/folders/{id}/copy (returns job for large folders)
 *                  GET /api/folders/copy-jobs/{jobId} (check job status)
 *
 * Job Status Flow:
 * 1. PENDING - Job created, waiting to start
 * 2. RUNNING - Job in progress
 * 3. COMPLETED - Job finished successfully
 * 4. FAILED - Job failed with error
 *
 * @author RepeatWise Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CopyJobResponse {

    /**
     * Job unique identifier
     */
    private UUID jobId;

    /**
     * Source folder ID being copied
     */
    private UUID sourceFolderId;

    /**
     * Source folder name
     */
    private String sourceFolderName;

    /**
     * Target parent folder ID (null if copying to root)
     */
    private UUID targetParentFolderId;

    /**
     * New folder name for the copy
     */
    private String newFolderName;

    /**
     * Job status: PENDING, RUNNING, COMPLETED, FAILED
     */
    private String status;

    /**
     * Total items to copy (folders + decks + cards)
     */
    @Builder.Default
    private Integer totalItems = 0;

    /**
     * Items processed so far
     */
    @Builder.Default
    private Integer processedItems = 0;

    /**
     * Progress percentage (0-100)
     */
    @Builder.Default
    private Integer progressPercent = 0;

    /**
     * Result folder ID (populated when COMPLETED)
     */
    private UUID resultFolderId;

    /**
     * Error message (populated when FAILED)
     */
    private String errorMessage;

    /**
     * Job creation timestamp
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant createdAt;

    /**
     * Job start timestamp
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant startedAt;

    /**
     * Job completion timestamp
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant completedAt;

    /**
     * Estimated time remaining (in seconds)
     * Only available when status = RUNNING
     */
    private Long estimatedSecondsRemaining;
}
