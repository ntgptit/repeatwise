package com.repeatwise.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.repeatwise.entity.ReviewLog;

/**
 * Repository interface for ReviewLog entity
 *
 * Requirements:
 * - UC-024: Rate Card
 * - UC-025: Undo Review
 * - UC-031: View User Statistics
 * - Provides CRUD operations and custom queries for review logs
 *
 * Query Methods:
 * - Find review logs by user
 * - Count reviews today
 * - Find last review log
 * - Get reviews for past 7 days
 *
 * @author RepeatWise Team
 */
@Repository
public interface ReviewLogRepository extends JpaRepository<ReviewLog, UUID> {

    /**
     * Count reviews today for a user
     * UC-023: Check daily limit
     *
     * @param userId     User UUID
     * @param startOfDay Start of today (00:00:00)
     * @param endOfDay   End of today (23:59:59)
     * @return Count of reviews today
     */
    @Query("""
            SELECT COUNT(rl) FROM ReviewLog rl \
            WHERE rl.user.id = :userId \
            AND rl.reviewedAt >= :startOfDay \
            AND rl.reviewedAt < :endOfDay""")
    long countReviewsToday(
            @Param("userId") UUID userId,
            @Param("startOfDay") Instant startOfDay,
            @Param("endOfDay") Instant endOfDay);

    /**
     * Find last review log for user (for undo)
     * UC-025: Undo Review
     *
     * @param userId User UUID
     * @return Optional of last ReviewLog
     */
    @Query("""
            SELECT rl FROM ReviewLog rl \
            WHERE rl.user.id = :userId \
            ORDER BY rl.reviewedAt DESC""")
    List<ReviewLog> findLastReviewLogsByUserId(@Param("userId") UUID userId);

    /**
     * Find review logs by card and user
     *
     * @param cardId Card UUID
     * @param userId User UUID
     * @return List of review logs
     */
    @Query("""
            SELECT rl FROM ReviewLog rl \
            WHERE rl.card.id = :cardId \
            AND rl.user.id = :userId \
            ORDER BY rl.reviewedAt DESC""")
    List<ReviewLog> findByCardIdAndUserId(
            @Param("cardId") UUID cardId,
            @Param("userId") UUID userId);

    /**
     * Get review counts for past 7 days
     * UC-031: View User Statistics
     *
     * @param userId    User UUID
     * @param startDate Start date (7 days ago)
     * @return List of objects with date and count
     */
    @Query(value = """
            SELECT DATE(rl.reviewed_at) as review_date, COUNT(*) as count \
            FROM review_logs rl \
            WHERE rl.user_id = :userId \
            AND rl.reviewed_at >= :startDate \
            GROUP BY DATE(rl.reviewed_at) \
            ORDER BY review_date ASC""", nativeQuery = true)
    List<Object[]> findReviewCountsPast7Days(
            @Param("userId") UUID userId,
            @Param("startDate") Instant startDate);
}
