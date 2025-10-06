package com.spacedlearning.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for learning statistics response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningStatisticsResponse {

    private UUID userId;
    private LocalDate date;
    private String statType;
    private BigDecimal value;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Additional fields for aggregated statistics
    private List<SetStatistics> setStatistics;
    private OverallStatistics overallStatistics;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SetStatistics {
        private UUID setId;
        private String setName;
        private Integer totalReviews;
        private BigDecimal averageScore;
        private Integer currentCycle;
        private String status;
        private LocalDateTime lastReviewedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OverallStatistics {
        private Integer totalSets;
        private Integer activeSets;
        private Integer masteredSets;
        private Integer totalReviews;
        private BigDecimal overallAverageScore;
        private Integer totalLearningDays;
        private Integer currentStreak;
        private Integer longestStreak;
    }
}
