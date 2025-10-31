package com.repeatwise.dto.request.folder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for copying folder (with all contents)
 *
 * Requirements:
 * - UC-010: Copy Folder
 * - BR-021: Copy scope (folders, decks, cards)
 * - BR-022: SRS state reset (all cards to Box 1)
 * - BR-023: Async threshold (<=50 items sync, 51-500 async, >500 reject)
 * - BR-024: Auto-naming (Copy, Copy 2, Copy 3...)
 *
 * Validation:
 * - Resulting depth must not exceed 10
 * - Name must be unique in target parent
 *
 * Copy Behavior:
 * - Small folder (<=50 items): Synchronous copy (< 5 seconds)
 * - Large folder (51-500 items): Asynchronous job (not implemented yet)
 * - Too large (>500 items): Rejected with error
 * - All cards reset to Box 1 (fresh SRS state)
 *
 * @author RepeatWise Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CopyFolderRequest {

    /**
     * Target parent folder ID
     * - If null: Copy to root level
     * - If not null: Copy under specified parent
     */
    private UUID targetParentFolderId;

    /**
     * Name for copied folder (required)
     * Default suggestion: "{original_name} (Copy)"
     *
     * Auto-renaming on duplicate:
     * - "Folder Name (Copy)"
     * - "Folder Name (Copy 2)"
     * - "Folder Name (Copy 3)"
     */
    @NotBlank(message = "{folder.copy.name.required}")
    @Size(min = 1, max = 100, message = "{folder.name.size}")
    private String newName;

    /**
     * Include sub-folders in copy (default: true)
     * If false: Only copy the root folder and its direct decks
     */
    @Builder.Default
    private Boolean includeSubfolders = true;

    /**
     * Include cards in copy (default: true)
     * If false: Copy folder structure and decks, but not cards
     * (Useful for creating empty template structures)
     */
    @Builder.Default
    private Boolean includeCards = true;

    /**
     * Reset SRS progress for copied cards (default: true)
     * If true: All cards reset to Box 1, due_date = tomorrow
     * If false: Keep original SRS state (future feature)
     */
    @Builder.Default
    private Boolean resetSrsProgress = true;
}
