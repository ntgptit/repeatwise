package com.repeatwise.dto.response.folder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for folder statistics response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderStatsResponse {

    private UUID folderId;
    private Integer totalCardsCount;
    private Integer dueCardsCount;
    private Integer newCardsCount;
    private Integer matureCardsCount;
    private LocalDateTime lastComputedAt;
}
