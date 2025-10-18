package com.repeatwise.dto.response.folder;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for folder statistics
 *
 * Requirements:
 * - UC-010: View Folder Statistics
 * - API Endpoints: GET /api/folders/{id}/stats
 *
 * @author RepeatWise Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderStatsResponse {

    /**
     * Folder unique identifier
     */
    private UUID folderId;

    /**
     * Folder name (for display)
     */
    private String folderName;

    /**
     * Total cards in folder and all descendants
     */
    @Builder.Default
    private Integer totalCards = 0;

    /**
     * Due cards (due_date <= today)
     */
    @Builder.Default
    private Integer dueCards = 0;

    /**
     * New cards (never reviewed, review_count = 0)
     */
    @Builder.Default
    private Integer newCards = 0;

    /**
     * Learning cards (box 1-4)
     */
    @Builder.Default
    private Integer learningCards = 0;

    /**
     * Mature cards (box 5-7)
     */
    @Builder.Default
    private Integer matureCards = 0;

    /**
     * Timestamp when stats were last computed
     * Used to show "as of" information to user
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant lastComputedAt;

    /**
     * Indicates if stats are stale (>5 minutes old)
     * If true, stats are being recomputed in background
     */
    @Builder.Default
    private Boolean isStale = false;
}
