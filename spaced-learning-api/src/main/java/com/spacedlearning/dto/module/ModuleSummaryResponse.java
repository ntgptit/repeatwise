package com.spacedlearning.dto.module;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for summarized module response (without progress)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleSummaryResponse {
    private UUID id;
    private UUID bookId;
    private Integer moduleNo;
    private String title;
    private Integer wordCount;
    private String url;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Integer vocabularyCount;
    private Integer grammarCount;
}