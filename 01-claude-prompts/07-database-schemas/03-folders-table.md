# Table – folders

**Columns**
- `id UUID PK`
- `user_id UUID FK users`
- `parent_id UUID FK folders(id)` nullable
- `name VARCHAR(100) NOT NULL`
- `materialized_path VARCHAR(1024) NOT NULL`
- `depth SMALLINT NOT NULL`
- `is_deleted BOOLEAN NOT NULL DEFAULT FALSE`
- `created_at`, `updated_at`

**Indexes**
- `idx_folders_user_parent` (user_id, parent_id, is_deleted)
- `idx_folders_materialized_path`

**Notes**
- Enforce depth ≤10; materialized path format `/parent/.../id/`.
- Soft delete used; filter by `is_deleted=false` in queries.

**Claude tips**
- When moving folders, update `materialized_path` and `depth` for descendants.
