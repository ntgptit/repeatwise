-- V16: Insert sample review logs
-- Purpose: Seed historical review data for analytics demos

INSERT INTO review_logs (
    id,
    card_id,
    user_id,
    rating,
    previous_box,
    new_box,
    interval_days,
    reviewed_at
)
SELECT
    '91111111-1111-1111-1111-111111111111'::uuid,
    c.id,
    u.id,
    'GOOD',
    2,
    3,
    4,
    CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM cards c
JOIN decks d ON d.id = c.deck_id
JOIN users u ON u.id = d.user_id AND u.username = 'alice'
WHERE c.id = '61111111-1111-1111-1111-111111111111'::uuid
UNION ALL
SELECT
    '91111111-1111-1111-1111-111111111112'::uuid,
    c.id,
    u.id,
    'EASY',
    3,
    4,
    6,
    CURRENT_TIMESTAMP - INTERVAL '10 days'
FROM cards c
JOIN decks d ON d.id = c.deck_id
JOIN users u ON u.id = d.user_id AND u.username = 'alice'
WHERE c.id = '61111111-1111-1111-1111-111111111111'::uuid
UNION ALL
SELECT
    '92222222-2222-2222-2222-222222222221'::uuid,
    c.id,
    u.id,
    'HARD',
    1,
    2,
    3,
    CURRENT_TIMESTAMP - INTERVAL '3 days'
FROM cards c
JOIN decks d ON d.id = c.deck_id
JOIN users u ON u.id = d.user_id AND u.username = 'bao'
WHERE c.id = '62222222-2222-2222-2222-222222222222'::uuid
ON CONFLICT (id) DO UPDATE
SET
    card_id = EXCLUDED.card_id,
    user_id = EXCLUDED.user_id,
    rating = EXCLUDED.rating,
    previous_box = EXCLUDED.previous_box,
    new_box = EXCLUDED.new_box,
    interval_days = EXCLUDED.interval_days,
    reviewed_at = EXCLUDED.reviewed_at;

