-- V6: Create SRS-related tables
-- Purpose: Spaced Repetition System settings, card positions, and review logs

-- Table: srs_settings
-- Purpose: User SRS configuration
CREATE TABLE srs_settings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
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

    -- Foreign Keys
    CONSTRAINT fk_srs_settings_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,

    -- Constraints
    CONSTRAINT chk_total_boxes CHECK (total_boxes = 7),
    CONSTRAINT chk_review_order CHECK (review_order IN ('ASCENDING', 'DESCENDING', 'RANDOM')),
    CONSTRAINT chk_forgotten_card_action CHECK (forgotten_card_action IN ('MOVE_TO_BOX_1', 'MOVE_DOWN_N_BOXES', 'STAY_IN_BOX')),
    CONSTRAINT chk_move_down_boxes CHECK (move_down_boxes BETWEEN 1 AND 3),
    CONSTRAINT chk_new_cards_per_day CHECK (new_cards_per_day BETWEEN 1 AND 100),
    CONSTRAINT chk_max_reviews_per_day CHECK (max_reviews_per_day BETWEEN 1 AND 500)
);

-- Indexes for srs_settings
CREATE INDEX idx_srs_settings_user ON srs_settings (user_id);

-- Comments for srs_settings
COMMENT ON TABLE srs_settings IS 'User SRS (Spaced Repetition System) configuration';
COMMENT ON COLUMN srs_settings.total_boxes IS 'Total SRS boxes (fixed at 7 for MVP)';
COMMENT ON COLUMN srs_settings.review_order IS 'Review order: ASCENDING, DESCENDING, or RANDOM';
COMMENT ON COLUMN srs_settings.forgotten_card_action IS 'Action when card is rated AGAIN';
COMMENT ON COLUMN srs_settings.move_down_boxes IS 'Number of boxes to move down (1-3)';
COMMENT ON COLUMN srs_settings.new_cards_per_day IS 'Maximum new cards per day (1-100)';
COMMENT ON COLUMN srs_settings.max_reviews_per_day IS 'Maximum reviews per day (1-500)';

-- Table: card_box_position
-- Purpose: SRS state per user per card (CRITICAL for review performance)
CREATE TABLE card_box_position (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
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

    -- Foreign Keys
    CONSTRAINT fk_card_box_position_card FOREIGN KEY (card_id)
        REFERENCES cards(id) ON DELETE CASCADE,
    CONSTRAINT fk_card_box_position_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,

    -- Constraints
    CONSTRAINT chk_current_box CHECK (current_box BETWEEN 1 AND 7),
    CONSTRAINT chk_interval_days CHECK (interval_days >= 1),
    CONSTRAINT chk_review_count CHECK (review_count >= 0),
    CONSTRAINT chk_lapse_count CHECK (lapse_count >= 0)
);

-- Indexes for card_box_position (CRITICAL FOR PERFORMANCE)
CREATE UNIQUE INDEX idx_card_box_position_user_card ON card_box_position (user_id, card_id)
    WHERE deleted_at IS NULL;
CREATE INDEX idx_card_box_user_due ON card_box_position (user_id, due_date, current_box)
    WHERE deleted_at IS NULL;
CREATE INDEX idx_card_box_user_box ON card_box_position (user_id, current_box)
    WHERE deleted_at IS NULL;
CREATE INDEX idx_card_box_new ON card_box_position (user_id, card_id)
    WHERE review_count = 0 AND deleted_at IS NULL;

-- Comments for card_box_position
COMMENT ON TABLE card_box_position IS 'SRS state per user per card (most critical table for review performance)';
COMMENT ON COLUMN card_box_position.current_box IS 'Current SRS box (1-7)';
COMMENT ON COLUMN card_box_position.interval_days IS 'Days until next review';
COMMENT ON COLUMN card_box_position.due_date IS 'Next review due date';
COMMENT ON COLUMN card_box_position.review_count IS 'Total number of reviews';
COMMENT ON COLUMN card_box_position.lapse_count IS 'Number of times forgotten (AGAIN rating)';
COMMENT ON COLUMN card_box_position.deleted_at IS 'Soft delete timestamp';
COMMENT ON INDEX idx_card_box_user_due IS 'MOST IMPORTANT index for review session queries';

-- Table: review_logs
-- Purpose: Immutable review history for analytics, undo, and statistics
CREATE TABLE review_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    card_id UUID NOT NULL,
    user_id UUID NOT NULL,
    rating VARCHAR(10) NOT NULL,
    previous_box INTEGER NOT NULL,
    new_box INTEGER NOT NULL,
    interval_days INTEGER NOT NULL,
    reviewed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Keys
    CONSTRAINT fk_review_logs_card FOREIGN KEY (card_id)
        REFERENCES cards(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_logs_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,

    -- Constraints
    CONSTRAINT chk_rating CHECK (rating IN ('AGAIN', 'HARD', 'GOOD', 'EASY')),
    CONSTRAINT chk_previous_box CHECK (previous_box BETWEEN 1 AND 7),
    CONSTRAINT chk_new_box CHECK (new_box BETWEEN 1 AND 7),
    CONSTRAINT chk_interval_days_log CHECK (interval_days >= 1)
);

-- Indexes for review_logs
CREATE INDEX idx_review_logs_user_date ON review_logs (user_id, reviewed_at DESC);
CREATE INDEX idx_review_logs_card ON review_logs (card_id);
CREATE INDEX idx_review_logs_user_today ON review_logs (user_id, reviewed_at)
    WHERE reviewed_at >= CURRENT_DATE;

-- Comments for review_logs
COMMENT ON TABLE review_logs IS 'Immutable review history for analytics, undo, and statistics';
COMMENT ON COLUMN review_logs.rating IS 'User rating: AGAIN, HARD, GOOD, or EASY';
COMMENT ON COLUMN review_logs.previous_box IS 'Box number before review';
COMMENT ON COLUMN review_logs.new_box IS 'Box number after review';
COMMENT ON COLUMN review_logs.interval_days IS 'Interval assigned after review';
COMMENT ON INDEX idx_review_logs_user_today IS 'Optimized for daily statistics queries';
