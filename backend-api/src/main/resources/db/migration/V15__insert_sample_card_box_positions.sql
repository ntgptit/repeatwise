-- V15: Insert sample card box positions
-- Purpose: Seed SRS progression state for sample users and cards

INSERT INTO card_box_position (
    id,
    card_id,
    user_id,
    current_box,
    interval_days,
    due_date,
    review_count,
    lapse_count,
    last_reviewed_at,
    created_at,
    updated_at
)
SELECT
    '81111111-1111-1111-1111-111111111111'::uuid,
    c.id,
    u.id,
    3,
    4,
    CURRENT_DATE + 2,
    6,
    1,
    CURRENT_TIMESTAMP - INTERVAL '1 day',
    CURRENT_TIMESTAMP - INTERVAL '25 days',
    CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM cards c
JOIN decks d ON d.id = c.deck_id
JOIN users u ON u.id = d.user_id AND u.username = 'alice'
WHERE c.id = '61111111-1111-1111-1111-111111111111'::uuid
UNION ALL
SELECT
    '81111111-1111-1111-1111-111111111112'::uuid,
    c.id,
    u.id,
    1,
    1,
    CURRENT_DATE,
    0,
    0,
    NULL,
    CURRENT_TIMESTAMP - INTERVAL '22 days',
    CURRENT_TIMESTAMP - INTERVAL '2 days'
FROM cards c
JOIN decks d ON d.id = c.deck_id
JOIN users u ON u.id = d.user_id AND u.username = 'alice'
WHERE c.id = '61111111-1111-1111-1111-111111111112'::uuid
UNION ALL
SELECT
    '82222222-2222-2222-2222-222222222222'::uuid,
    c.id,
    u.id,
    2,
    3,
    CURRENT_DATE + 1,
    2,
    0,
    CURRENT_TIMESTAMP - INTERVAL '3 days',
    CURRENT_TIMESTAMP - INTERVAL '18 days',
    CURRENT_TIMESTAMP - INTERVAL '3 days'
FROM cards c
JOIN decks d ON d.id = c.deck_id
JOIN users u ON u.id = d.user_id AND u.username = 'bao'
WHERE c.id = '62222222-2222-2222-2222-222222222222'::uuid
ON CONFLICT (id) DO UPDATE
SET
    card_id = EXCLUDED.card_id,
    user_id = EXCLUDED.user_id,
    current_box = EXCLUDED.current_box,
    interval_days = EXCLUDED.interval_days,
    due_date = EXCLUDED.due_date,
    review_count = EXCLUDED.review_count,
    lapse_count = EXCLUDED.lapse_count,
    last_reviewed_at = EXCLUDED.last_reviewed_at,
    updated_at = EXCLUDED.updated_at,
    deleted_at = EXCLUDED.deleted_at;

