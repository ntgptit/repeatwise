package com.spacedlearning.dto.set;

import com.spacedlearning.dto.review.ReviewHistoryResponse;
import com.spacedlearning.dto.reminder.RemindScheduleResponse;
import com.spacedlearning.entity.enums.SetCategory;
import com.spacedlearning.entity.enums.SetStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningSetDetailResponse {

    private UUID id;
    private String name;
    private String description;
    private SetCategory category;
    private Integer wordCount;
    private SetStatus status;
    private Integer currentCycle;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    
    // Additional details
    private List<ReviewHistoryResponse> reviewHistories;
    private List<RemindScheduleResponse> remindSchedules;
    private Double currentCycleAverageScore;
    private Integer completedReviewsInCurrentCycle;
    private LocalDateTime nextReviewDate;
}
