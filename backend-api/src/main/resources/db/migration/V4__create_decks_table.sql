-- RepeatWise Database Migration
-- Version: V4
-- Description: Create decks table for flashcard decks
-- Requirements: UC-011 to UC-014 - Deck Management
-- Date: 2025-01-18

-- Create decks table
CREATE TABLE IF NOT EXISTS decks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    folder_id UUID,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,

    -- Foreign keys
    CONSTRAINT fk_decks_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_decks_folder
        FOREIGN KEY (folder_id) REFERENCES folders(id) ON DELETE CASCADE,

    -- Constraints
    CONSTRAINT chk_name_not_empty CHECK (TRIM(name) != '')
);

-- Create indexes for performance optimization
CREATE INDEX IF NOT EXISTS idx_decks_user
    ON decks(user_id)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_decks_folder
    ON decks(folder_id)
    WHERE deleted_at IS NULL;

-- Business rule: Deck name unique within folder (per user)
CREATE UNIQUE INDEX IF NOT EXISTS idx_decks_name_folder
    ON decks(user_id, COALESCE(folder_id::TEXT, 'ROOT'), name)
    WHERE deleted_at IS NULL;

-- Add comments for documentation
COMMENT ON TABLE decks IS 'Flashcard decks containing cards for study';
COMMENT ON COLUMN decks.user_id IS 'Owner of this deck';
COMMENT ON COLUMN decks.folder_id IS 'Parent folder ID (NULL for root-level decks)';
COMMENT ON COLUMN decks.name IS 'Deck name (unique within same folder per user)';
COMMENT ON COLUMN decks.description IS 'Optional deck description';
COMMENT ON COLUMN decks.deleted_at IS 'Soft delete timestamp (NULL = active)';
