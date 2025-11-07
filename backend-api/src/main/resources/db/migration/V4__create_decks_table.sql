-- V4: Create decks table
-- Purpose: Flashcard decks containing cards

CREATE TABLE decks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    folder_id UUID,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,

    -- Foreign Keys
    CONSTRAINT fk_decks_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_decks_folder FOREIGN KEY (folder_id)
        REFERENCES folders(id) ON DELETE CASCADE,

    -- Constraints
    CONSTRAINT chk_deck_name_not_empty CHECK (TRIM(name) <> '')
);

-- Indexes
CREATE INDEX idx_decks_user ON decks (user_id)
    WHERE deleted_at IS NULL;
CREATE INDEX idx_decks_folder ON decks (folder_id)
    WHERE deleted_at IS NULL;
CREATE UNIQUE INDEX idx_decks_name_folder ON decks (
    user_id,
    COALESCE(folder_id::TEXT, 'ROOT'),
    LOWER(name)
) WHERE deleted_at IS NULL;

-- Comments
COMMENT ON TABLE decks IS 'Flashcard decks containing cards';
COMMENT ON COLUMN decks.folder_id IS 'Parent folder reference (NULL = root level deck)';
COMMENT ON COLUMN decks.deleted_at IS 'Soft delete timestamp';
COMMENT ON INDEX idx_decks_name_folder IS 'Enforces unique deck name per folder per user (case-insensitive)';
