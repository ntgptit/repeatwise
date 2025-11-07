-- V7: Create statistics tables
-- Purpose: Denormalized statistics for performance optimization

-- Table: user_stats
-- Purpose: Denormalized user statistics for performance
CREATE TABLE user_stats (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL UNIQUE,
    total_cards INTEGER NOT NULL DEFAULT 0,
    total_decks INTEGER NOT NULL DEFAULT 0,
    total_folders INTEGER NOT NULL DEFAULT 0,
    cards_reviewed_today INTEGER NOT NULL DEFAULT 0,
    streak_days INTEGER NOT NULL DEFAULT 0,
    last_study_date DATE,
    total_study_time_minutes INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Keys
    CONSTRAINT fk_user_stats_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);

-- Indexes for user_stats
CREATE UNIQUE INDEX idx_user_stats_user ON user_stats (user_id);

-- Comments for user_stats
COMMENT ON TABLE user_stats IS 'Denormalized user statistics for performance (avoids expensive aggregations)';
COMMENT ON COLUMN user_stats.total_cards IS 'Total cards owned by user';
COMMENT ON COLUMN user_stats.total_decks IS 'Total decks owned by user';
COMMENT ON COLUMN user_stats.total_folders IS 'Total folders owned by user';
COMMENT ON COLUMN user_stats.cards_reviewed_today IS 'Number of cards reviewed today';
COMMENT ON COLUMN user_stats.streak_days IS 'Consecutive study days';
COMMENT ON COLUMN user_stats.last_study_date IS 'Last date user studied';
COMMENT ON COLUMN user_stats.total_study_time_minutes IS 'Total study time in minutes';

-- Table: folder_stats
-- Purpose: Cached folder statistics with TTL (performance optimization)
CREATE TABLE folder_stats (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    folder_id UUID NOT NULL,
    user_id UUID NOT NULL,
    total_cards_count INTEGER NOT NULL DEFAULT 0,
    due_cards_count INTEGER NOT NULL DEFAULT 0,
    new_cards_count INTEGER NOT NULL DEFAULT 0,
    mature_cards_count INTEGER NOT NULL DEFAULT 0,
    last_computed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Keys
    CONSTRAINT fk_folder_stats_folder FOREIGN KEY (folder_id)
        REFERENCES folders(id) ON DELETE CASCADE,
    CONSTRAINT fk_folder_stats_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,

    -- Constraints
    CONSTRAINT chk_total_cards CHECK (total_cards_count >= 0),
    CONSTRAINT chk_due_cards CHECK (due_cards_count >= 0),
    CONSTRAINT chk_new_cards CHECK (new_cards_count >= 0),
    CONSTRAINT chk_mature_cards CHECK (mature_cards_count >= 0)
);

-- Indexes for folder_stats
CREATE INDEX idx_folder_stats_folder ON folder_stats (folder_id, user_id);
CREATE INDEX idx_folder_stats_lookup ON folder_stats (folder_id, user_id, last_computed_at DESC);
CREATE UNIQUE INDEX idx_folder_stats_unique ON folder_stats (folder_id, user_id);

-- Comments for folder_stats
COMMENT ON TABLE folder_stats IS 'Cached folder statistics with TTL = 5 minutes (performance optimization)';
COMMENT ON COLUMN folder_stats.total_cards_count IS 'Total cards in folder and descendants (recursive)';
COMMENT ON COLUMN folder_stats.due_cards_count IS 'Due cards today';
COMMENT ON COLUMN folder_stats.new_cards_count IS 'New cards (review_count = 0)';
COMMENT ON COLUMN folder_stats.mature_cards_count IS 'Mature cards (current_box >= 5)';
COMMENT ON COLUMN folder_stats.last_computed_at IS 'Last computation timestamp (TTL = 5 minutes)';
