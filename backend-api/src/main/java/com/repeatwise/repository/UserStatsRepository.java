package com.repeatwise.repository;

import com.repeatwise.entity.UserStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * User Stats Repository
 *
 * Requirements:
 * - UC-023: View Statistics
 * - Database access for user_stats table
 *
 * @author RepeatWise Team
 */
@Repository
public interface UserStatsRepository extends JpaRepository<UserStats, UUID> {

    /**
     * Find user stats by user ID
     *
     * @param userId User ID
     * @return Optional of UserStats
     */
    Optional<UserStats> findByUserId(UUID userId);

    /**
     * Check if user stats exist for user
     *
     * @param userId User ID
     * @return true if exists
     */
    boolean existsByUserId(UUID userId);
}
