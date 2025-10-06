package com.spacedlearning.repository;

import com.spacedlearning.entity.LearningCycle;
import com.spacedlearning.entity.LearningSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for LearningCycle entity
 */
@Repository
public interface LearningCycleRepository extends JpaRepository<LearningCycle, UUID> {

    /**
     * Find learning cycles by learning set
     * 
     * @param learningSet Learning set
     * @return List of learning cycles
     */
    List<LearningCycle> findByLearningSet(LearningSet learningSet);

    /**
     * Find learning cycle by learning set and cycle number
     * 
     * @param learningSet Learning set
     * @param cycleNumber Cycle number
     * @return Optional learning cycle
     */
    Optional<LearningCycle> findByLearningSetAndCycleNumber(LearningSet learningSet, Integer cycleNumber);

    /**
     * Find current active learning cycle for a set
     * 
     * @param learningSet Learning set
     * @return Optional active learning cycle
     */
    @Query("SELECT lc FROM LearningCycle lc WHERE lc.learningSet = :learningSet AND lc.status = 'ACTIVE' ORDER BY lc.cycleNumber DESC")
    Optional<LearningCycle> findActiveByLearningSet(@Param("learningSet") LearningSet learningSet);

    /**
     * Find latest learning cycle for a set
     * 
     * @param learningSet Learning set
     * @return Optional latest learning cycle
     */
    @Query("SELECT lc FROM LearningCycle lc WHERE lc.learningSet = :learningSet ORDER BY lc.cycleNumber DESC")
    Optional<LearningCycle> findLatestByLearningSet(@Param("learningSet") LearningSet learningSet);

    /**
     * Count learning cycles by learning set
     * 
     * @param learningSet Learning set
     * @return Number of learning cycles
     */
    long countByLearningSet(LearningSet learningSet);

    /**
     * Find learning cycles by user ID
     * 
     * @param userId User ID
     * @return List of learning cycles
     */
    @Query("SELECT lc FROM LearningCycle lc WHERE lc.learningSet.user.id = :userId")
    List<LearningCycle> findByUserId(@Param("userId") UUID userId);

    /**
     * Find active learning cycles by user ID
     * 
     * @param userId User ID
     * @return List of active learning cycles
     */
    @Query("SELECT lc FROM LearningCycle lc WHERE lc.learningSet.user.id = :userId AND lc.status = 'ACTIVE'")
    List<LearningCycle> findActiveByUserId(@Param("userId") UUID userId);
}
