package com.repeatwise.dto.response.folder;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for folder response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderResponse {

    private UUID id;
    private String name;
    private String description;
    private UUID parentFolderId;
    private Integer depth;
    private String path;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
