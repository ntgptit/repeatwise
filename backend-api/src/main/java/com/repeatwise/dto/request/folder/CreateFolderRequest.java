package com.repeatwise.dto.request.folder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for creating a new folder
 *
 * Requirements:
 * - UC-005: Create Folder Hierarchy
 * - BR-010: Folder naming (1-100 chars, no special chars)
 * - BR-011: Max depth = 10 levels
 *
 * Validation:
 * - name: Required, 1-100 characters, trim whitespace
 * - description: Optional, max 500 characters
 * - parentFolderId: Optional (null = root folder)
 *
 * @author RepeatWise Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateFolderRequest {

    /**
     * Folder name (required, 1-100 chars)
     * Trimmed before validation
     * Must not contain special characters: < > " ' \ / |
     */
    @NotBlank(message = "{folder.name.required}")
    @Size(min = 1, max = 100, message = "{folder.name.size}")
    private String name;

    /**
     * Folder description (optional, max 500 chars)
     */
    @Size(max = 500, message = "{folder.description.size}")
    private String description;

    /**
     * Parent folder ID (optional)
     * - If null: Create root-level folder (depth = 0)
     * - If not null: Create as child of parent folder
     *
     * Validation:
     * - Parent must exist and belong to user
     * - Parent must not be deleted
     * - Resulting depth must not exceed 10
     */
    private UUID parentFolderId;
}
