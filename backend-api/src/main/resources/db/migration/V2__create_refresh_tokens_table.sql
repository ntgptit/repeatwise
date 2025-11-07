-- V2: Create refresh_tokens table
-- Purpose: JWT refresh tokens for authentication and logout tracking

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    token VARCHAR(500) NOT NULL,
    user_id UUID NOT NULL,
    device_id VARCHAR(100),
    device_info VARCHAR(255),
    ip_address VARCHAR(50),
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoked_at TIMESTAMP,
    is_revoked BOOLEAN NOT NULL DEFAULT FALSE,

    -- Foreign Keys
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,

    -- Constraints
    CONSTRAINT chk_expires_at_future CHECK (expires_at > created_at),
    CONSTRAINT chk_revoked_at_valid CHECK (revoked_at IS NULL OR revoked_at >= created_at)
);

-- Indexes
CREATE UNIQUE INDEX idx_refresh_tokens_token ON refresh_tokens (token);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id)
    WHERE is_revoked = FALSE AND expires_at > NOW();
CREATE INDEX idx_refresh_tokens_user_device ON refresh_tokens (user_id, device_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens (expires_at)
    WHERE is_revoked = FALSE;

-- Comments
COMMENT ON TABLE refresh_tokens IS 'JWT refresh tokens for authentication and logout tracking';
COMMENT ON COLUMN refresh_tokens.token IS 'Refresh token string (should be hashed)';
COMMENT ON COLUMN refresh_tokens.device_id IS 'Device identifier for multi-device support';
COMMENT ON COLUMN refresh_tokens.device_info IS 'Device information (browser, OS)';
COMMENT ON COLUMN refresh_tokens.expires_at IS 'Token expiration (7 days from creation)';
COMMENT ON COLUMN refresh_tokens.is_revoked IS 'Revocation flag for soft delete';
