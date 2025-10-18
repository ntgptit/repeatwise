package com.repeatwise.dto.request.folder;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for moving folder to new parent
 *
 * Requirements:
 * - UC-007: Move Folder
 * - BR-017: Move validation (no circular ref, max depth, unique name)
 * - BR-018: Path recalculation for folder and all descendants
 * - BR-019: Depth recalculation with delta propagation
 *
 * Validation:
 * - Cannot move folder into itself
 * - Cannot move folder into any of its descendants (circular reference)
 * - Resulting depth must not exceed 10 for any descendant
 * - Name must be unique in target parent
 *
 * @author RepeatWise Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoveFolderRequest {

    /**
     * Target parent folder ID
     * - If null: Move to root level (parentFolderId = null, depth = 0)
     * - If not null: Move under specified parent folder
     *
     * Validation:
     * - Target parent must exist and belong to user (if not null)
     * - Target parent must not be the folder itself
     * - Target parent must not be a descendant of the folder
     * - Resulting max depth must not exceed 10
     *
     * NOTE: Nullable to allow moving to root level (BR-017, UC-007 A4)
     */
    private UUID newParentFolderId;

    /**
     * Optional: New name for folder (rename during move)
     * If null, keep current name
     * If provided, must be unique in target parent
     */
    private String newName;
}
