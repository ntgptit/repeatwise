-- V1: Create users table
-- Purpose: User accounts and authentication

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) NOT NULL,
    username VARCHAR(30) NOT NULL,
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
    CONSTRAINT chk_name_not_empty CHECK (TRIM(name) <> ''),
    CONSTRAINT chk_language_valid CHECK (language IN ('VI', 'EN')),
    CONSTRAINT chk_theme_valid CHECK (theme IN ('LIGHT', 'DARK', 'SYSTEM'))
);

-- Indexes
CREATE UNIQUE INDEX idx_users_email ON users (LOWER(email));
CREATE UNIQUE INDEX idx_users_username ON users (LOWER(username));

-- Comments
COMMENT ON TABLE users IS 'User accounts and authentication';
COMMENT ON COLUMN users.email IS 'User email (case-insensitive, unique)';
COMMENT ON COLUMN users.username IS 'Username (3-30 chars, alphanumeric + underscore)';
COMMENT ON COLUMN users.password_hash IS 'Bcrypt hash (cost factor 12)';
COMMENT ON COLUMN users.timezone IS 'IANA timezone identifier';
COMMENT ON COLUMN users.language IS 'Language preference (VI or EN)';
COMMENT ON COLUMN users.theme IS 'UI theme (LIGHT, DARK, SYSTEM)';
