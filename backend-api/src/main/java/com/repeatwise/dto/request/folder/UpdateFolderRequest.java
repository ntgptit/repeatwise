package com.repeatwise.dto.request.folder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating folder (rename and update description)
 *
 * Requirements:
 * - UC-008: Rename Folder
 * - BR-014: Rename validation (unique name within parent)
 * - BR-015: Only name and description can be changed via update
 *
 * Validation:
 * - name: Required, 1-100 characters
 * - description: Optional, max 500 characters
 *
 * Note:
 * - To move folder to different parent, use MoveFolderRequest
 * - Path, depth, parentFolderId cannot be changed via update
 *
 * @author RepeatWise Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateFolderRequest {

    /**
     * New folder name (required, 1-100 chars)
     *
     * Validation:
     * - Must be unique within same parent folder (BR-014)
     * - Must not be empty
     * - Trimmed before validation
     */
    @NotBlank(message = "{folder.name.required}")
    @Size(min = 1, max = 100, message = "{folder.name.size}")
    private String name;

    /**
     * New folder description (optional, max 500 chars)
     */
    @Size(max = 500, message = "{folder.description.size}")
    private String description;
}
