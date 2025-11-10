package com.repeatwise.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.repeatwise.entity.SrsSettings;
import com.repeatwise.entity.User;

/**
 * Repository for SrsSettings entity with Spring Data JPA.
 * Provides database access methods for SRS (Spaced Repetition System) settings.
 */
@Repository
public interface SrsSettingsRepository extends JpaRepository<SrsSettings, UUID> {

    /**
     * Find SRS settings by user.
     * Each user has exactly one SRS settings record (one-to-one relationship).
     *
     * @param user User entity
     * @return Optional containing SRS settings if found
     */
    Optional<SrsSettings> findByUser(User user);

    /**
     * Check if SRS settings exist for a user.
     *
     * @param user User entity
     * @return true if settings exist
     */
    boolean existsByUser(User user);

    /**
     * Delete SRS settings for a user.
     *
     * @param user User entity
     */
    void deleteByUser(User user);
}
