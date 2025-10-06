package com.spacedlearning.repository;

import com.spacedlearning.entity.EmailVerification;
import com.spacedlearning.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for EmailVerification entity
 * Provides data access methods for email verification management
 */
@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, UUID> {

    /**
     * Find email verification by verification token
     * @param token verification token
     * @return Optional containing email verification if found
     */
    Optional<EmailVerification> findByVerificationToken(String token);

    /**
     * Find active email verification by user
     * @param user user entity
     * @return Optional containing active email verification if found
     */
    @Query("SELECT ev FROM EmailVerification ev WHERE ev.user = :user AND ev.isVerified = false AND ev.expiresAt > :now")
    Optional<EmailVerification> findActiveByUser(@Param("user") User user, @Param("now") LocalDateTime now);

    /**
     * Find all expired email verifications
     * @param now current time
     * @return list of expired email verifications
     */
    @Query("SELECT ev FROM EmailVerification ev WHERE ev.expiresAt <= :now AND ev.isVerified = false")
    List<EmailVerification> findExpiredVerifications(@Param("now") LocalDateTime now);

    /**
     * Delete expired email verifications
     * @param now current time
     * @return number of deleted records
     */
    @Modifying
    @Query("DELETE FROM EmailVerification ev WHERE ev.expiresAt <= :now AND ev.isVerified = false")
    int deleteExpiredVerifications(@Param("now") LocalDateTime now);

    /**
     * Check if user has pending verification
     * @param user user entity
     * @param now current time
     * @return true if user has pending verification
     */
    @Query("SELECT COUNT(ev) > 0 FROM EmailVerification ev WHERE ev.user = :user AND ev.isVerified = false AND ev.expiresAt > :now")
    boolean hasPendingVerification(@Param("user") User user, @Param("now") LocalDateTime now);
}

