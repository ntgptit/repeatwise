package com.repeatwise.service;

import com.repeatwise.dto.response.stats.BoxDistributionResponse;
import com.repeatwise.dto.response.stats.UserStatsResponse;

import java.util.UUID;

/**
 * Statistics Service interface
 *
 * Requirements:
 * - UC-031: View User Statistics
 * - UC-032: View Box Distribution
 *
 * @author RepeatWise Team
 */
public interface IStatsService {

    /**
     * Get user statistics
     * UC-031: View User Statistics
     *
     * @param userId Current user UUID
     * @return User statistics response
     */
    UserStatsResponse getUserStats(UUID userId);

    /**
     * Get box distribution statistics
     * UC-032: View Box Distribution
     *
     * @param scopeType Scope type (ALL, DECK, FOLDER)
     * @param scopeId Scope ID (null for ALL, deck ID or folder ID)
     * @param userId Current user UUID
     * @return Box distribution response
     */
    BoxDistributionResponse getBoxDistribution(String scopeType, UUID scopeId, UUID userId);
}

