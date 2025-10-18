-- RepeatWise Database Migration
-- Version: V2
-- Description: Create refresh_tokens table for JWT authentication
-- Requirements: UC-002 User Login, UC-003 User Logout
-- Date: 2025-01-18

-- Create refresh_tokens table
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    token VARCHAR(500) NOT NULL UNIQUE,
    user_id UUID NOT NULL,
    device_id VARCHAR(100),
    device_info VARCHAR(255),
    ip_address VARCHAR(50),
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoked_at TIMESTAMP,
    is_revoked BOOLEAN NOT NULL DEFAULT FALSE,

    -- Foreign keys
    CONSTRAINT fk_refresh_tokens_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    -- Constraints
    CONSTRAINT chk_expires_at_future CHECK (expires_at > created_at),
    CONSTRAINT chk_revoked_at_valid CHECK (revoked_at IS NULL OR revoked_at >= created_at)
);

-- Create indexes for performance optimization
CREATE UNIQUE INDEX IF NOT EXISTS idx_refresh_tokens_token
    ON refresh_tokens(token);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id
    ON refresh_tokens(user_id)
    WHERE is_revoked = FALSE AND expires_at > NOW();

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_device
    ON refresh_tokens(user_id, device_id);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_expires_at
    ON refresh_tokens(expires_at)
    WHERE is_revoked = FALSE;

-- Add comments for documentation
COMMENT ON TABLE refresh_tokens IS 'JWT refresh tokens for authentication and logout tracking';
COMMENT ON COLUMN refresh_tokens.token IS 'Unique refresh token string (should be hashed in production)';
COMMENT ON COLUMN refresh_tokens.user_id IS 'User who owns this refresh token';
COMMENT ON COLUMN refresh_tokens.device_id IS 'Device identifier for multi-device logout support';
COMMENT ON COLUMN refresh_tokens.device_info IS 'Device information (browser, OS, mobile app version)';
COMMENT ON COLUMN refresh_tokens.ip_address IS 'IP address when token was created (security audit)';
COMMENT ON COLUMN refresh_tokens.expires_at IS 'Token expiration timestamp (7 days from creation for MVP)';
COMMENT ON COLUMN refresh_tokens.created_at IS 'Token creation timestamp';
COMMENT ON COLUMN refresh_tokens.revoked_at IS 'Timestamp when token was revoked (logout event)';
COMMENT ON COLUMN refresh_tokens.is_revoked IS 'Whether token has been revoked (soft delete for audit trail)';
