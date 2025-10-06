package com.spacedlearning.service;

import com.spacedlearning.dto.learning.PerformReviewRequest;
import com.spacedlearning.dto.learning.PerformReviewResponse;
import com.spacedlearning.dto.learning.StartLearningCycleRequest;
import com.spacedlearning.dto.learning.StartLearningCycleResponse;

import java.util.UUID;

/**
 * Service interface for Learning Cycle operations
 * Based on UC-010: Start Learning Cycle and UC-011: Perform Review Session
 */
public interface LearningCycleService {

    /**
     * Start a new learning cycle for a set
     * Based on UC-010: Start Learning Cycle
     * 
     * @param request Start learning cycle request
     * @param userId Current user ID
     * @return Start learning cycle response
     */
    StartLearningCycleResponse startLearningCycle(StartLearningCycleRequest request, UUID userId);

    /**
     * Perform a review session for a set
     * Based on UC-011: Perform Review Session
     * 
     * @param request Perform review request
     * @param userId Current user ID
     * @return Perform review response
     */
    PerformReviewResponse performReview(PerformReviewRequest request, UUID userId);

    /**
     * Check if user can start a new learning cycle for a set
     * 
     * @param setId Set ID
     * @param userId User ID
     * @return true if can start, false otherwise
     */
    boolean canStartLearningCycle(UUID setId, UUID userId);

    /**
     * Check if user can perform review for a set
     * 
     * @param setId Set ID
     * @param userId User ID
     * @return true if can review, false otherwise
     */
    boolean canPerformReview(UUID setId, UUID userId);

    /**
     * Get current learning cycle information for a set
     * 
     * @param setId Set ID
     * @param userId User ID
     * @return Current cycle information
     */
    StartLearningCycleResponse getCurrentCycleInfo(UUID setId, UUID userId);
}
