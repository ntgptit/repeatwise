ALTER TABLE folders
    ADD COLUMN sort_order INTEGER NOT NULL DEFAULT 0;

WITH ordered_folders AS (
    SELECT
        id,
        ROW_NUMBER() OVER (PARTITION BY parent_folder_id ORDER BY created_at, id) - 1 AS rn
    FROM folders
)
UPDATE folders f
SET sort_order = ordered_folders.rn
FROM ordered_folders
WHERE f.id = ordered_folders.id;
