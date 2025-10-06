package com.spacedlearning.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spacedlearning.entity.ReviewHistory;
import com.spacedlearning.entity.enums.ReviewStatus;
import com.spacedlearning.entity.enums.SkipReason;

/**
 * Repository interface for ReviewHistory entity
 * Provides data access methods for review history management
 */
@Repository
public interface ReviewHistoryRepository extends JpaRepository<ReviewHistory, UUID> {

    /**
     * Find review histories by learning set
     * @param setId learning set ID
     * @return list of review histories for learning set
     */
    List<ReviewHistory> findByLearningSetId(UUID setId);

    /**
     * Find review histories by learning set with pagination
     * @param setId learning set ID
     * @param pageable pagination information
     * @return page of review histories for learning set
     */
    Page<ReviewHistory> findByLearningSetId(UUID setId, Pageable pageable);

    /**
     * Find review histories by learning cycle
     * @param cycleId learning cycle ID
     * @return list of review histories for learning cycle
     */
    List<ReviewHistory> findByLearningCycleId(UUID cycleId);

    /**
     * Find review histories by learning set and cycle
     * @param setId learning set ID
     * @param cycleId learning cycle ID
     * @return list of review histories for learning set and cycle
     */
    List<ReviewHistory> findByLearningSetIdAndLearningCycleId(UUID setId, UUID cycleId);

    /**
     * Find review histories by status
     * @param status review status
     * @return list of review histories with status
     */
    List<ReviewHistory> findByStatus(ReviewStatus status);

    /**
     * Find review histories by learning set and status
     * @param setId learning set ID
     * @param status review status
     * @return list of review histories for learning set with status
     */
    List<ReviewHistory> findByLearningSetIdAndStatus(UUID setId, ReviewStatus status);

    /**
     * Find review histories by learning cycle and status
     * @param cycleId learning cycle ID
     * @param status review status
     * @return list of review histories for learning cycle with status
     */
    List<ReviewHistory> findByLearningCycleIdAndStatus(UUID cycleId, ReviewStatus status);

    /**
     * Find review histories by review date
     * @param reviewDate review date
     * @return list of review histories on date
     */
    List<ReviewHistory> findByReviewDate(LocalDate reviewDate);

    /**
     * Find review histories by review date range
     * @param startDate start date
     * @param endDate end date
     * @return list of review histories between dates
     */
    List<ReviewHistory> findByReviewDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find review histories by learning set and review date range
     * @param setId learning set ID
     * @param startDate start date
     * @param endDate end date
     * @return list of review histories for learning set between dates
     */
    List<ReviewHistory> findByLearningSetIdAndReviewDateBetween(UUID setId, LocalDate startDate, LocalDate endDate);

    /**
     * Find review histories by score range
     * @param minScore minimum score
     * @param maxScore maximum score
     * @return list of review histories with scores in range
     */
    List<ReviewHistory> findByScoreBetween(Integer minScore, Integer maxScore);

    /**
     * Find review histories by learning set and score range
     * @param setId learning set ID
     * @param minScore minimum score
     * @param maxScore maximum score
     * @return list of review histories for learning set with scores in range
     */
    List<ReviewHistory> findByLearningSetIdAndScoreBetween(UUID setId, Integer minScore, Integer maxScore);

    /**
     * Find review histories by skip reason
     * @param skipReason skip reason
     * @return list of review histories with skip reason
     */
    List<ReviewHistory> findBySkipReason(SkipReason skipReason);

    /**
     * Find review histories by learning set and skip reason
     * @param setId learning set ID
     * @param skipReason skip reason
     * @return list of review histories for learning set with skip reason
     */
    List<ReviewHistory> findByLearningSetIdAndSkipReason(UUID setId, SkipReason skipReason);

    /**
     * Find review history by learning cycle and review number
     * @param cycleId learning cycle ID
     * @param reviewNumber review number
     * @return Optional containing review history if found
     */
    Optional<ReviewHistory> findByLearningCycleIdAndReviewNumber(UUID cycleId, Integer reviewNumber);

    /**
     * Find review histories by review number
     * @param reviewNumber review number
     * @return list of review histories with review number
     */
    List<ReviewHistory> findByReviewNumber(Integer reviewNumber);

    /**
     * Find review histories by learning set and review number
     * @param setId learning set ID
     * @param reviewNumber review number
     * @return list of review histories for learning set with review number
     */
    List<ReviewHistory> findByLearningSetIdAndReviewNumber(UUID setId, Integer reviewNumber);

    /**
     * Find completed review histories
     * @return list of completed review histories
     */
    @Query("SELECT rh FROM ReviewHistory rh WHERE rh.status = 'COMPLETED'")
    List<ReviewHistory> findCompletedReviews();

    /**
     * Find completed review histories by learning set
     * @param setId learning set ID
     * @return list of completed review histories for learning set
     */
    @Query("SELECT rh FROM ReviewHistory rh WHERE rh.learningSet.id = :setId AND rh.status = 'COMPLETED'")
    List<ReviewHistory> findCompletedReviewsByLearningSet(@Param("setId") UUID setId);

    /**
     * Find skipped review histories
     * @return list of skipped review histories
     */
    @Query("SELECT rh FROM ReviewHistory rh WHERE rh.status = 'SKIPPED'")
    List<ReviewHistory> findSkippedReviews();

    /**
     * Find skipped review histories by learning set
     * @param setId learning set ID
     * @return list of skipped review histories for learning set
     */
    @Query("SELECT rh FROM ReviewHistory rh WHERE rh.learningSet.id = :setId AND rh.status = 'SKIPPED'")
    List<ReviewHistory> findSkippedReviewsByLearningSet(@Param("setId") UUID setId);

    /**
     * Find review histories with scores
     * @return list of review histories with scores
     */
    @Query("SELECT rh FROM ReviewHistory rh WHERE rh.score IS NOT NULL")
    List<ReviewHistory> findReviewsWithScores();

    /**
     * Find review histories with scores by learning set
     * @param setId learning set ID
     * @return list of review histories with scores for learning set
     */
    @Query("SELECT rh FROM ReviewHistory rh WHERE rh.learningSet.id = :setId AND rh.score IS NOT NULL")
    List<ReviewHistory> findReviewsWithScoresByLearningSet(@Param("setId") UUID setId);

    /**
     * Find review histories without scores
     * @return list of review histories without scores
     */
    @Query("SELECT rh FROM ReviewHistory rh WHERE rh.score IS NULL")
    List<ReviewHistory> findReviewsWithoutScores();

    /**
     * Find review histories without scores by learning set
     * @param setId learning set ID
     * @return list of review histories without scores for learning set
     */
    @Query("SELECT rh FROM ReviewHistory rh WHERE rh.learningSet.id = :setId AND rh.score IS NULL")
    List<ReviewHistory> findReviewsWithoutScoresByLearningSet(@Param("setId") UUID setId);

    /**
     * Count review histories by learning set
     * @param setId learning set ID
     * @return count of review histories for learning set
     */
    long countByLearningSetId(UUID setId);

    /**
     * Count review histories by learning set and status
     * @param setId learning set ID
     * @param status review status
     * @return count of review histories for learning set with status
     */
    long countByLearningSetIdAndStatus(UUID setId, ReviewStatus status);

    /**
     * Count review histories by learning cycle
     * @param cycleId learning cycle ID
     * @return count of review histories for learning cycle
     */
    long countByLearningCycleId(UUID cycleId);

    /**
     * Count review histories by learning cycle and status
     * @param cycleId learning cycle ID
     * @param status review status
     * @return count of review histories for learning cycle with status
     */
    long countByLearningCycleIdAndStatus(UUID cycleId, ReviewStatus status);

    /**
     * Calculate average score for learning set
     * @param setId learning set ID
     * @return average score for learning set
     */
    @Query("SELECT AVG(rh.score) FROM ReviewHistory rh WHERE rh.learningSet.id = :setId AND rh.score IS NOT NULL")
    Double calculateAverageScoreByLearningSet(@Param("setId") UUID setId);

    /**
     * Calculate average score for learning cycle
     * @param cycleId learning cycle ID
     * @return average score for learning cycle
     */
    @Query("SELECT AVG(rh.score) FROM ReviewHistory rh WHERE rh.learningCycle.id = :cycleId AND rh.score IS NOT NULL")
    Double calculateAverageScoreByLearningCycle(@Param("cycleId") UUID cycleId);

    /**
     * Find review histories by notes containing text
     * @param notes notes to search for
     * @return list of review histories with matching notes
     */
    @Query("SELECT rh FROM ReviewHistory rh WHERE LOWER(rh.notes) LIKE LOWER(CONCAT('%', :notes, '%'))")
    List<ReviewHistory> findByNotesContainingIgnoreCase(@Param("notes") String notes);

    /**
     * Find review histories by learning set and notes containing text
     * @param setId learning set ID
     * @param notes notes to search for
     * @return list of review histories for learning set with matching notes
     */
    @Query("SELECT rh FROM ReviewHistory rh WHERE rh.learningSet.id = :setId AND LOWER(rh.notes) LIKE LOWER(CONCAT('%', :notes, '%'))")
    List<ReviewHistory> findByLearningSetIdAndNotesContainingIgnoreCase(@Param("setId") UUID setId, @Param("notes") String notes);

    /**
     * Find review histories created after specified date
     * @param date creation date
     * @return list of review histories created after date
     */
    @Query("SELECT rh FROM ReviewHistory rh WHERE rh.createdAt > :date")
    List<ReviewHistory> findByCreatedAtAfter(@Param("date") java.time.LocalDateTime date);

    /**
     * Find review histories created between dates
     * @param startDate start date
     * @param endDate end date
     * @return list of review histories created between dates
     */
    @Query("SELECT rh FROM ReviewHistory rh WHERE rh.createdAt BETWEEN :startDate AND :endDate")
    List<ReviewHistory> findByCreatedAtBetween(@Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    /**
     * Find review histories by learning set created between dates
     * @param setId learning set ID
     * @param startDate start date
     * @param endDate end date
     * @return list of review histories for learning set created between dates
     */
    @Query("SELECT rh FROM ReviewHistory rh WHERE rh.learningSet.id = :setId AND rh.createdAt BETWEEN :startDate AND :endDate")
    List<ReviewHistory> findByLearningSetIdAndCreatedAtBetween(@Param("setId") UUID setId, @Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    // Additional methods for LearningCycleService
    /**
     * Find review histories by learning set and cycle number
     * @param learningSet learning set
     * @param cycleNumber cycle number
     * @return list of review histories for learning set and cycle
     */
    List<ReviewHistory> findByLearningSetAndCycleNumber(com.spacedlearning.entity.LearningSet learningSet, Integer cycleNumber);

    /**
     * Find review history by learning set, cycle number and review number
     * @param learningSet learning set
     * @param cycleNumber cycle number
     * @param reviewNumber review number
     * @return optional review history
     */
    Optional<ReviewHistory> findByLearningSetAndCycleNumberAndReviewNumber(
            com.spacedlearning.entity.LearningSet learningSet, Integer cycleNumber, Integer reviewNumber);

    /**
     * Check if review exists by learning set, cycle number and review number
     * @param learningSet learning set
     * @param cycleNumber cycle number
     * @param reviewNumber review number
     * @return true if exists, false otherwise
     */
    boolean existsByLearningSetAndCycleNumberAndReviewNumber(
            com.spacedlearning.entity.LearningSet learningSet, Integer cycleNumber, Integer reviewNumber);

    /**
     * Count review histories by learning set, cycle number and status
     * @param learningSet learning set
     * @param cycleNumber cycle number
     * @param status review status
     * @return count of review histories
     */
    long countByLearningSetAndCycleNumberAndStatus(
            com.spacedlearning.entity.LearningSet learningSet, Integer cycleNumber, ReviewStatus status);
}