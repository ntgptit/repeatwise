package com.repeatwise.dto.response.folder;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for folder statistics response (UC-012).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderStatsResponse {

    private UUID folderId;
    private String folderName;
    private Integer totalFolders;
    private Integer totalDecks;
    private Integer totalCards;
    private Integer dueCards;
    private Integer newCards;
    private Integer learningCards;
    private Integer reviewCards;
    private Integer masteredCards;
    private Double completionRate;
    private Boolean cached;
    private LocalDateTime lastUpdatedAt;
}
