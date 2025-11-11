-- V13: Insert sample cards
-- Purpose: Seed cards linked to sample decks

INSERT INTO cards (
    id,
    deck_id,
    front,
    back,
    created_at,
    updated_at
)
SELECT
    '61111111-1111-1111-1111-111111111111'::uuid,
    d.id,
    'Ohayou gozaimasu',
    'Chao buoi sang',
    CURRENT_TIMESTAMP - INTERVAL '25 days',
    CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM decks d
WHERE d.id = '51111111-1111-1111-1111-111111111111'::uuid
UNION ALL
SELECT
    '61111111-1111-1111-1111-111111111112'::uuid,
    d.id,
    'Arigatou gozaimasu',
    'Xin cam on (lich su)',
    CURRENT_TIMESTAMP - INTERVAL '22 days',
    CURRENT_TIMESTAMP - INTERVAL '2 days'
FROM decks d
WHERE d.id = '51111111-1111-1111-1111-111111111111'::uuid
UNION ALL
SELECT
    '62222222-2222-2222-2222-222222222222'::uuid,
    d.id,
    'to make progress',
    'dat tien bo; cai thien dan dan',
    CURRENT_TIMESTAMP - INTERVAL '18 days',
    CURRENT_TIMESTAMP - INTERVAL '5 days'
FROM decks d
WHERE d.id = '52222222-2222-2222-2222-222222222222'::uuid
ON CONFLICT (id) DO UPDATE
SET
    deck_id = EXCLUDED.deck_id,
    front = EXCLUDED.front,
    back = EXCLUDED.back,
    updated_at = EXCLUDED.updated_at;

