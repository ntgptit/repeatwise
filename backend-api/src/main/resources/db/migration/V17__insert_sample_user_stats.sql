-- V17: Insert sample user statistics
-- Purpose: Seed denormalized stats for demo dashboards

INSERT INTO user_stats (
    id,
    user_id,
    total_cards,
    total_decks,
    total_folders,
    cards_reviewed_today,
    streak_days,
    last_study_date,
    total_study_time_minutes,
    created_at,
    updated_at
)
SELECT
    'a1111111-1111-1111-1111-111111111111'::uuid,
    u.id,
    2,
    1,
    2,
    1,
    5,
    CURRENT_DATE,
    180,
    CURRENT_TIMESTAMP - INTERVAL '30 days',
    CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM users u
WHERE u.username = 'alice'
UNION ALL
SELECT
    'a2222222-2222-2222-2222-222222222222'::uuid,
    u.id,
    1,
    1,
    1,
    0,
    2,
    CURRENT_DATE - 5,
    60,
    CURRENT_TIMESTAMP - INTERVAL '20 days',
    CURRENT_TIMESTAMP - INTERVAL '2 days'
FROM users u
WHERE u.username = 'bao'
ON CONFLICT (user_id) DO UPDATE
SET
    total_cards = EXCLUDED.total_cards,
    total_decks = EXCLUDED.total_decks,
    total_folders = EXCLUDED.total_folders,
    cards_reviewed_today = EXCLUDED.cards_reviewed_today,
    streak_days = EXCLUDED.streak_days,
    last_study_date = EXCLUDED.last_study_date,
    total_study_time_minutes = EXCLUDED.total_study_time_minutes,
    updated_at = EXCLUDED.updated_at;

