-- RepeatWise Database Migration
-- Version: V6
-- Description: Create SRS (Spaced Repetition System) tables
-- Requirements: UC-019 to UC-022 - SRS Review & Settings
-- Date: 2025-01-18

-- ============================================================
-- Table 1: srs_settings - User SRS Configuration
-- ============================================================
CREATE TABLE IF NOT EXISTS srs_settings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    total_boxes INTEGER NOT NULL DEFAULT 7,
    review_order VARCHAR(20) NOT NULL DEFAULT 'RANDOM',
    notification_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    notification_time TIME NOT NULL DEFAULT '09:00',
    forgotten_card_action VARCHAR(30) NOT NULL DEFAULT 'MOVE_TO_BOX_1',
    move_down_boxes INTEGER NOT NULL DEFAULT 1,
    new_cards_per_day INTEGER NOT NULL DEFAULT 20,
    max_reviews_per_day INTEGER NOT NULL DEFAULT 200,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign keys
    CONSTRAINT fk_srs_settings_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    -- Constraints
    CONSTRAINT chk_total_boxes CHECK (total_boxes = 7),
    CONSTRAINT chk_review_order CHECK (review_order IN ('ASCENDING', 'DESCENDING', 'RANDOM')),
    CONSTRAINT chk_forgotten_card_action CHECK (
        forgotten_card_action IN ('MOVE_TO_BOX_1', 'MOVE_DOWN_N_BOXES', 'STAY_IN_BOX')
    ),
    CONSTRAINT chk_move_down_boxes CHECK (move_down_boxes BETWEEN 1 AND 3),
    CONSTRAINT chk_new_cards_per_day CHECK (new_cards_per_day BETWEEN 1 AND 100),
    CONSTRAINT chk_max_reviews_per_day CHECK (max_reviews_per_day BETWEEN 1 AND 500)
);

-- Indexes for srs_settings
CREATE INDEX IF NOT EXISTS idx_srs_settings_user
    ON srs_settings(user_id);

-- ============================================================
-- Table 2: card_box_position - SRS State per User (CRITICAL)
-- ============================================================
CREATE TABLE IF NOT EXISTS card_box_position (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    card_id UUID NOT NULL,
    user_id UUID NOT NULL,
    current_box INTEGER NOT NULL DEFAULT 1,
    interval_days INTEGER NOT NULL DEFAULT 1,
    due_date DATE NOT NULL,
    review_count INTEGER NOT NULL DEFAULT 0,
    lapse_count INTEGER NOT NULL DEFAULT 0,
    last_reviewed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,

    -- Foreign keys
    CONSTRAINT fk_card_box_position_card
        FOREIGN KEY (card_id) REFERENCES cards(id) ON DELETE CASCADE,
    CONSTRAINT fk_card_box_position_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    -- Constraints
    CONSTRAINT chk_current_box CHECK (current_box BETWEEN 1 AND 7),
    CONSTRAINT chk_interval_days CHECK (interval_days >= 1),
    CONSTRAINT chk_review_count CHECK (review_count >= 0),
    CONSTRAINT chk_lapse_count CHECK (lapse_count >= 0)
);

-- Indexes for card_box_position (CRITICAL FOR PERFORMANCE)
CREATE UNIQUE INDEX IF NOT EXISTS idx_card_box_position_user_card
    ON card_box_position(user_id, card_id)
    WHERE deleted_at IS NULL;

-- Most important index for review session query
CREATE INDEX IF NOT EXISTS idx_card_box_user_due
    ON card_box_position(user_id, due_date, current_box)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_card_box_user_box
    ON card_box_position(user_id, current_box)
    WHERE deleted_at IS NULL;

-- Index for new cards (review_count = 0)
CREATE INDEX IF NOT EXISTS idx_card_box_new
    ON card_box_position(user_id, card_id)
    WHERE review_count = 0 AND deleted_at IS NULL;

-- ============================================================
-- Table 3: review_logs - Review History (Analytics)
-- ============================================================
CREATE TABLE IF NOT EXISTS review_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    card_id UUID NOT NULL,
    user_id UUID NOT NULL,
    rating VARCHAR(10) NOT NULL,
    previous_box INTEGER NOT NULL,
    new_box INTEGER NOT NULL,
    interval_days INTEGER NOT NULL,
    reviewed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign keys
    CONSTRAINT fk_review_logs_card
        FOREIGN KEY (card_id) REFERENCES cards(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_logs_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    -- Constraints
    CONSTRAINT chk_rating CHECK (rating IN ('AGAIN', 'HARD', 'GOOD', 'EASY')),
    CONSTRAINT chk_previous_box CHECK (previous_box BETWEEN 1 AND 7),
    CONSTRAINT chk_new_box CHECK (new_box BETWEEN 1 AND 7),
    CONSTRAINT chk_interval_days_log CHECK (interval_days >= 1)
);

-- Indexes for review_logs
CREATE INDEX IF NOT EXISTS idx_review_logs_user_date
    ON review_logs(user_id, reviewed_at DESC);

CREATE INDEX IF NOT EXISTS idx_review_logs_card
    ON review_logs(card_id);

CREATE INDEX IF NOT EXISTS idx_review_logs_user_today
    ON review_logs(user_id, reviewed_at)
    WHERE reviewed_at >= CURRENT_DATE;

-- ============================================================
-- Comments for documentation
-- ============================================================
COMMENT ON TABLE srs_settings IS 'User SRS (Spaced Repetition System) configuration and preferences';
COMMENT ON TABLE card_box_position IS 'SRS state per user per card - MOST CRITICAL TABLE for review session performance';
COMMENT ON TABLE review_logs IS 'Immutable review history for analytics, undo, and statistics';

COMMENT ON COLUMN card_box_position.current_box IS 'Current box position (1-7) in Leitner system';
COMMENT ON COLUMN card_box_position.interval_days IS 'Days until next review (1,3,7,14,30,60,120)';
COMMENT ON COLUMN card_box_position.due_date IS 'Next review due date (calculated from current_box + interval)';
COMMENT ON COLUMN card_box_position.review_count IS 'Total times this card has been reviewed';
COMMENT ON COLUMN card_box_position.lapse_count IS 'Times user forgot this card (rating = AGAIN)';

COMMENT ON COLUMN review_logs.rating IS 'User rating: AGAIN (forgot), HARD (difficult), GOOD (normal), EASY (too easy)';
COMMENT ON COLUMN review_logs.previous_box IS 'Box before review';
COMMENT ON COLUMN review_logs.new_box IS 'Box after review';
