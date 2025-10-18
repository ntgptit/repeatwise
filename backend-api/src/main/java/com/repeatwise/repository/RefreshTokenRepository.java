package com.repeatwise.repository;

import com.repeatwise.entity.RefreshToken;
import com.repeatwise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Refresh Token Repository
 *
 * Requirements:
 * - UC-003: User Logout - Find and revoke refresh tokens
 * - API Spec: POST /api/auth/logout - Single device logout
 * - API Spec: POST /api/auth/logout-all - All devices logout
 *
 * @author RepeatWise Team
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    /**
     * Find refresh token by token string
     *
     * @param token Refresh token string
     * @return Optional RefreshToken
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Find all valid (non-revoked, non-expired) refresh tokens for a user
     *
     * @param user User entity
     * @return List of valid refresh tokens
     */
    @Query("SELECT rt FROM RefreshToken rt " +
           "WHERE rt.user = :user " +
           "AND rt.isRevoked = false " +
           "AND rt.expiresAt > :now")
    List<RefreshToken> findAllValidByUser(
        @Param("user") User user,
        @Param("now") Instant now
    );

    /**
     * Find all refresh tokens for a user (including revoked and expired)
     *
     * @param user User entity
     * @return List of all refresh tokens
     */
    List<RefreshToken> findAllByUser(User user);

    /**
     * Revoke all refresh tokens for a user (logout from all devices)
     *
     * @param userId User ID
     * @param revokedAt Revocation timestamp
     * @return Number of tokens revoked
     */
    @Modifying
    @Query("UPDATE RefreshToken rt " +
           "SET rt.isRevoked = true, rt.revokedAt = :revokedAt " +
           "WHERE rt.user.id = :userId " +
           "AND rt.isRevoked = false")
    int revokeAllByUserId(
        @Param("userId") UUID userId,
        @Param("revokedAt") Instant revokedAt
    );

    /**
     * Delete all expired refresh tokens (cleanup job)
     *
     * @param now Current timestamp
     * @return Number of tokens deleted
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
    int deleteAllExpired(@Param("now") Instant now);

    /**
     * Delete all revoked refresh tokens older than specified date (cleanup job)
     *
     * @param revokedBefore Timestamp threshold
     * @return Number of tokens deleted
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt " +
           "WHERE rt.isRevoked = true " +
           "AND rt.revokedAt < :revokedBefore")
    int deleteAllRevokedBefore(@Param("revokedBefore") Instant revokedBefore);

    /**
     * Count valid refresh tokens for a user
     *
     * @param userId User ID
     * @param now Current timestamp
     * @return Count of valid tokens
     */
    @Query("SELECT COUNT(rt) FROM RefreshToken rt " +
           "WHERE rt.user.id = :userId " +
           "AND rt.isRevoked = false " +
           "AND rt.expiresAt > :now")
    long countValidByUserId(
        @Param("userId") UUID userId,
        @Param("now") Instant now
    );

    /**
     * Check if a refresh token exists and is valid
     *
     * @param token Refresh token string
     * @param now Current timestamp
     * @return true if exists and valid, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(rt) > 0 THEN true ELSE false END " +
           "FROM RefreshToken rt " +
           "WHERE rt.token = :token " +
           "AND rt.isRevoked = false " +
           "AND rt.expiresAt > :now")
    boolean existsValidByToken(
        @Param("token") String token,
        @Param("now") Instant now
    );
}
