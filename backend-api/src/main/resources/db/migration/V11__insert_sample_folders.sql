-- V11: Insert sample folders
-- Purpose: Seed hierarchical folders for demo users

-- Root folder for Alice
INSERT INTO folders (
    id,
    user_id,
    parent_folder_id,
    name,
    description,
    depth,
    path,
    created_at,
    updated_at
)
SELECT
    '41111111-1111-1111-1111-111111111111'::uuid,
    u.id,
    NULL,
    'SRS Mastery',
    'Thu muc goc chua cac bo the hoc SRS',
    0,
    '/41111111-1111-1111-1111-111111111111',
    CURRENT_TIMESTAMP - INTERVAL '40 days',
    CURRENT_TIMESTAMP - INTERVAL '3 days'
FROM users u
WHERE u.username = 'alice'
ON CONFLICT (id) DO UPDATE
SET
    user_id = EXCLUDED.user_id,
    parent_folder_id = EXCLUDED.parent_folder_id,
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    depth = EXCLUDED.depth,
    path = EXCLUDED.path,
    updated_at = EXCLUDED.updated_at;

-- Child folder for Alice
INSERT INTO folders (
    id,
    user_id,
    parent_folder_id,
    name,
    description,
    depth,
    path,
    created_at,
    updated_at
)
SELECT
    '41111111-1111-1111-1111-111111111112'::uuid,
    u.id,
    f.id,
    'JLPT N5',
    'Tong hop tu vung va mau cau JLPT N5',
    1,
    '/41111111-1111-1111-1111-111111111111/41111111-1111-1111-1111-111111111112',
    CURRENT_TIMESTAMP - INTERVAL '35 days',
    CURRENT_TIMESTAMP - INTERVAL '2 days'
FROM users u
JOIN folders f ON f.user_id = u.id AND f.name = 'SRS Mastery' AND f.deleted_at IS NULL
WHERE u.username = 'alice'
ON CONFLICT (id) DO UPDATE
SET
    user_id = EXCLUDED.user_id,
    parent_folder_id = EXCLUDED.parent_folder_id,
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    depth = EXCLUDED.depth,
    path = EXCLUDED.path,
    updated_at = EXCLUDED.updated_at;

-- Root folder for Bao
INSERT INTO folders (
    id,
    user_id,
    parent_folder_id,
    name,
    description,
    depth,
    path,
    created_at,
    updated_at
)
SELECT
    '42222222-2222-2222-2222-222222222221'::uuid,
    u.id,
    NULL,
    'IELTS Prep',
    'Tai lieu luyen thi IELTS',
    0,
    '/42222222-2222-2222-2222-222222222221',
    CURRENT_TIMESTAMP - INTERVAL '28 days',
    CURRENT_TIMESTAMP - INTERVAL '5 days'
FROM users u
WHERE u.username = 'bao'
ON CONFLICT (id) DO UPDATE
SET
    user_id = EXCLUDED.user_id,
    parent_folder_id = EXCLUDED.parent_folder_id,
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    depth = EXCLUDED.depth,
    path = EXCLUDED.path,
    updated_at = EXCLUDED.updated_at;

