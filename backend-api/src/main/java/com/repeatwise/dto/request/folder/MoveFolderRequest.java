package com.repeatwise.dto.request.folder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

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
