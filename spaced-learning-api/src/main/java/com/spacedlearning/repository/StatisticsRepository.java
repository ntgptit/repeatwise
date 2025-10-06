package com.spacedlearning.repository;

import java.math.BigDecimal;
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

import com.spacedlearning.entity.Statistics;
import com.spacedlearning.entity.enums.StatType;

/**
 * Repository interface for Statistics entity
 * Provides data access methods for statistics management
 */
@Repository
public interface StatisticsRepository extends JpaRepository<Statistics, UUID> {

    /**
     * Find statistics by user
     * @param userId user ID
     * @return list of statistics for user
     */
    List<Statistics> findByUserId(UUID userId);

    /**
     * Find statistics by user with pagination
     * @param userId user ID
     * @param pageable pagination information
     * @return page of statistics for user
     */
    Page<Statistics> findByUserId(UUID userId, Pageable pageable);

    /**
     * Find statistics by learning set
     * @param setId learning set ID
     * @return list of statistics for learning set
     */
    List<Statistics> findByLearningSetId(UUID setId);

    /**
     * Find statistics by user and learning set
     * @param userId user ID
     * @param setId learning set ID
     * @return list of statistics for user and learning set
     */
    List<Statistics> findByUserIdAndLearningSetId(UUID userId, UUID setId);

    /**
     * Find statistics by stat type
     * @param statType statistic type
     * @return list of statistics with type
     */
    List<Statistics> findByStatType(StatType statType);

    /**
     * Find statistics by user and stat type
     * @param userId user ID
     * @param statType statistic type
     * @return list of statistics for user with type
     */
    List<Statistics> findByUserIdAndStatType(UUID userId, StatType statType);

    /**
     * Find statistics by learning set and stat type
     * @param setId learning set ID
     * @param statType statistic type
     * @return list of statistics for learning set with type
     */
    List<Statistics> findByLearningSetIdAndStatType(UUID setId, StatType statType);

    /**
     * Find statistics by stat date
     * @param statDate statistic date
     * @return list of statistics on date
     */
    List<Statistics> findByStatDate(LocalDate statDate);

    /**
     * Find statistics by user and stat date
     * @param userId user ID
     * @param statDate statistic date
     * @return list of statistics for user on date
     */
    List<Statistics> findByUserIdAndStatDate(UUID userId, LocalDate statDate);

    /**
     * Find statistics by stat date range
     * @param startDate start date
     * @param endDate end date
     * @return list of statistics between dates
     */
    List<Statistics> findByStatDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find statistics by user and stat date range
     * @param userId user ID
     * @param startDate start date
     * @param endDate end date
     * @return list of statistics for user between dates
     */
    List<Statistics> findByUserIdAndStatDateBetween(UUID userId, LocalDate startDate, LocalDate endDate);

    /**
     * Find statistics by learning set and stat date range
     * @param setId learning set ID
     * @param startDate start date
     * @param endDate end date
     * @return list of statistics for learning set between dates
     */
    List<Statistics> findByLearningSetIdAndStatDateBetween(UUID setId, LocalDate startDate, LocalDate endDate);

    /**
     * Find statistics by stat value range
     * @param minValue minimum stat value
     * @param maxValue maximum stat value
     * @return list of statistics with values in range
     */
    List<Statistics> findByStatValueBetween(BigDecimal minValue, BigDecimal maxValue);

    /**
     * Find statistics by user and stat value range
     * @param userId user ID
     * @param minValue minimum stat value
     * @param maxValue maximum stat value
     * @return list of statistics for user with values in range
     */
    List<Statistics> findByUserIdAndStatValueBetween(UUID userId, BigDecimal minValue, BigDecimal maxValue);

    /**
     * Find statistics by learning set and stat value range
     * @param setId learning set ID
     * @param minValue minimum stat value
     * @param maxValue maximum stat value
     * @return list of statistics for learning set with values in range
     */
    List<Statistics> findByLearningSetIdAndStatValueBetween(UUID setId, BigDecimal minValue, BigDecimal maxValue);

    /**
     * Find user-level statistics (no specific learning set)
     * @param userId user ID
     * @return list of user-level statistics
     */
    @Query("SELECT s FROM Statistics s WHERE s.user.id = :userId AND s.learningSet IS NULL")
    List<Statistics> findUserLevelStatistics(@Param("userId") UUID userId);

    /**
     * Find user-level statistics by stat type
     * @param userId user ID
     * @param statType statistic type
     * @return list of user-level statistics with type
     */
    @Query("SELECT s FROM Statistics s WHERE s.user.id = :userId AND s.learningSet IS NULL AND s.statType = :statType")
    List<Statistics> findUserLevelStatisticsByType(@Param("userId") UUID userId, @Param("statType") StatType statType);

    /**
     * Find set-level statistics (with specific learning set)
     * @param userId user ID
     * @return list of set-level statistics
     */
    @Query("SELECT s FROM Statistics s WHERE s.user.id = :userId AND s.learningSet IS NOT NULL")
    List<Statistics> findSetLevelStatistics(@Param("userId") UUID userId);

    /**
     * Find set-level statistics by stat type
     * @param userId user ID
     * @param statType statistic type
     * @return list of set-level statistics with type
     */
    @Query("SELECT s FROM Statistics s WHERE s.user.id = :userId AND s.learningSet IS NOT NULL AND s.statType = :statType")
    List<Statistics> findSetLevelStatisticsByType(@Param("userId") UUID userId, @Param("statType") StatType statType);

    /**
     * Find daily review statistics
     * @return list of daily review statistics
     */
    @Query("SELECT s FROM Statistics s WHERE s.statType = 'DAILY_REVIEWS'")
    List<Statistics> findDailyReviewStatistics();

    /**
     * Find daily review statistics by user
     * @param userId user ID
     * @return list of daily review statistics for user
     */
    @Query("SELECT s FROM Statistics s WHERE s.user.id = :userId AND s.statType = 'DAILY_REVIEWS'")
    List<Statistics> findDailyReviewStatisticsByUser(@Param("userId") UUID userId);

    /**
     * Find weekly review statistics
     * @return list of weekly review statistics
     */
    @Query("SELECT s FROM Statistics s WHERE s.statType = 'WEEKLY_REVIEWS'")
    List<Statistics> findWeeklyReviewStatistics();

    /**
     * Find weekly review statistics by user
     * @param userId user ID
     * @return list of weekly review statistics for user
     */
    @Query("SELECT s FROM Statistics s WHERE s.user.id = :userId AND s.statType = 'WEEKLY_REVIEWS'")
    List<Statistics> findWeeklyReviewStatisticsByUser(@Param("userId") UUID userId);

    /**
     * Find monthly review statistics
     * @return list of monthly review statistics
     */
    @Query("SELECT s FROM Statistics s WHERE s.statType = 'MONTHLY_REVIEWS'")
    List<Statistics> findMonthlyReviewStatistics();

    /**
     * Find monthly review statistics by user
     * @param userId user ID
     * @return list of monthly review statistics for user
     */
    @Query("SELECT s FROM Statistics s WHERE s.user.id = :userId AND s.statType = 'MONTHLY_REVIEWS'")
    List<Statistics> findMonthlyReviewStatisticsByUser(@Param("userId") UUID userId);

    /**
     * Find average score statistics
     * @return list of average score statistics
     */
    @Query("SELECT s FROM Statistics s WHERE s.statType = 'AVERAGE_SCORE'")
    List<Statistics> findAverageScoreStatistics();

    /**
     * Find average score statistics by user
     * @param userId user ID
     * @return list of average score statistics for user
     */
    @Query("SELECT s FROM Statistics s WHERE s.user.id = :userId AND s.statType = 'AVERAGE_SCORE'")
    List<Statistics> findAverageScoreStatisticsByUser(@Param("userId") UUID userId);

    /**
     * Find learning streak statistics
     * @return list of learning streak statistics
     */
    @Query("SELECT s FROM Statistics s WHERE s.statType = 'LEARNING_STREAK'")
    List<Statistics> findLearningStreakStatistics();

    /**
     * Find learning streak statistics by user
     * @param userId user ID
     * @return list of learning streak statistics for user
     */
    @Query("SELECT s FROM Statistics s WHERE s.user.id = :userId AND s.statType = 'LEARNING_STREAK'")
    List<Statistics> findLearningStreakStatisticsByUser(@Param("userId") UUID userId);

    /**
     * Find review accuracy statistics
     * @return list of review accuracy statistics
     */
    @Query("SELECT s FROM Statistics s WHERE s.statType = 'REVIEW_ACCURACY'")
    List<Statistics> findReviewAccuracyStatistics();

    /**
     * Find review accuracy statistics by user
     * @param userId user ID
     * @return list of review accuracy statistics for user
     */
    @Query("SELECT s FROM Statistics s WHERE s.user.id = :userId AND s.statType = 'REVIEW_ACCURACY'")
    List<Statistics> findReviewAccuracyStatisticsByUser(@Param("userId") UUID userId);

    /**
     * Find time spent statistics
     * @return list of time spent statistics
     */
    @Query("SELECT s FROM Statistics s WHERE s.statType = 'TIME_SPENT'")
    List<Statistics> findTimeSpentStatistics();

    /**
     * Find time spent statistics by user
     * @param userId user ID
     * @return list of time spent statistics for user
     */
    @Query("SELECT s FROM Statistics s WHERE s.user.id = :userId AND s.statType = 'TIME_SPENT'")
    List<Statistics> findTimeSpentStatisticsByUser(@Param("userId") UUID userId);

    /**
     * Find statistics by user, learning set, stat type, and date
     * @param userId user ID
     * @param setId learning set ID
     * @param statType statistic type
     * @param statDate statistic date
     * @return Optional containing statistics if found
     */
    Optional<Statistics> findByUserIdAndLearningSetIdAndStatTypeAndStatDate(
            UUID userId, UUID setId, StatType statType, LocalDate statDate);

    /**
     * Find user-level statistics by user, stat type, and date
     * @param userId user ID
     * @param statType statistic type
     * @param statDate statistic date
     * @return Optional containing statistics if found
     */
    @Query("SELECT s FROM Statistics s WHERE s.user.id = :userId AND s.learningSet IS NULL AND s.statType = :statType AND s.statDate = :statDate")
    Optional<Statistics> findUserLevelStatisticsByTypeAndDate(
            @Param("userId") UUID userId, @Param("statType") StatType statType, @Param("statDate") LocalDate statDate);

    /**
     * Count statistics by user
     * @param userId user ID
     * @return count of statistics for user
     */
    long countByUserId(UUID userId);

    /**
     * Count statistics by user and stat type
     * @param userId user ID
     * @param statType statistic type
     * @return count of statistics for user with type
     */
    long countByUserIdAndStatType(UUID userId, StatType statType);

    /**
     * Count statistics by learning set
     * @param setId learning set ID
     * @return count of statistics for learning set
     */
    long countByLearningSetId(UUID setId);

    /**
     * Count statistics by learning set and stat type
     * @param setId learning set ID
     * @param statType statistic type
     * @return count of statistics for learning set with type
     */
    long countByLearningSetIdAndStatType(UUID setId, StatType statType);

    /**
     * Calculate average stat value for user and stat type
     * @param userId user ID
     * @param statType statistic type
     * @return average stat value for user and type
     */
    @Query("SELECT AVG(s.statValue) FROM Statistics s WHERE s.user.id = :userId AND s.statType = :statType")
    Double calculateAverageStatValueByUserAndType(@Param("userId") UUID userId, @Param("statType") StatType statType);

    /**
     * Calculate average stat value for learning set and stat type
     * @param setId learning set ID
     * @param statType statistic type
     * @return average stat value for learning set and type
     */
    @Query("SELECT AVG(s.statValue) FROM Statistics s WHERE s.learningSet.id = :setId AND s.statType = :statType")
    Double calculateAverageStatValueByLearningSetAndType(@Param("setId") UUID setId, @Param("statType") StatType statType);

    /**
     * Calculate sum of stat values for user and stat type
     * @param userId user ID
     * @param statType statistic type
     * @return sum of stat values for user and type
     */
    @Query("SELECT SUM(s.statValue) FROM Statistics s WHERE s.user.id = :userId AND s.statType = :statType")
    BigDecimal calculateSumStatValueByUserAndType(@Param("userId") UUID userId, @Param("statType") StatType statType);

    /**
     * Calculate sum of stat values for learning set and stat type
     * @param setId learning set ID
     * @param statType statistic type
     * @return sum of stat values for learning set and type
     */
    @Query("SELECT SUM(s.statValue) FROM Statistics s WHERE s.learningSet.id = :setId AND s.statType = :statType")
    BigDecimal calculateSumStatValueByLearningSetAndType(@Param("setId") UUID setId, @Param("statType") StatType statType);

    /**
     * Find statistics created after specified date
     * @param date creation date
     * @return list of statistics created after date
     */
    @Query("SELECT s FROM Statistics s WHERE s.createdAt > :date")
    List<Statistics> findByCreatedAtAfter(@Param("date") java.time.LocalDateTime date);

    /**
     * Find statistics created between dates
     * @param startDate start date
     * @param endDate end date
     * @return list of statistics created between dates
     */
    @Query("SELECT s FROM Statistics s WHERE s.createdAt BETWEEN :startDate AND :endDate")
    List<Statistics> findByCreatedAtBetween(@Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    /**
     * Find statistics by user created between dates
     * @param userId user ID
     * @param startDate start date
     * @param endDate end date
     * @return list of statistics for user created between dates
     */
    @Query("SELECT s FROM Statistics s WHERE s.user.id = :userId AND s.createdAt BETWEEN :startDate AND :endDate")
    List<Statistics> findByUserIdAndCreatedAtBetween(@Param("userId") UUID userId, @Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    /**
     * Find statistics by metadata containing text
     * @param metadata metadata to search for
     * @return list of statistics with matching metadata
     */
    @Query("SELECT s FROM Statistics s WHERE LOWER(s.metadata) LIKE LOWER(CONCAT('%', :metadata, '%'))")
    List<Statistics> findByMetadataContainingIgnoreCase(@Param("metadata") String metadata);

    /**
     * Find statistics by user and metadata containing text
     * @param userId user ID
     * @param metadata metadata to search for
     * @return list of statistics for user with matching metadata
     */
    @Query("SELECT s FROM Statistics s WHERE s.user.id = :userId AND LOWER(s.metadata) LIKE LOWER(CONCAT('%', :metadata, '%'))")
    List<Statistics> findByUserIdAndMetadataContainingIgnoreCase(@Param("userId") UUID userId, @Param("metadata") String metadata);
}
