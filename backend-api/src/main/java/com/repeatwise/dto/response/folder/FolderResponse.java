package com.repeatwise.dto.response.folder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

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
