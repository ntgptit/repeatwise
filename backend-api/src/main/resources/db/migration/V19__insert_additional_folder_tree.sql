-- V19: Insert additional folders for tree structure
-- Purpose: Expand folder hierarchy for richer demo scenarios

-- Alice - JLPT subfolders
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
    '41111111-1111-1111-1111-111111111113'::uuid,
    u.id,
    parent_folder.id,
    'Grammar Basics',
    'Ngu phap can ban JLPT N5',
    2,
    parent_folder.path || '/41111111-1111-1111-1111-111111111113',
    CURRENT_TIMESTAMP - INTERVAL '32 days',
    CURRENT_TIMESTAMP - INTERVAL '5 days'
FROM users u
JOIN folders parent_folder
    ON parent_folder.user_id = u.id
   AND parent_folder.name = 'JLPT N5'
   AND parent_folder.deleted_at IS NULL
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
    '41111111-1111-1111-1111-111111111114'::uuid,
    u.id,
    parent_folder.id,
    'Vocabulary Themes',
    'Chu de tu vung theo linh vuc',
    2,
    parent_folder.path || '/41111111-1111-1111-1111-111111111114',
    CURRENT_TIMESTAMP - INTERVAL '31 days',
    CURRENT_TIMESTAMP - INTERVAL '4 days'
FROM users u
JOIN folders parent_folder
    ON parent_folder.user_id = u.id
   AND parent_folder.name = 'JLPT N5'
   AND parent_folder.deleted_at IS NULL
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

-- Alice - Grammar child folder
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
    '41111111-1111-1111-1111-111111111115'::uuid,
    u.id,
    parent_folder.id,
    'Lessons 1-5',
    'Ngu phap bai 1 den 5',
    3,
    parent_folder.path || '/41111111-1111-1111-1111-111111111115',
    CURRENT_TIMESTAMP - INTERVAL '28 days',
    CURRENT_TIMESTAMP - INTERVAL '2 days'
FROM users u
JOIN folders parent_folder
    ON parent_folder.user_id = u.id
   AND parent_folder.id = '41111111-1111-1111-1111-111111111113'::uuid
   AND parent_folder.deleted_at IS NULL
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

-- Bao - IELTS subfolders
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
    '42222222-2222-2222-2222-222222222222'::uuid,
    u.id,
    root_folder.id,
    'Writing Task 2',
    'De cuong va ideas cho Writing Task 2',
    1,
    root_folder.path || '/42222222-2222-2222-2222-222222222222',
    CURRENT_TIMESTAMP - INTERVAL '18 days',
    CURRENT_TIMESTAMP - INTERVAL '3 days'
FROM users u
JOIN folders root_folder
    ON root_folder.user_id = u.id
   AND root_folder.name = 'IELTS Prep'
   AND root_folder.deleted_at IS NULL
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
    '42222222-2222-2222-2222-222222222223'::uuid,
    u.id,
    child_folder.id,
    'Band 7 Samples',
    'Bai mau dat band 7',
    2,
    child_folder.path || '/42222222-2222-2222-2222-222222222223',
    CURRENT_TIMESTAMP - INTERVAL '15 days',
    CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM users u
JOIN folders child_folder
    ON child_folder.user_id = u.id
   AND child_folder.name = 'Writing Task 2'
   AND child_folder.deleted_at IS NULL
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

