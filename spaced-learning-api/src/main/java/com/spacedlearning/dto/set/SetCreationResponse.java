package com.spacedlearning.dto.set;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.spacedlearning.entity.enums.SetCategory;
import com.spacedlearning.entity.enums.SetStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for learning set creation response
 * Contains learning set data returned after successful creation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetCreationResponse {

    private UUID id;
    
    private String name;
    
    private String description;
    
    private SetCategory category;
    
    private Integer wordCount;
    
    private SetStatus status;
    
    private Integer currentCycle;
    
    private Integer totalReviews;
    
    private BigDecimal averageScore;
    
    private LocalDateTime lastReviewedAt;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
