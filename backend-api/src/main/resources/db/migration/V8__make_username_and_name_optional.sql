-- V8: Make username and name optional
-- Purpose: Allow users to register with email only (username and name are optional)
-- Related to: UC-001 User Registration

-- Make username nullable
ALTER TABLE users ALTER COLUMN username DROP NOT NULL;

-- Make name nullable
ALTER TABLE users ALTER COLUMN name DROP NOT NULL;

-- Drop the constraint that requires name to not be empty
-- (it will be checked at application level if provided)
ALTER TABLE users DROP CONSTRAINT IF EXISTS chk_name_not_empty;

-- Update unique index on username to handle NULL values
-- PostgreSQL unique index allows multiple NULL values
-- Only enforce uniqueness for non-NULL usernames
DROP INDEX IF EXISTS idx_users_username;
CREATE UNIQUE INDEX idx_users_username ON users (username) WHERE username IS NOT NULL;

-- Update comments
COMMENT ON COLUMN users.username IS 'Username (3-30 chars, alphanumeric + underscore) - Optional';
COMMENT ON COLUMN users.name IS 'Full name - Optional';
