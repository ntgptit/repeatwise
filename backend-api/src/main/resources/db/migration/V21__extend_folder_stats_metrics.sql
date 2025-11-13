-- V21: Extend folder_stats with additional metrics for UC-012

ALTER TABLE folder_stats
    ADD COLUMN IF NOT EXISTS total_folders_count INTEGER NOT NULL DEFAULT 0;

ALTER TABLE folder_stats
    ADD COLUMN IF NOT EXISTS total_decks_count INTEGER NOT NULL DEFAULT 0;

ALTER TABLE folder_stats
    ADD COLUMN IF NOT EXISTS learning_cards_count INTEGER NOT NULL DEFAULT 0;

ALTER TABLE folder_stats
    ADD COLUMN IF NOT EXISTS review_cards_count INTEGER NOT NULL DEFAULT 0;

COMMENT ON COLUMN folder_stats.total_folders_count IS 'Total descendant folders (excluding self)';
COMMENT ON COLUMN folder_stats.total_decks_count IS 'Total decks within folder subtree';
COMMENT ON COLUMN folder_stats.learning_cards_count IS 'Cards in learning phase (current_box < 3 and review_count > 0)';
COMMENT ON COLUMN folder_stats.review_cards_count IS 'Cards in review phase (current_box between 3 and 4)';

