-- Add unique constraint to username field
ALTER TABLE users ADD CONSTRAINT uq_users_username UNIQUE (username);

COMMENT ON COLUMN users.username IS 'Unique username for login.';
