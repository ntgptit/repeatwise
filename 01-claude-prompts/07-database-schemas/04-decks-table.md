# Table – decks

**Columns**
- `id UUID PK`
- `user_id UUID FK users`
- `folder_id UUID FK folders` nullable (root decks)
- `name VARCHAR(100) NOT NULL`
- `description VARCHAR(500)`
- `is_deleted BOOLEAN NOT NULL DEFAULT FALSE`
- `created_at`, `updated_at`

**Indexes**
- `idx_decks_user_folder` (user_id, folder_id, is_deleted)
- `idx_decks_name_search` (optional text search)

**Notes**
- Deck-specific settings stored in `deck_settings` table.
- Soft delete cascades to cards via application logic.

**Claude tips**
- When moving deck to root, set `folder_id=NULL` but keep `user_id`.
