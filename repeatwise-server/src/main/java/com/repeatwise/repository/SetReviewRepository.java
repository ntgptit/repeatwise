package com.repeatwise.repository;

import com.repeatwise.model.SetReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SetReviewRepository extends JpaRepository<SetReview, UUID> {

    /**
     * Find all reviews by set cycle ID
     */
    List<SetReview> findBySetCycleId(UUID setCycleId);

    /**
     * Find reviews by set cycle ID ordered by review number
     */
    @Query("SELECT sr FROM SetReview sr WHERE sr.setCycle.id = :setCycleId AND sr.deletedAt IS NULL ORDER BY sr.reviewNo ASC")
    List<SetReview> findReviewsBySetCycleIdOrderByReviewNo(@Param("setCycleId") UUID setCycleId);

    /**
     * Find review by ID and set cycle ID
     */
    Optional<SetReview> findByIdAndSetCycleId(UUID id, UUID setCycleId);

    /**
     * Find review by review number and set cycle ID
     */
    Optional<SetReview> findByReviewNoAndSetCycleId(Integer reviewNo, UUID setCycleId);

    /**
     * Find the next review number for a set cycle
     */
    @Query("SELECT COALESCE(MAX(sr.reviewNo), 0) + 1 FROM SetReview sr WHERE sr.setCycle.id = :setCycleId AND sr.deletedAt IS NULL")
    Integer findNextReviewNumber(@Param("setCycleId") UUID setCycleId);

    /**
     * Count reviews by set cycle ID
     */
    long countBySetCycleId(UUID setCycleId);

    /**
     * Find reviews by score range
     */
    @Query("SELECT sr FROM SetReview sr WHERE sr.score >= :minScore AND sr.score <= :maxScore AND sr.deletedAt IS NULL")
    List<SetReview> findReviewsByScoreRange(@Param("minScore") Integer minScore, @Param("maxScore") Integer maxScore);

    /**
     * Find reviews with score above threshold
     */
    @Query("SELECT sr FROM SetReview sr WHERE sr.score >= :minScore AND sr.deletedAt IS NULL")
    List<SetReview> findReviewsWithMinScore(@Param("minScore") Integer minScore);

    /**
     * Find reviews with score below threshold
     */
    @Query("SELECT sr FROM SetReview sr WHERE sr.score < :maxScore AND sr.deletedAt IS NULL")
    List<SetReview> findReviewsWithMaxScore(@Param("maxScore") Integer maxScore);

    /**
     * Find reviews reviewed on a specific date
     */
    @Query("SELECT sr FROM SetReview sr WHERE sr.reviewedAt = :date AND sr.deletedAt IS NULL")
    List<SetReview> findReviewsReviewedOnDate(@Param("date") LocalDate date);

    /**
     * Find reviews reviewed between dates
     */
    @Query("SELECT sr FROM SetReview sr WHERE sr.reviewedAt BETWEEN :startDate AND :endDate AND sr.deletedAt IS NULL")
    List<SetReview> findReviewsReviewedBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Calculate average score for a set cycle
     */
    @Query("SELECT AVG(sr.score) FROM SetReview sr WHERE sr.setCycle.id = :setCycleId AND sr.deletedAt IS NULL")
    BigDecimal findAverageScoreBySetCycleId(@Param("setCycleId") UUID setCycleId);

    /**
     * Find the highest score for a set cycle
     */
    @Query("SELECT MAX(sr.score) FROM SetReview sr WHERE sr.setCycle.id = :setCycleId AND sr.deletedAt IS NULL")
    Integer findHighestScoreBySetCycleId(@Param("setCycleId") UUID setCycleId);

    /**
     * Find the lowest score for a set cycle
     */
    @Query("SELECT MIN(sr.score) FROM SetReview sr WHERE sr.setCycle.id = :setCycleId AND sr.deletedAt IS NULL")
    Integer findLowestScoreBySetCycleId(@Param("setCycleId") UUID setCycleId);

    /**
     * Find reviews by user ID
     */
    @Query("SELECT sr FROM SetReview sr WHERE sr.setCycle.set.user.id = :userId AND sr.deletedAt IS NULL")
    List<SetReview> findReviewsByUserId(@Param("userId") UUID userId);

    /**
     * Find reviews by user ID and date range
     */
    @Query("SELECT sr FROM SetReview sr WHERE sr.setCycle.set.user.id = :userId AND sr.reviewedAt BETWEEN :startDate AND :endDate AND sr.deletedAt IS NULL")
    List<SetReview> findReviewsByUserIdAndDateRange(@Param("userId") UUID userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find reviews by set ID
     */
    @Query("SELECT sr FROM SetReview sr WHERE sr.setCycle.set.id = :setId AND sr.deletedAt IS NULL ORDER BY sr.reviewedAt DESC")
    List<SetReview> findReviewsBySetId(@Param("setId") UUID setId);

    /**
     * Calculate average score for a set across all cycles
     */
    @Query("SELECT AVG(sr.score) FROM SetReview sr WHERE sr.setCycle.set.id = :setId AND sr.deletedAt IS NULL")
    BigDecimal findAverageScoreBySetId(@Param("setId") UUID setId);

    /**
     * Find the most recent review for a set cycle
     */
    @Query("SELECT sr FROM SetReview sr WHERE sr.setCycle.id = :setCycleId AND sr.deletedAt IS NULL ORDER BY sr.reviewedAt DESC LIMIT 1")
    Optional<SetReview> findLatestReviewBySetCycleId(@Param("setCycleId") UUID setCycleId);

    /**
     * Find reviews that need to be completed (less than 5 reviews for active cycles)
     */
    @Query("SELECT sr.setCycle FROM SetReview sr WHERE sr.setCycle.status = 'ACTIVE' AND sr.deletedAt IS NULL GROUP BY sr.setCycle HAVING COUNT(sr) < 5")
    List<SetReview> findCyclesWithIncompleteReviews();
} 
