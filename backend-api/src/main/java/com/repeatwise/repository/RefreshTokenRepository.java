package com.repeatwise.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.repeatwise.entity.RefreshToken;
import com.repeatwise.entity.User;

/**
 * Repository for RefreshToken entity with Spring Data JPA.
 * Provides database access methods for JWT refresh token management.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    /**
     * Find refresh token by token string.
     * Used for token refresh operations.
     *
     * @param token Token string
     * @return Optional containing refresh token if found
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Find all refresh tokens for a user.
     *
     * @param user User entity
     * @return List of refresh tokens
     */
    List<RefreshToken> findByUser(User user);

    /**
     * Find all active (non-revoked, non-expired) refresh tokens for a user.
     *
     * @param user User entity
     * @param now  Current timestamp
     * @return List of active refresh tokens
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user = :user AND rt.isRevoked = false AND rt.expiresAt > :now")
    List<RefreshToken> findActiveTokensByUser(@Param("user") User user, @Param("now") LocalDateTime now);

    /**
     * Revoke all refresh tokens for a user (logout from all devices).
     *
     * @param user      User entity
     * @param revokedAt Timestamp of revocation
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.isRevoked = true, rt.revokedAt = :revokedAt WHERE rt.user = :user AND rt.isRevoked = false")
    void revokeAllTokensByUser(@Param("user") User user, @Param("revokedAt") LocalDateTime revokedAt);

    /**
     * Delete expired refresh tokens for cleanup.
     *
     * @param now Current timestamp
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    /**
     * Delete all refresh tokens for a user.
     *
     * @param user User entity
     */
    void deleteByUser(User user);
}
