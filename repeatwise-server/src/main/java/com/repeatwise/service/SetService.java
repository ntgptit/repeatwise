package com.repeatwise.service;

import com.repeatwise.dto.SetDto;
import com.repeatwise.enums.SetStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SetService {

    /**
     * Create a new set
     */
    SetDto createSet(UUID userId, SetDto setDto);

    /**
     * Find set by ID
     */
    Optional<SetDto> findById(UUID id);

    /**
     * Find set by ID and user ID
     */
    Optional<SetDto> findByIdAndUserId(UUID id, UUID userId);

    /**
     * Update set
     */
    SetDto updateSet(UUID id, UUID userId, SetDto setDto);

    /**
     * Delete set (soft delete)
     */
    void deleteSet(UUID id, UUID userId);

    /**
     * Find all sets by user ID
     */
    List<SetDto> findByUserId(UUID userId);

    /**
     * Find sets by user ID and status
     */
    List<SetDto> findByUserIdAndStatus(UUID userId, SetStatus status);

    /**
     * Find sets that need to be reviewed today
     */
    List<SetDto> findSetsToReviewToday(UUID userId);

    /**
     * Find overdue sets for a user
     */
    List<SetDto> findOverdueSets(UUID userId);

    /**
     * Find active sets (LEARNING or REVIEWING status)
     */
    List<SetDto> findActiveSets(UUID userId);

    /**
     * Find mastered sets
     */
    List<SetDto> findMasteredSets(UUID userId);

    /**
     * Find sets that haven't started yet
     */
    List<SetDto> findNotStartedSets(UUID userId);

    /**
     * Start learning a set (change status to LEARNING)
     */
    SetDto startLearning(UUID setId, UUID userId);

    /**
     * Mark set as mastered
     */
    SetDto markAsMastered(UUID setId, UUID userId);

    /**
     * Get sets for daily review (respecting 3-set limit)
     */
    List<SetDto> getDailyReviewSets(UUID userId, LocalDate date);

    /**
     * Schedule next cycle for a set
     */
    SetDto scheduleNextCycle(UUID setId, UUID userId, LocalDate nextCycleDate);

    /**
     * Get set statistics
     */
    SetStatistics getSetStatistics(UUID setId, UUID userId);

    /**
     * Set statistics data class
     */
    record SetStatistics(
        long totalCycles,
        long completedCycles,
        double averageScore,
        LocalDate lastReviewDate,
        LocalDate nextReviewDate,
        int currentCycle,
        SetStatus status
    ) {}
} 
