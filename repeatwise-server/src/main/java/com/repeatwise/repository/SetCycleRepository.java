package com.repeatwise.repository;

import com.repeatwise.enums.CycleStatus;
import com.repeatwise.model.SetCycle;
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
public interface SetCycleRepository extends JpaRepository<SetCycle, UUID> {

    /**
     * Find all cycles by set ID
     */
    List<SetCycle> findBySetId(UUID setId);

    /**
     * Find cycles by set ID and status
     */
    List<SetCycle> findBySetIdAndStatus(UUID setId, CycleStatus status);

    /**
     * Find cycle by ID and set ID
     */
    Optional<SetCycle> findByIdAndSetId(UUID id, UUID setId);

    /**
     * Find the current active cycle for a set
     */
    @Query("SELECT sc FROM SetCycle sc WHERE sc.set.id = :setId AND sc.status = 'ACTIVE' AND sc.deletedAt IS NULL")
    Optional<SetCycle> findActiveCycleBySetId(@Param("setId") UUID setId);

    /**
     * Find the latest finished cycle for a set
     */
    @Query("SELECT sc FROM SetCycle sc WHERE sc.set.id = :setId AND sc.status = 'FINISHED' AND sc.deletedAt IS NULL ORDER BY sc.cycleNo DESC")
    List<SetCycle> findLatestFinishedCycleBySetId(@Param("setId") UUID setId);

    /**
     * Find cycles by cycle number and set ID
     */
    Optional<SetCycle> findByCycleNoAndSetId(Integer cycleNo, UUID setId);

    /**
     * Find the next cycle number for a set
     */
    @Query("SELECT COALESCE(MAX(sc.cycleNo), 0) + 1 FROM SetCycle sc WHERE sc.set.id = :setId AND sc.deletedAt IS NULL")
    Integer findNextCycleNumber(@Param("setId") UUID setId);

    /**
     * Find cycles that started on a specific date
     */
    @Query("SELECT sc FROM SetCycle sc WHERE sc.startedAt = :date AND sc.deletedAt IS NULL")
    List<SetCycle> findCyclesStartedOnDate(@Param("date") LocalDate date);

    /**
     * Find cycles that finished on a specific date
     */
    @Query("SELECT sc FROM SetCycle sc WHERE sc.finishedAt = :date AND sc.deletedAt IS NULL")
    List<SetCycle> findCyclesFinishedOnDate(@Param("date") LocalDate date);

    /**
     * Find cycles with average score above a threshold
     */
    @Query("SELECT sc FROM SetCycle sc WHERE sc.avgScore >= :minScore AND sc.deletedAt IS NULL")
    List<SetCycle> findCyclesWithMinAverageScore(@Param("minScore") BigDecimal minScore);

    /**
     * Find cycles with average score below a threshold
     */
    @Query("SELECT sc FROM SetCycle sc WHERE sc.avgScore < :maxScore AND sc.deletedAt IS NULL")
    List<SetCycle> findCyclesWithMaxAverageScore(@Param("maxScore") BigDecimal maxScore);

    /**
     * Count cycles by set ID
     */
    long countBySetId(UUID setId);

    /**
     * Count finished cycles by set ID
     */
    long countBySetIdAndStatus(UUID setId, CycleStatus status);

    /**
     * Find all active cycles across all sets
     */
    @Query("SELECT sc FROM SetCycle sc WHERE sc.status = 'ACTIVE' AND sc.deletedAt IS NULL")
    List<SetCycle> findAllActiveCycles();

    /**
     * Find cycles by user ID
     */
    @Query("SELECT sc FROM SetCycle sc WHERE sc.set.user.id = :userId AND sc.deletedAt IS NULL")
    List<SetCycle> findCyclesByUserId(@Param("userId") UUID userId);

    /**
     * Find cycles by user ID and status
     */
    @Query("SELECT sc FROM SetCycle sc WHERE sc.set.user.id = :userId AND sc.status = :status AND sc.deletedAt IS NULL")
    List<SetCycle> findCyclesByUserIdAndStatus(@Param("userId") UUID userId, @Param("status") CycleStatus status);

    /**
     * Find the average score of all cycles for a set
     */
    @Query("SELECT AVG(sc.avgScore) FROM SetCycle sc WHERE sc.set.id = :setId AND sc.status = 'FINISHED' AND sc.deletedAt IS NULL")
    BigDecimal findAverageScoreBySetId(@Param("setId") UUID setId);

    /**
     * Find cycles that need to be finished (have 5 reviews but still active)
     */
    @Query("SELECT sc FROM SetCycle sc WHERE sc.status = 'ACTIVE' AND sc.deletedAt IS NULL AND " +
           "(SELECT COUNT(sr) FROM SetReview sr WHERE sr.setCycle.id = sc.id AND sr.deletedAt IS NULL) >= 5")
    List<SetCycle> findCyclesReadyToFinish();

    /**
     * Find average score for a specific cycle by calculating from its reviews
     */
    @Query("SELECT AVG(sr.score) FROM SetReview sr WHERE sr.setCycle.id = :cycleId AND sr.deletedAt IS NULL")
    BigDecimal findAverageScoreBySetCycleId(@Param("cycleId") UUID cycleId);
} 
