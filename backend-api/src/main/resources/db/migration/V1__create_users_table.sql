-- RepeatWise Database Migration
-- Version: V1
-- Description: Create users table
-- Requirements: UC-001 User Registration, UC-002 User Login
-- Date: 2025-01-18

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(30) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(60) NOT NULL,
    name VARCHAR(100) NOT NULL,
    timezone VARCHAR(50) NOT NULL DEFAULT 'Asia/Ho_Chi_Minh',
    language VARCHAR(10) NOT NULL DEFAULT 'VI',
    theme VARCHAR(10) NOT NULL DEFAULT 'SYSTEM',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT chk_username_format CHECK (username ~* '^[a-z0-9_]{3,30}$'),
    CONSTRAINT chk_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT chk_password_hash_bcrypt CHECK (LENGTH(password_hash) = 60),
    CONSTRAINT chk_name_not_empty CHECK (TRIM(name) != ''),
    CONSTRAINT chk_language_valid CHECK (language IN ('VI', 'EN')),
    CONSTRAINT chk_theme_valid CHECK (theme IN ('LIGHT', 'DARK', 'SYSTEM'))
);

-- Create indexes for performance optimization
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email
    ON users(LOWER(email));

CREATE UNIQUE INDEX IF NOT EXISTS idx_users_username
    ON users(LOWER(username));

-- Add comments for documentation
COMMENT ON TABLE users IS 'User accounts for RepeatWise application';
COMMENT ON COLUMN users.username IS 'Unique username (3-30 chars, lowercase, numbers, underscore)';
COMMENT ON COLUMN users.email IS 'Unique email address for login and notifications';
COMMENT ON COLUMN users.password_hash IS 'Bcrypt hash of user password (cost factor 12)';
COMMENT ON COLUMN users.name IS 'Display name of user';
COMMENT ON COLUMN users.timezone IS 'User timezone for due date calculation (default: Asia/Ho_Chi_Minh)';
COMMENT ON COLUMN users.language IS 'Preferred language: VI (Vietnamese) or EN (English)';
COMMENT ON COLUMN users.theme IS 'UI theme preference: LIGHT, DARK, or SYSTEM';
