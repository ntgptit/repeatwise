package com.spacedlearning.repository;

import com.spacedlearning.entity.SRSConfiguration;
import com.spacedlearning.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for SRSConfiguration entity
 */
@Repository
public interface SRSConfigurationRepository extends JpaRepository<SRSConfiguration, UUID> {

    /**
     * Find SRS configuration by user ID
     * 
     * @param userId User ID
     * @return Optional SRS configuration
     */
    Optional<SRSConfiguration> findByUserId(UUID userId);

    /**
     * Find active SRS configuration by user ID
     * 
     * @param userId User ID
     * @return Optional active SRS configuration
     */
    @Query("SELECT s FROM SRSConfiguration s WHERE s.user.id = :userId AND s.isActive = true")
    Optional<SRSConfiguration> findActiveByUserId(@Param("userId") UUID userId);

    /**
     * Check if user has SRS configuration
     * 
     * @param userId User ID
     * @return true if exists, false otherwise
     */
    boolean existsByUserId(UUID userId);

    /**
     * Delete SRS configuration by user ID
     * 
     * @param userId User ID
     */
    void deleteByUserId(UUID userId);
}