package com.repeatwise.entity;

import com.repeatwise.entity.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * Refresh Token Entity
 *
 * Requirements:
 * - UC-003: User Logout - Token blacklist for logout
 * - API Spec: POST /api/auth/refresh - Refresh token rotation
 * - Security: 7-day expiry, HttpOnly cookie storage
 * - Database Schema: refresh_tokens table (section 2.9)
 *
 * Business Rules:
 * - BR-005: Token Invalidation - Server-side token blacklist
 * - BR-007: Multi-Device Logout - Track tokens per device
 *
 * Note: Extends BaseEntity for id, created_at, updated_at
 * But also has created_at field explicitly for backward compatibility
 *
 * @author RepeatWise Team
 */
@Entity
@Table(name = "refresh_tokens", indexes = {
    @Index(name = "idx_refresh_tokens_token", columnList = "token"),
    @Index(name = "idx_refresh_tokens_user_id", columnList = "user_id"),
    @Index(name = "idx_refresh_tokens_user_device", columnList = "user_id, device_id")
})
@EntityListeners(org.springframework.data.jpa.domain.support.AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RefreshToken extends BaseEntity {

    @NotNull(message = "{error.refreshtoken.token.required}")
    @Column(name = "token", nullable = false, unique = true, length = 500)
    private String token;

    @NotNull(message = "{error.user.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "device_id", length = 100)
    private String deviceId;

    @Column(name = "device_info", length = 255)
    private String deviceInfo;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @NotNull(message = "{error.refreshtoken.expiresat.required}")
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @NotNull(message = "{error.refreshtoken.isrevoked.required}")
    @Column(name = "is_revoked", nullable = false)
    @Builder.Default
    private Boolean isRevoked = false;

    /**
     * Check if refresh token is expired
     *
     * @return true if expired, false otherwise
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    /**
     * Check if refresh token is valid (not expired and not revoked)
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return !isExpired() && !isRevoked;
    }

    /**
     * Revoke this refresh token (soft delete for audit trail)
     */
    public void revoke() {
        this.isRevoked = true;
        this.revokedAt = Instant.now();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final RefreshToken that)) {
            return false;
        }
        return (getId() != null) && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
