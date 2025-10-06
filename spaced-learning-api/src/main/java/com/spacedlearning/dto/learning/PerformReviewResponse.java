package com.spacedlearning.dto.learning;

import com.spacedlearning.entity.enums.SetStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for review session response based on UC-011: Perform Review Session
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformReviewResponse {

    private UUID reviewId;
    private UUID setId;
    private String setName;
    private Integer cycleNumber;
    private Integer reviewNumber;
    private Integer score;
    private String status;
    private String skipReason;
    private String notes;
    private LocalDateTime reviewDate;
    private SetStatus setStatus;
    private Integer totalReviews;
    private Double averageScore;
    private Boolean cycleCompleted;
    private Boolean setMastered;
    private String message;
    private LocalDateTime nextReminderDate;
}
