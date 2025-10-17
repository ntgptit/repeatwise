package com.repeatwise.repository;

import com.repeatwise.entity.SrsSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * SRS Settings Repository
 *
 * Requirements:
 * - UC-022: Configure SRS Settings
 * - Database access for srs_settings table
 *
 * @author RepeatWise Team
 */
@Repository
public interface SrsSettingsRepository extends JpaRepository<SrsSettings, UUID> {

    /**
     * Find SRS settings by user ID
     *
     * @param userId User ID
     * @return Optional of SrsSettings
     */
    Optional<SrsSettings> findByUserId(UUID userId);

    /**
     * Check if SRS settings exist for user
     *
     * @param userId User ID
     * @return true if exists
     */
    boolean existsByUserId(UUID userId);
}
