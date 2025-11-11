-- V14: Insert sample SRS settings
-- Purpose: Seed SRS configurations tied to sample users

INSERT INTO srs_settings (
    id,
    user_id,
    total_boxes,
    review_order,
    notification_enabled,
    notification_time,
    forgotten_card_action,
    move_down_boxes,
    new_cards_per_day,
    max_reviews_per_day,
    created_at,
    updated_at
)
SELECT
    '71111111-1111-1111-1111-111111111111'::uuid,
    u.id,
    7,
    'RANDOM',
    TRUE,
    TIME '08:30',
    'MOVE_DOWN_N_BOXES',
    2,
    15,
    150,
    CURRENT_TIMESTAMP - INTERVAL '25 days',
    CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM users u
WHERE u.username = 'alice'
UNION ALL
SELECT
    '72222222-2222-2222-2222-222222222222'::uuid,
    u.id,
    7,
    'ASCENDING',
    FALSE,
    TIME '07:45',
    'MOVE_TO_BOX_1',
    1,
    20,
    120,
    CURRENT_TIMESTAMP - INTERVAL '18 days',
    CURRENT_TIMESTAMP - INTERVAL '3 days'
FROM users u
WHERE u.username = 'bao'
ON CONFLICT (user_id) DO UPDATE
SET
    review_order = EXCLUDED.review_order,
    notification_enabled = EXCLUDED.notification_enabled,
    notification_time = EXCLUDED.notification_time,
    forgotten_card_action = EXCLUDED.forgotten_card_action,
    move_down_boxes = EXCLUDED.move_down_boxes,
    new_cards_per_day = EXCLUDED.new_cards_per_day,
    max_reviews_per_day = EXCLUDED.max_reviews_per_day,
    updated_at = EXCLUDED.updated_at;

