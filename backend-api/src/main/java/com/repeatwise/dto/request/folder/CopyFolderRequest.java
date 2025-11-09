package com.repeatwise.dto.request.folder;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for copying a folder to a new location
 * Used in UC-010: Copy Folder
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CopyFolderRequest {

    /**
     * Destination parent folder ID (null = copy to root)
     */
    private UUID destinationFolderId;

    /**
     * Optional new name for the copied folder
     * If not provided, system will generate unique name like "FolderName (copy)"
     */
    @Size(min = 1, max = 100, message = "{error.folder.name.size}")
    private String newName;

    /**
     * Naming policy for handling name conflicts
     * Options: APPEND_COPY_SUFFIX (default), REPLACE, SKIP
     * MVP: Only APPEND_COPY_SUFFIX is supported
     */
    private String renamePolicy;
}
