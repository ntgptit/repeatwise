-- RepeatWise Database Migration
-- Version: V5
-- Description: Create cards table for flashcards
-- Requirements: UC-015 to UC-018 - Flashcard Management
-- Date: 2025-01-18

-- Create cards table
CREATE TABLE IF NOT EXISTS cards (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    deck_id UUID NOT NULL,
    front TEXT NOT NULL,
    back TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,

    -- Foreign keys
    CONSTRAINT fk_cards_deck
        FOREIGN KEY (deck_id) REFERENCES decks(id) ON DELETE CASCADE,

    -- Constraints
    CONSTRAINT chk_front_not_empty CHECK (TRIM(front) != ''),
    CONSTRAINT chk_back_not_empty CHECK (TRIM(back) != ''),
    CONSTRAINT chk_front_length CHECK (LENGTH(front) <= 5000),
    CONSTRAINT chk_back_length CHECK (LENGTH(back) <= 5000)
);

-- Create indexes for performance optimization
CREATE INDEX IF NOT EXISTS idx_cards_deck
    ON cards(deck_id)
    WHERE deleted_at IS NULL;

-- Add comments for documentation
COMMENT ON TABLE cards IS 'Flashcards with front and back text (basic MVP version)';
COMMENT ON COLUMN cards.deck_id IS 'Parent deck containing this card';
COMMENT ON COLUMN cards.front IS 'Front side of flashcard (question/prompt, max 5000 chars)';
COMMENT ON COLUMN cards.back IS 'Back side of flashcard (answer/explanation, max 5000 chars)';
COMMENT ON COLUMN cards.deleted_at IS 'Soft delete timestamp (NULL = active)';
