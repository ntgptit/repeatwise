package com.repeatwise.repository;

import com.repeatwise.enums.SetStatus;
import com.repeatwise.model.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SetRepository extends JpaRepository<Set, UUID> {

    /**
     * Find all sets by user ID
     */
    List<Set> findByUserId(UUID userId);

    /**
     * Find sets by user ID and status
     */
    List<Set> findByUserIdAndStatus(UUID userId, SetStatus status);

    /**
     * Find set by ID and user ID
     */
    Optional<Set> findByIdAndUserId(UUID id, UUID userId);

    /**
     * Find sets that need to be reviewed today
     */
    @Query("SELECT s FROM Set s WHERE s.nextCycleStartDate = :today AND s.deletedAt IS NULL")
    List<Set> findSetsToReviewToday(@Param("today") LocalDate today);

    /**
     * Find sets that are overdue for review
     */
    @Query("SELECT s FROM Set s WHERE s.nextCycleStartDate < :today AND s.deletedAt IS NULL")
    List<Set> findOverdueSets(@Param("today") LocalDate today);

    /**
     * Find sets that need review on a specific date
     */
    @Query("SELECT s FROM Set s WHERE s.nextCycleStartDate = :date AND s.deletedAt IS NULL")
    List<Set> findSetsToReviewOnDate(@Param("date") LocalDate date);

    /**
     * Find sets by user ID with pagination
     */
    @Query("SELECT s FROM Set s WHERE s.user.id = :userId AND s.deletedAt IS NULL ORDER BY s.createdAt DESC")
    List<Set> findSetsByUserIdOrderByCreatedAtDesc(@Param("userId") UUID userId);

    /**
     * Count sets by user ID
     */
    long countByUserId(UUID userId);

    /**
     * Count sets by user ID and status
     */
    long countByUserIdAndStatus(UUID userId, SetStatus status);

    /**
     * Find sets that are currently in learning or reviewing status
     */
    @Query("SELECT s FROM Set s WHERE s.user.id = :userId AND s.status IN ('LEARNING', 'REVIEWING') AND s.deletedAt IS NULL")
    List<Set> findActiveSetsByUserId(@Param("userId") UUID userId);

    /**
     * Find sets that are mastered
     */
    @Query("SELECT s FROM Set s WHERE s.user.id = :userId AND s.status = 'MASTERED' AND s.deletedAt IS NULL")
    List<Set> findMasteredSetsByUserId(@Param("userId") UUID userId);

    /**
     * Find sets with highest word count for a user (for scheduling prioritization)
     */
    @Query("SELECT s FROM Set s WHERE s.user.id = :userId AND s.deletedAt IS NULL ORDER BY s.wordCount DESC")
    List<Set> findSetsByUserIdOrderByWordCountDesc(@Param("userId") UUID userId);

    /**
     * Find sets that haven't started yet
     */
    @Query("SELECT s FROM Set s WHERE s.user.id = :userId AND s.status = 'NOT_STARTED' AND s.deletedAt IS NULL")
    List<Set> findNotStartedSetsByUserId(@Param("userId") UUID userId);
} 
