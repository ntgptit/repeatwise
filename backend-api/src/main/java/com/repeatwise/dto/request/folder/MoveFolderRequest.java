package com.repeatwise.dto.request.folder;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for moving a folder to another parent
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoveFolderRequest {

    private UUID targetParentFolderId; // null = move to root
}
