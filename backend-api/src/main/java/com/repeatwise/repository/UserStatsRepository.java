package com.repeatwise.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.repeatwise.entity.User;
import com.repeatwise.entity.UserStats;

/**
 * Repository for UserStats entity with Spring Data JPA.
 * Provides database access methods for user statistics tracking.
 */
@Repository
public interface UserStatsRepository extends JpaRepository<UserStats, UUID> {

    /**
     * Find user statistics by user.
     * Each user has exactly one user stats record (one-to-one relationship).
     *
     * @param user User entity
     * @return Optional containing user stats if found
     */
    Optional<UserStats> findByUser(User user);

    /**
     * Check if user stats exist for a user.
     *
     * @param user User entity
     * @return true if stats exist
     */
    boolean existsByUser(User user);

    /**
     * Delete user stats for a user.
     *
     * @param user User entity
     */
    void deleteByUser(User user);
}
