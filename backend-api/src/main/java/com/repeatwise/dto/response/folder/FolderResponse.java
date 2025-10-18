package com.repeatwise.dto.response.folder;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for folder details
 *
 * Requirements:
 * - UC-005: Create Folder - Return created folder
 * - UC-006: Rename Folder - Return updated folder
 * - API Endpoints: GET /api/folders/{id}
 *
 * @author RepeatWise Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderResponse {

    /**
     * Folder unique identifier
     */
    private UUID id;

    /**
     * Folder name
     */
    private String name;

    /**
     * Folder description (optional)
     */
    private String description;

    /**
     * Parent folder ID (null if root folder)
     */
    private UUID parentFolderId;

    /**
     * Depth in folder tree (0 = root, max 10)
     */
    private Integer depth;

    /**
     * Materialized path (e.g., /uuid1/uuid2/uuid3)
     */
    private String path;

    /**
     * Number of direct child folders
     */
    @Builder.Default
    private Integer childrenCount = 0;

    /**
     * Number of direct decks in this folder
     */
    @Builder.Default
    private Integer deckCount = 0;

    /**
     * Creation timestamp
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant createdAt;

    /**
     * Last update timestamp
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant updatedAt;
}
