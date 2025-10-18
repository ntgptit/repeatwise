-- RepeatWise Database Migration
-- Version: V8
-- Description: Create statistics tables for denormalized caching
-- Requirements: UC-010 View Folder Statistics, UC-023 View Statistics
-- Date: 2025-01-18

-- ============================================================
-- Table 1: user_stats - User Progress Statistics
-- ============================================================
CREATE TABLE IF NOT EXISTS user_stats (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    total_cards_learned INTEGER NOT NULL DEFAULT 0,
    streak_days INTEGER NOT NULL DEFAULT 0,
    last_study_date DATE,
    total_study_time_minutes INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign keys
    CONSTRAINT fk_user_stats_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    -- Constraints
    CONSTRAINT chk_total_cards_learned CHECK (total_cards_learned >= 0),
    CONSTRAINT chk_streak_days CHECK (streak_days >= 0),
    CONSTRAINT chk_total_study_time CHECK (total_study_time_minutes >= 0)
);

-- Indexes for user_stats
CREATE UNIQUE INDEX IF NOT EXISTS idx_user_stats_user
    ON user_stats(user_id);

-- ============================================================
-- Table 2: folder_stats - Cached Folder Statistics
-- ============================================================
CREATE TABLE IF NOT EXISTS folder_stats (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    folder_id UUID NOT NULL,
    user_id UUID NOT NULL,
    total_cards_count INTEGER NOT NULL DEFAULT 0,
    due_cards_count INTEGER NOT NULL DEFAULT 0,
    new_cards_count INTEGER NOT NULL DEFAULT 0,
    mature_cards_count INTEGER NOT NULL DEFAULT 0,
    last_computed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign keys
    CONSTRAINT fk_folder_stats_folder
        FOREIGN KEY (folder_id) REFERENCES folders(id) ON DELETE CASCADE,
    CONSTRAINT fk_folder_stats_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    -- Constraints
    CONSTRAINT chk_total_cards CHECK (total_cards_count >= 0),
    CONSTRAINT chk_due_cards CHECK (due_cards_count >= 0),
    CONSTRAINT chk_new_cards CHECK (new_cards_count >= 0),
    CONSTRAINT chk_mature_cards CHECK (mature_cards_count >= 0)
);

-- Indexes for folder_stats
CREATE INDEX IF NOT EXISTS idx_folder_stats_folder
    ON folder_stats(folder_id, user_id);

-- Index for TTL-based cache lookup
CREATE INDEX IF NOT EXISTS idx_folder_stats_lookup
    ON folder_stats(folder_id, user_id, last_computed_at DESC);

-- Unique constraint: one stats row per folder per user
CREATE UNIQUE INDEX IF NOT EXISTS idx_folder_stats_unique
    ON folder_stats(folder_id, user_id);

-- ============================================================
-- Comments for documentation
-- ============================================================
COMMENT ON TABLE user_stats IS 'Denormalized user progress statistics (updated after each review)';
COMMENT ON TABLE folder_stats IS 'Cached folder statistics with 5-minute TTL (performance optimization)';

COMMENT ON COLUMN user_stats.total_cards_learned IS 'Total unique cards reviewed at least once';
COMMENT ON COLUMN user_stats.streak_days IS 'Consecutive days with at least 1 review';
COMMENT ON COLUMN user_stats.last_study_date IS 'Last date user reviewed any card';
COMMENT ON COLUMN user_stats.total_study_time_minutes IS 'Total study time in minutes (estimated)';

COMMENT ON COLUMN folder_stats.total_cards_count IS 'Total cards in folder and all descendants (recursive)';
COMMENT ON COLUMN folder_stats.due_cards_count IS 'Cards due for review today';
COMMENT ON COLUMN folder_stats.new_cards_count IS 'Cards never reviewed (review_count = 0)';
COMMENT ON COLUMN folder_stats.mature_cards_count IS 'Cards in box >= 5 (well-learned)';
COMMENT ON COLUMN folder_stats.last_computed_at IS 'Timestamp of last calculation (TTL = 5 minutes)';
