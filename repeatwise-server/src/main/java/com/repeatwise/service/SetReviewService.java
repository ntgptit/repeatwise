package com.repeatwise.service;

import com.repeatwise.dto.SetReviewDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SetReviewService {

    /**
     * Create a new review
     */
    SetReviewDto createReview(UUID cycleId, UUID userId, SetReviewDto reviewDto);

    /**
     * Find review by ID
     */
    Optional<SetReviewDto> findById(UUID id);

    /**
     * Find review by ID and cycle ID
     */
    Optional<SetReviewDto> findByIdAndCycleId(UUID id, UUID cycleId);

    /**
     * Update review
     */
    SetReviewDto updateReview(UUID id, UUID userId, SetReviewDto reviewDto);

    /**
     * Delete review (soft delete)
     */
    void deleteReview(UUID id, UUID userId);

    /**
     * Find all reviews for a cycle
     */
    List<SetReviewDto> findByCycleId(UUID cycleId);

    /**
     * Find reviews by cycle ID ordered by review number
     */
    List<SetReviewDto> findReviewsByCycleIdOrderByReviewNo(UUID cycleId);

    /**
     * Find review by review number and cycle ID
     */
    Optional<SetReviewDto> findByReviewNoAndCycleId(Integer reviewNo, UUID cycleId);

    /**
     * Get the next review number for a cycle
     */
    Integer getNextReviewNumber(UUID cycleId);

    /**
     * Count reviews for a cycle
     */
    long countByCycleId(UUID cycleId);

    /**
     * Find reviews by score range
     */
    List<SetReviewDto> findByScoreRange(UUID cycleId, Integer minScore, Integer maxScore);

    /**
     * Find reviews with score above threshold
     */
    List<SetReviewDto> findWithMinScore(UUID cycleId, Integer minScore);

    /**
     * Find reviews with score below threshold
     */
    List<SetReviewDto> findWithMaxScore(UUID cycleId, Integer maxScore);

    /**
     * Find reviews reviewed on a specific date
     */
    List<SetReviewDto> findReviewsReviewedOnDate(LocalDate date);

    /**
     * Find reviews reviewed between dates
     */
    List<SetReviewDto> findReviewsReviewedBetweenDates(LocalDate startDate, LocalDate endDate);

    /**
     * Calculate average score for a cycle
     */
    BigDecimal calculateAverageScore(UUID cycleId);

    /**
     * Find the highest score for a cycle
     */
    Integer findHighestScore(UUID cycleId);

    /**
     * Find the lowest score for a cycle
     */
    Integer findLowestScore(UUID cycleId);

    /**
     * Find reviews by user ID
     */
    List<SetReviewDto> findByUserId(UUID userId);

    /**
     * Find reviews by user ID and date range
     */
    List<SetReviewDto> findByUserIdAndDateRange(UUID userId, LocalDate startDate, LocalDate endDate);

    /**
     * Find reviews by set ID
     */
    List<SetReviewDto> findBySetId(UUID setId);

    /**
     * Calculate average score for a set across all cycles
     */
    BigDecimal calculateAverageScoreBySetId(UUID setId);

    /**
     * Find the most recent review for a cycle
     */
    Optional<SetReviewDto> findLatestReviewByCycleId(UUID cycleId);

    /**
     * Get review statistics
     */
    ReviewStatistics getReviewStatistics(UUID cycleId);

    /**
     * Review statistics data class
     */
    record ReviewStatistics(
        int totalReviews,
        BigDecimal averageScore,
        Integer highestScore,
        Integer lowestScore,
        LocalDate firstReviewDate,
        LocalDate lastReviewDate
    ) {}
} 
