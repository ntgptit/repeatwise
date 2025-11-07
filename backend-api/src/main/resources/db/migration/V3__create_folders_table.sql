-- V3: Create folders table
-- Purpose: Hierarchical folder structure for organizing decks

CREATE TABLE folders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    parent_folder_id UUID,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    depth INTEGER NOT NULL DEFAULT 0,
    path VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,

    -- Foreign Keys
    CONSTRAINT fk_folders_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_folders_parent FOREIGN KEY (parent_folder_id)
        REFERENCES folders(id) ON DELETE CASCADE,

    -- Constraints
    CONSTRAINT chk_name_not_empty CHECK (TRIM(name) <> ''),
    CONSTRAINT chk_depth_max CHECK (depth >= 0 AND depth <= 10),
    CONSTRAINT chk_path_format CHECK (path ~* '^(/[0-9a-f-]{36})+$')
);

-- Indexes
CREATE INDEX idx_folders_user ON folders (user_id)
    WHERE deleted_at IS NULL;
CREATE INDEX idx_folders_parent ON folders (user_id, parent_folder_id)
    WHERE deleted_at IS NULL;
CREATE INDEX idx_folders_path ON folders (user_id, path)
    WHERE deleted_at IS NULL;
CREATE UNIQUE INDEX idx_folders_name_parent ON folders (
    user_id,
    COALESCE(parent_folder_id::TEXT, 'ROOT'),
    LOWER(name)
) WHERE deleted_at IS NULL;

-- Comments
COMMENT ON TABLE folders IS 'Hierarchical folder structure for organizing decks';
COMMENT ON COLUMN folders.parent_folder_id IS 'Parent folder reference (NULL = root level)';
COMMENT ON COLUMN folders.depth IS 'Tree depth (0 = root level, max 10)';
COMMENT ON COLUMN folders.path IS 'Materialized path for efficient hierarchy queries (e.g., /uuid1/uuid2/uuid3)';
COMMENT ON COLUMN folders.deleted_at IS 'Soft delete timestamp';
COMMENT ON INDEX idx_folders_path IS 'Critical for fast descendant queries using materialized path pattern';
COMMENT ON INDEX idx_folders_name_parent IS 'Enforces unique folder name per parent per user (case-insensitive)';
