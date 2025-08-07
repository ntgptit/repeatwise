-- Add authentication fields to users table
ALTER TABLE users ADD COLUMN name VARCHAR(128) NOT NULL DEFAULT 'User';
ALTER TABLE users ADD COLUMN password VARCHAR(255) NOT NULL DEFAULT '$2a$10$dummy.hash.for.existing.users';

-- Update existing users to have a proper name (using username as name)
UPDATE users SET name = username WHERE name = 'User';

-- Make name field not null after setting default values
ALTER TABLE users ALTER COLUMN name SET NOT NULL;
ALTER TABLE users ALTER COLUMN password SET NOT NULL;
