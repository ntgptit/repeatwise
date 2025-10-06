package com.spacedlearning.dto.review;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.spacedlearning.entity.enums.ReviewStatus;
import com.spacedlearning.entity.enums.SkipReason;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for review session response
 * Contains review session data returned after successful creation or retrieval
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSessionResponse {

    private UUID id;
    
    private UUID setId;
    
    private UUID cycleId;
    
    private Integer reviewNumber;
    
    private Integer score;
    
    private ReviewStatus status;
    
    private SkipReason skipReason;
    
    private LocalDate reviewDate;
    
    private String notes;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
