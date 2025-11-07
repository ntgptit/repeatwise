-- V5: Create cards table
-- Purpose: Flashcards with front and back text

CREATE TABLE cards (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    deck_id UUID NOT NULL,
    front TEXT NOT NULL,
    back TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,

    -- Foreign Keys
    CONSTRAINT fk_cards_deck FOREIGN KEY (deck_id)
        REFERENCES decks(id) ON DELETE CASCADE,

    -- Constraints
    CONSTRAINT chk_front_not_empty CHECK (TRIM(front) <> ''),
    CONSTRAINT chk_back_not_empty CHECK (TRIM(back) <> ''),
    CONSTRAINT chk_front_length CHECK (LENGTH(front) <= 5000),
    CONSTRAINT chk_back_length CHECK (LENGTH(back) <= 5000)
);

-- Indexes
CREATE INDEX idx_cards_deck ON cards (deck_id)
    WHERE deleted_at IS NULL;

-- Comments
COMMENT ON TABLE cards IS 'Flashcards with front and back text';
COMMENT ON COLUMN cards.front IS 'Front side text (max 5,000 characters)';
COMMENT ON COLUMN cards.back IS 'Back side text (max 5,000 characters)';
COMMENT ON COLUMN cards.deleted_at IS 'Soft delete timestamp';
