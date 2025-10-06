package com.spacedlearning.dto.statistics;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for learning statistics response
 * Contains comprehensive learning statistics for a user
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponse {

    // Overall Statistics
    private Integer totalSets;
    private Integer activeSets;
    private Integer completedSets;
    private Integer totalReviews;
    private BigDecimal averageScore;
    private Integer currentStreak;
    private Integer longestStreak;

    // Recent Activity
    private Integer reviewsToday;
    private Integer reviewsThisWeek;
    private Integer reviewsThisMonth;

    // Performance Metrics
    private Map<String, Integer> performanceByCategory;
    private List<DailyProgress> dailyProgress;
    private List<SetProgress> setProgress;

    // Learning Trends
    private BigDecimal improvementRate;
    private LocalDate lastReviewDate;
    private Integer totalStudyTime; // in minutes

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyProgress {
        private LocalDate date;
        private Integer reviewsCompleted;
        private BigDecimal averageScore;
        private Integer studyTime; // in minutes
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SetProgress {
        private String setName;
        private Integer totalReviews;
        private BigDecimal averageScore;
        private Integer currentCycle;
        private String status;
        private LocalDate lastReviewedAt;
    }
}
