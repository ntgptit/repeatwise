-- RepeatWise Database Migration
-- Version: V3
-- Description: Create folders table for hierarchical folder organization
-- Requirements: UC-005 to UC-010 - Folder Management
-- Date: 2025-01-18

-- Create folders table
CREATE TABLE IF NOT EXISTS folders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    parent_folder_id UUID,
    depth INTEGER NOT NULL DEFAULT 0,
    path VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,

    -- Foreign keys
    CONSTRAINT fk_folders_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_folders_parent
        FOREIGN KEY (parent_folder_id) REFERENCES folders(id) ON DELETE CASCADE,

    -- Constraints
    CONSTRAINT chk_name_not_empty CHECK (TRIM(name) != ''),
    CONSTRAINT chk_depth_max CHECK (depth >= 0 AND depth <= 10),
    CONSTRAINT chk_path_format CHECK (path ~ '^(/[0-9a-f-]{36})+$')
);

-- Create indexes for performance optimization
CREATE INDEX IF NOT EXISTS idx_folders_user
    ON folders(user_id)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_folders_parent
    ON folders(user_id, parent_folder_id)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_folders_path
    ON folders(user_id, path)
    WHERE deleted_at IS NULL;

-- Business rule: Folder name unique within parent (per user)
CREATE UNIQUE INDEX IF NOT EXISTS idx_folders_name_parent
    ON folders(user_id, COALESCE(parent_folder_id::TEXT, 'ROOT'), name)
    WHERE deleted_at IS NULL;

-- Add comments for documentation
COMMENT ON TABLE folders IS 'Hierarchical folder structure with max 10 levels depth';
COMMENT ON COLUMN folders.user_id IS 'Owner of this folder';
COMMENT ON COLUMN folders.name IS 'Folder name (unique within same parent per user)';
COMMENT ON COLUMN folders.description IS 'Optional folder description';
COMMENT ON COLUMN folders.parent_folder_id IS 'Parent folder ID (NULL for root-level folders)';
COMMENT ON COLUMN folders.depth IS 'Depth level in hierarchy (0=root, max=10)';
COMMENT ON COLUMN folders.path IS 'Materialized path for fast descendant queries (/uuid1/uuid2/uuid3)';
COMMENT ON COLUMN folders.deleted_at IS 'Soft delete timestamp (NULL = active)';
