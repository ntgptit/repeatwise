package com.spacedlearning.service;

import com.spacedlearning.dto.statistics.LearningStatisticsRequest;
import com.spacedlearning.dto.statistics.LearningStatisticsResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for Statistics operations
 */
public interface StatisticsService {

    /**
     * Get learning statistics for user
     * 
     * @param request Statistics request
     * @return Learning statistics response
     */
    LearningStatisticsResponse getLearningStatistics(LearningStatisticsRequest request);

    /**
     * Get daily statistics for user
     * 
     * @param userId User ID
     * @param date Date
     * @return Daily statistics
     */
    LearningStatisticsResponse getDailyStatistics(UUID userId, LocalDate date);

    /**
     * Get weekly statistics for user
     * 
     * @param userId User ID
     * @param startDate Week start date
     * @return Weekly statistics
     */
    LearningStatisticsResponse getWeeklyStatistics(UUID userId, LocalDate startDate);

    /**
     * Get monthly statistics for user
     * 
     * @param userId User ID
     * @param year Year
     * @param month Month (1-12)
     * @return Monthly statistics
     */
    LearningStatisticsResponse getMonthlyStatistics(UUID userId, int year, int month);

    /**
     * Get overall statistics for user
     * 
     * @param userId User ID
     * @return Overall statistics
     */
    LearningStatisticsResponse getOverallStatistics(UUID userId);

    /**
     * Get statistics for specific set
     * 
     * @param userId User ID
     * @param setId Set ID
     * @return Set statistics
     */
    LearningStatisticsResponse getSetStatistics(UUID userId, UUID setId);

    /**
     * Get statistics history for user
     * 
     * @param userId User ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of statistics
     */
    List<LearningStatisticsResponse> getStatisticsHistory(UUID userId, LocalDate startDate, LocalDate endDate);

    /**
     * Update statistics after review completion
     * 
     * @param userId User ID
     * @param setId Set ID
     * @param score Review score
     */
    void updateStatisticsAfterReview(UUID userId, UUID setId, Integer score);

    /**
     * Update statistics after cycle completion
     * 
     * @param userId User ID
     * @param setId Set ID
     * @param averageScore Cycle average score
     */
    void updateStatisticsAfterCycleCompletion(UUID userId, UUID setId, Double averageScore);
}
