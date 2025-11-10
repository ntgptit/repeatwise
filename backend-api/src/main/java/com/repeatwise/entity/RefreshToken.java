package com.repeatwise.entity;

import java.time.LocalDateTime;

import com.repeatwise.entity.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Refresh token entity for JWT authentication
 */
@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken extends BaseEntity {

    @NotBlank(message = "{error.auth.token.required}")
    @Size(max = 500, message = "{error.auth.token.length}")
    @Column(name = "token", nullable = false, unique = true, length = 500)
    private String token;

    @NotNull(message = "{error.user.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Size(max = 100, message = "{error.refresh.token.device.id.length}")
    @Column(name = "device_id", length = 100)
    private String deviceId;

    @Size(max = 255, message = "{error.refresh.token.device.info.length}")
    @Column(name = "device_info", length = 255)
    private String deviceInfo;

    @Size(max = 50, message = "{error.refresh.token.ip.length}")
    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @NotNull(message = "{error.refresh.token.expiration.required}")
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Builder.Default
    @Column(name = "is_revoked", nullable = false)
    private Boolean isRevoked = false;

    /**
     * Check if token is expired
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    /**
     * Check if token is valid (not revoked and not expired)
     */
    public boolean isValid() {
        return !this.isRevoked && !isExpired();
    }

    /**
     * Revoke the token
     */
    public void revoke() {
        this.isRevoked = true;
        this.revokedAt = LocalDateTime.now();
    }
}
