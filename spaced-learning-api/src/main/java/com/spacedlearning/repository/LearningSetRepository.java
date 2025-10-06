package com.spacedlearning.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spacedlearning.entity.LearningSet;
import com.spacedlearning.entity.enums.SetCategory;
import com.spacedlearning.entity.enums.SetStatus;

/**
 * Repository interface for LearningSet entity
 * Provides data access methods for learning set management
 */
@Repository
public interface LearningSetRepository extends JpaRepository<LearningSet, UUID> {

    /**
     * Find learning sets by user
     * @param userId user ID
     * @return list of learning sets for user
     */
    List<LearningSet> findByUserId(UUID userId);

    /**
     * Find learning sets by user with pagination
     * @param userId user ID
     * @param pageable pagination information
     * @return page of learning sets for user
     */
    Page<LearningSet> findByUserId(UUID userId, Pageable pageable);

    /**
     * Find learning sets by user and status
     * @param userId user ID
     * @param status set status
     * @return list of learning sets for user with status
     */
    List<LearningSet> findByUserIdAndStatus(UUID userId, SetStatus status);

    /**
     * Find learning sets by user and category
     * @param userId user ID
     * @param category set category
     * @return list of learning sets for user with category
     */
    List<LearningSet> findByUserIdAndCategory(UUID userId, SetCategory category);

    /**
     * Find learning sets by status
     * @param status set status
     * @return list of learning sets with status
     */
    List<LearningSet> findByStatus(SetStatus status);

    /**
     * Find learning sets by category
     * @param category set category
     * @return list of learning sets with category
     */
    List<LearningSet> findByCategory(SetCategory category);

    /**
     * Find learning sets by name containing text (case insensitive)
     * @param userId user ID
     * @param name name to search for
     * @return list of learning sets with matching names
     */
    @Query("SELECT ls FROM LearningSet ls WHERE ls.user.id = :userId AND LOWER(ls.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<LearningSet> findByUserIdAndNameContainingIgnoreCase(@Param("userId") UUID userId, @Param("name") String name);

    /**
     * Find learning sets by name containing text with pagination
     * @param userId user ID
     * @param name name to search for
     * @param pageable pagination information
     * @return page of learning sets with matching names
     */
    @Query("SELECT ls FROM LearningSet ls WHERE ls.user.id = :userId AND LOWER(ls.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<LearningSet> findByUserIdAndNameContainingIgnoreCase(@Param("userId") UUID userId, @Param("name") String name, Pageable pageable);

    /**
     * Find learning sets by user and word count range
     * @param userId user ID
     * @param minWords minimum word count
     * @param maxWords maximum word count
     * @return list of learning sets with word count in range
     */
    @Query("SELECT ls FROM LearningSet ls WHERE ls.user.id = :userId AND ls.wordCount BETWEEN :minWords AND :maxWords")
    List<LearningSet> findByUserIdAndWordCountBetween(@Param("userId") UUID userId, @Param("minWords") Integer minWords, @Param("maxWords") Integer maxWords);

    /**
     * Find learning sets by user and current cycle
     * @param userId user ID
     * @param currentCycle current cycle number
     * @return list of learning sets with current cycle
     */
    List<LearningSet> findByUserIdAndCurrentCycle(UUID userId, Integer currentCycle);

    /**
     * Find learning sets by user and average score range
     * @param userId user ID
     * @param minScore minimum average score
     * @param maxScore maximum average score
     * @return list of learning sets with average score in range
     */
    @Query("SELECT ls FROM LearningSet ls WHERE ls.user.id = :userId AND ls.averageScore BETWEEN :minScore AND :maxScore")
    List<LearningSet> findByUserIdAndAverageScoreBetween(@Param("userId") UUID userId, @Param("minScore") java.math.BigDecimal minScore, @Param("maxScore") java.math.BigDecimal maxScore);

    /**
     * Find learning sets created after specified date
     * @param userId user ID
     * @param date creation date
     * @return list of learning sets created after date
     */
    @Query("SELECT ls FROM LearningSet ls WHERE ls.user.id = :userId AND ls.createdAt > :date")
    List<LearningSet> findByUserIdAndCreatedAtAfter(@Param("userId") UUID userId, @Param("date") LocalDateTime date);

    /**
     * Find learning sets last reviewed after specified date
     * @param userId user ID
     * @param date last reviewed date
     * @return list of learning sets last reviewed after date
     */
    @Query("SELECT ls FROM LearningSet ls WHERE ls.user.id = :userId AND ls.lastReviewedAt > :date")
    List<LearningSet> findByUserIdAndLastReviewedAtAfter(@Param("userId") UUID userId, @Param("date") LocalDateTime date);

    /**
     * Find learning sets that need review (not reviewed recently)
     * @param userId user ID
     * @param date threshold date
     * @return list of learning sets needing review
     */
    @Query("SELECT ls FROM LearningSet ls WHERE ls.user.id = :userId AND (ls.lastReviewedAt IS NULL OR ls.lastReviewedAt < :date)")
    List<LearningSet> findLearningSetsNeedingReview(@Param("userId") UUID userId, @Param("date") LocalDateTime date);

    /**
     * Find learning sets with pending reminders
     * @param userId user ID
     * @param today today's date
     * @return list of learning sets with pending reminders
     */
    @Query("SELECT DISTINCT ls FROM LearningSet ls JOIN ls.reminderSchedules rs WHERE ls.user.id = :userId AND rs.scheduledDate = :today AND rs.status = 'PENDING'")
    List<LearningSet> findLearningSetsWithPendingReminders(@Param("userId") UUID userId, @Param("today") java.time.LocalDate today);

    /**
     * Count learning sets by user
     * @param userId user ID
     * @return count of learning sets for user
     */
    long countByUserId(UUID userId);

    /**
     * Count learning sets by user and status
     * @param userId user ID
     * @param status set status
     * @return count of learning sets for user with status
     */
    long countByUserIdAndStatus(UUID userId, SetStatus status);

    /**
     * Count learning sets by user and category
     * @param userId user ID
     * @param category set category
     * @return count of learning sets for user with category
     */
    long countByUserIdAndCategory(UUID userId, SetCategory category);

    /**
     * Find learning sets with highest average scores
     * @param userId user ID
     * @param pageable pagination information
     * @return page of learning sets ordered by average score descending
     */
    @Query("SELECT ls FROM LearningSet ls WHERE ls.user.id = :userId AND ls.averageScore IS NOT NULL ORDER BY ls.averageScore DESC")
    Page<LearningSet> findTopLearningSetsByAverageScore(@Param("userId") UUID userId, Pageable pageable);

    /**
     * Find learning sets with most reviews
     * @param userId user ID
     * @param pageable pagination information
     * @return page of learning sets ordered by total reviews descending
     */
    @Query("SELECT ls FROM LearningSet ls WHERE ls.user.id = :userId ORDER BY ls.totalReviews DESC")
    Page<LearningSet> findTopLearningSetsByTotalReviews(@Param("userId") UUID userId, Pageable pageable);

    /**
     * Find learning sets by user and description containing text
     * @param userId user ID
     * @param description description to search for
     * @return list of learning sets with matching descriptions
     */
    @Query("SELECT ls FROM LearningSet ls WHERE ls.user.id = :userId AND LOWER(ls.description) LIKE LOWER(CONCAT('%', :description, '%'))")
    List<LearningSet> findByUserIdAndDescriptionContainingIgnoreCase(@Param("userId") UUID userId, @Param("description") String description);

    /**
     * Find learning sets created between dates
     * @param userId user ID
     * @param startDate start date
     * @param endDate end date
     * @return list of learning sets created between dates
     */
    @Query("SELECT ls FROM LearningSet ls WHERE ls.user.id = :userId AND ls.createdAt BETWEEN :startDate AND :endDate")
    List<LearningSet> findByUserIdAndCreatedAtBetween(@Param("userId") UUID userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find learning sets with no items
     * @param userId user ID
     * @return list of learning sets with no items
     */
    @Query("SELECT ls FROM LearningSet ls WHERE ls.user.id = :userId AND ls.wordCount = 0")
    List<LearningSet> findEmptyLearningSets(@Param("userId") UUID userId);

    /**
     * Find learning sets with items
     * @param userId user ID
     * @return list of learning sets with items
     */
    @Query("SELECT ls FROM LearningSet ls WHERE ls.user.id = :userId AND ls.wordCount > 0")
    List<LearningSet> findNonEmptyLearningSets(@Param("userId") UUID userId);

    /**
     * Count learning sets by user and status in list
     * @param userId user ID
     * @param statuses list of set statuses
     * @return count of learning sets for user with statuses
     */
    long countByUserIdAndStatusIn(UUID userId, List<SetStatus> statuses);
}