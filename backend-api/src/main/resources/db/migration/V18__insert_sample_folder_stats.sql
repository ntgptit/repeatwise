-- V18: Insert sample folder statistics
-- Purpose: Seed cached folder stats for demo dashboards

INSERT INTO folder_stats (
    id,
    folder_id,
    user_id,
    total_cards_count,
    due_cards_count,
    new_cards_count,
    mature_cards_count,
    last_computed_at
)
SELECT
    'b1111111-1111-1111-1111-111111111111'::uuid,
    f.id,
    u.id,
    2,
    1,
    1,
    1,
    CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM folders f
JOIN users u ON u.id = f.user_id AND u.username = 'alice'
WHERE f.name = 'SRS Mastery' AND f.deleted_at IS NULL
UNION ALL
SELECT
    'b1111111-1111-1111-1111-111111111112'::uuid,
    f.id,
    u.id,
    2,
    1,
    1,
    1,
    CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM folders f
JOIN users u ON u.id = f.user_id AND u.username = 'alice'
WHERE f.name = 'JLPT N5' AND f.deleted_at IS NULL
UNION ALL
SELECT
    'b2222222-2222-2222-2222-222222222221'::uuid,
    f.id,
    u.id,
    1,
    0,
    0,
    0,
    CURRENT_TIMESTAMP - INTERVAL '2 days'
FROM folders f
JOIN users u ON u.id = f.user_id AND u.username = 'bao'
WHERE f.name = 'IELTS Prep' AND f.deleted_at IS NULL
ON CONFLICT (folder_id, user_id) DO UPDATE
SET
    total_cards_count = EXCLUDED.total_cards_count,
    due_cards_count = EXCLUDED.due_cards_count,
    new_cards_count = EXCLUDED.new_cards_count,
    mature_cards_count = EXCLUDED.mature_cards_count,
    last_computed_at = EXCLUDED.last_computed_at;

