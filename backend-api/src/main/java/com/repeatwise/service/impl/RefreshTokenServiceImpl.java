package com.repeatwise.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repeatwise.config.properties.JwtProperties;
import com.repeatwise.entity.RefreshToken;
import com.repeatwise.entity.User;
import com.repeatwise.exception.RepeatWiseError;
import com.repeatwise.exception.RepeatWiseException;
import com.repeatwise.repository.RefreshTokenRepository;
import com.repeatwise.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of RefreshTokenService.
 * Manages refresh token lifecycle: creation, validation, and revocation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final int BCRYPT_COST_FACTOR = 12;

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    /**
     * Generate a secure random refresh token.
     * Uses UUID for simplicity and security.
     *
     * @return Random token string
     */
    private String generateRandomToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generate and save a new refresh token for user.
     * Token is hashed with BCrypt before storing.
     */
    @Override
    @Transactional
    public String createRefreshToken(User user, String deviceId, String deviceInfo, String ipAddress) {
        // Generate random token
        final var tokenValue = generateRandomToken();

        // Hash token with BCrypt
        final var tokenHash = BCrypt.hashpw(tokenValue, BCrypt.gensalt(BCRYPT_COST_FACTOR));

        // Calculate expiry time (7 days from now)
        final var expiresAt = LocalDateTime.now()
                .plusDays(this.jwtProperties.getRefreshTokenExpirationDays());

        // Create RefreshToken entity
        final var refreshToken = RefreshToken.builder()
                .user(user)
                .token(tokenHash)
                .deviceId(deviceId)
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .expiresAt(expiresAt)
                .isRevoked(false)
                .build();

        // Save to database
        this.refreshTokenRepository.save(refreshToken);
        log.info("Created refresh token for user: {}, device: {}", user.getId(), deviceId);

        // Return plain text token (not hashed)
        return tokenValue;
    }

    /**
     * Find refresh token entity by plain token (internal helper).
     * Does not throw exception if not found.
     * Searches all tokens (including revoked/expired) for security checks.
     *
     * @param token Plain text token
     * @return RefreshToken entity or null if not found
     */
    private RefreshToken findRefreshTokenByValue(String token) {
        // Find all tokens (including revoked ones for security checks)
        // (We need to check all because token is hashed)
        final var candidates = this.refreshTokenRepository.findAll();

        // Find matching token by comparing hash
        for (final var candidate : candidates) {
            if (BCrypt.checkpw(token, candidate.getToken())) {
                log.debug("Refresh token found for user: {}", candidate.getUser().getId());
                return candidate;
            }
        }

        return null;
    }

    /**
     * Validate refresh token and return associated RefreshToken entity.
     * Checks token hash, expiration, and revocation status.
     */
    @Override
    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String token) {
        final var refreshToken = findRefreshTokenByValue(token);
        if (refreshToken == null) {
            log.warn("Refresh token not found or invalid");
            throw new RepeatWiseException(RepeatWiseError.REFRESH_TOKEN_NOT_FOUND);
        }

        // Check if token is expired
        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Refresh token expired for user: {}", refreshToken.getUser().getId());
            throw new RepeatWiseException(RepeatWiseError.REFRESH_TOKEN_EXPIRED);
        }

        // Check if token is revoked
        if (refreshToken.getIsRevoked()) {
            log.warn("Refresh token revoked for user: {}", refreshToken.getUser().getId());
            throw new RepeatWiseException(RepeatWiseError.REFRESH_TOKEN_REVOKED);
        }

        return refreshToken;
    }

    /**
     * Validate and rotate refresh token (token rotation for security).
     * This implements the refresh token rotation pattern for enhanced security.
     */
    @Override
    @Transactional
    public String validateAndRotateRefreshToken(String token, String deviceId,
            String deviceInfo, String ipAddress) {
        // Find token (including revoked ones for security check)
        final var oldRefreshToken = findRefreshTokenByValue(token);
        if (oldRefreshToken == null) {
            log.warn("Refresh token not found");
            throw new RepeatWiseException(RepeatWiseError.REFRESH_TOKEN_NOT_FOUND);
        }

        // Check if token is expired
        if (oldRefreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Refresh token expired for user: {}", oldRefreshToken.getUser().getId());
            throw new RepeatWiseException(RepeatWiseError.REFRESH_TOKEN_EXPIRED);
        }

        // SECURITY: Check if token is already revoked (token reuse detection)
        if (oldRefreshToken.getIsRevoked()) {
            log.error("Token reuse detected for user: {}. Revoking all tokens.",
                    oldRefreshToken.getUser().getId());
            // Revoke all tokens for this user (security measure)
            revokeAllUserTokens(oldRefreshToken.getUser());
            throw new RepeatWiseException(RepeatWiseError.TOKEN_REUSE_DETECTED);
        }

        // Token is valid - proceed with rotation
        final var user = oldRefreshToken.getUser();

        // Generate new refresh token
        final var newRefreshToken = createRefreshToken(user, deviceId, deviceInfo, ipAddress);

        // Revoke old refresh token
        oldRefreshToken.revoke();
        this.refreshTokenRepository.save(oldRefreshToken);

        log.info("Rotated refresh token for user: {}", user.getId());
        return newRefreshToken;
    }

    /**
     * Revoke a specific refresh token.
     */
    @Override
    @Transactional
    public void revokeRefreshToken(String token) {
        // Use private helper instead of calling transactional method
        final var refreshToken = findRefreshTokenByValue(token);
        if (refreshToken == null) {
            log.warn("Cannot revoke non-existent token");
            return; // Silently fail - token doesn't exist or already invalid
        }

        refreshToken.revoke();
        this.refreshTokenRepository.save(refreshToken);
        log.info("Revoked refresh token for user: {}", refreshToken.getUser().getId());
    }

    /**
     * Revoke all refresh tokens for a user (logout from all devices).
     */
    @Override
    @Transactional
    public void revokeAllUserTokens(User user) {
        this.refreshTokenRepository.revokeAllTokensByUser(user, LocalDateTime.now());
        log.info("Revoked all refresh tokens for user: {}", user.getId());
    }

    /**
     * Delete expired refresh tokens (cleanup job).
     * Should be called periodically (e.g., daily cron job).
     */
    @Override
    @Transactional
    public void deleteExpiredTokens() {
        final var now = LocalDateTime.now();
        this.refreshTokenRepository.deleteExpiredTokens(now);
        log.info("Deleted expired refresh tokens");
    }
}
