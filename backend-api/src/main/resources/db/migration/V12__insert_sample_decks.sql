-- V12: Insert sample decks
-- Purpose: Seed decks associated with existing folders and users

-- Deck for Alice in JLPT folder
INSERT INTO decks (
    id,
    user_id,
    folder_id,
    name,
    description,
    created_at,
    updated_at
)
SELECT
    '51111111-1111-1111-1111-111111111111'::uuid,
    u.id,
    f.id,
    'Tu vung theo chu de',
    'Nhom tu vung JLPT N5 theo cac chu de quen thuoc',
    CURRENT_TIMESTAMP - INTERVAL '30 days',
    CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM users u
JOIN folders f ON f.user_id = u.id AND f.name = 'JLPT N5' AND f.deleted_at IS NULL
WHERE u.username = 'alice'
ON CONFLICT (id) DO UPDATE
SET
    user_id = EXCLUDED.user_id,
    folder_id = EXCLUDED.folder_id,
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    updated_at = EXCLUDED.updated_at;

-- Deck for Bao at root level
INSERT INTO decks (
    id,
    user_id,
    folder_id,
    name,
    description,
    created_at,
    updated_at
)
SELECT
    '52222222-2222-2222-2222-222222222222'::uuid,
    u.id,
    NULL,
    'Collocations',
    'Cac cum tu thong dung trong IELTS Writing',
    CURRENT_TIMESTAMP - INTERVAL '20 days',
    CURRENT_TIMESTAMP - INTERVAL '3 days'
FROM users u
WHERE u.username = 'bao'
ON CONFLICT (id) DO UPDATE
SET
    user_id = EXCLUDED.user_id,
    folder_id = EXCLUDED.folder_id,
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    updated_at = EXCLUDED.updated_at;

