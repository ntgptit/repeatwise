package com.repeatwise.dto.response.folder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for folder tree (with statistics)
 *
 * Requirements:
 * - UC-005: Create Folder Hierarchy - Display folder tree
 * - UC-010: View Folder Statistics - Display card counts
 * - API Endpoints: GET /api/folders (tree view)
 *
 * Used for sidebar folder tree with card statistics
 *
 * @author RepeatWise Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderTreeResponse {

    /**
     * Folder unique identifier
     */
    private UUID id;

    /**
     * Folder name
     */
    private String name;

    /**
     * Parent folder ID (null if root folder)
     */
    private UUID parentId;

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
     * Total cards in folder and all descendants
     * Includes cards from all nested folders and decks
     */
    @Builder.Default
    private Integer totalCards = 0;

    /**
     * Due cards (due_date <= today) in folder and descendants
     * Used to show "due count" badge in UI
     */
    @Builder.Default
    private Integer dueCards = 0;

    /**
     * New cards (never reviewed) in folder and descendants
     */
    @Builder.Default
    private Integer newCards = 0;

    /**
     * Mature cards (box >= 5) in folder and descendants
     */
    @Builder.Default
    private Integer matureCards = 0;

    /**
     * Child folders (for recursive tree structure)
     * Only populated if requested (expandChildren parameter)
     */
    @Builder.Default
    private List<FolderTreeResponse> children = new ArrayList<>();

    /**
     * Helper method to add child folder
     */
    public void addChild(final FolderTreeResponse child) {
        this.children.add(child);
    }
}
