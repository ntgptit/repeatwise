package com.repeatwise.service;

import com.repeatwise.dto.SetCycleDto;
import com.repeatwise.enums.CycleStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SetCycleService {

    /**
     * Start a new cycle for a set
     */
    SetCycleDto startCycle(UUID setId, UUID userId);

    /**
     * Finish a cycle (when 5 reviews are completed)
     */
    SetCycleDto finishCycle(UUID cycleId, UUID userId);

    /**
     * Find cycle by ID
     */
    Optional<SetCycleDto> findById(UUID id);

    /**
     * Find cycle by ID and set ID
     */
    Optional<SetCycleDto> findByIdAndSetId(UUID id, UUID setId);

    /**
     * Find active cycle for a set
     */
    Optional<SetCycleDto> findActiveCycleBySetId(UUID setId);

    /**
     * Find all cycles for a set
     */
    List<SetCycleDto> findBySetId(UUID setId);

    /**
     * Find cycles by set ID and status
     */
    List<SetCycleDto> findBySetIdAndStatus(UUID setId, CycleStatus status);

    /**
     * Find cycles that are ready to finish (have 5 reviews)
     */
    List<SetCycleDto> findCyclesReadyToFinish();

    /**
     * Calculate average score for a cycle
     */
    BigDecimal calculateAverageScore(UUID cycleId);

    /**
     * Calculate next cycle delay using SRS algorithm
     */
    int calculateNextCycleDelay(UUID cycleId);

    /**
     * Get cycle statistics
     */
    CycleStatistics getCycleStatistics(UUID cycleId);

    /**
     * Find cycles by user ID
     */
    List<SetCycleDto> findCyclesByUserId(UUID userId);

    /**
     * Find cycles by user ID and status
     */
    List<SetCycleDto> findCyclesByUserIdAndStatus(UUID userId, CycleStatus status);

    /**
     * Get the next cycle number for a set
     */
    Integer getNextCycleNumber(UUID setId);

    /**
     * Update cycle average score
     */
    SetCycleDto updateCycleAverageScore(UUID cycleId, BigDecimal avgScore);

    /**
     * Cycle statistics data class
     */
    record CycleStatistics(
        int cycleNo,
        CycleStatus status,
        LocalDate startedAt,
        LocalDate finishedAt,
        BigDecimal avgScore,
        int reviewCount,
        int nextCycleDelayDays
    ) {}

    /**
     * SRS Algorithm Configuration
     */
    record SRSConfig(
        int baseDelay,
        double penalty,
        double scaling,
        int minDelay,
        int maxDelay
    ) {
        public static final SRSConfig DEFAULT = new SRSConfig(30, 0.2, 0.02, 7, 90);
    }
} 
