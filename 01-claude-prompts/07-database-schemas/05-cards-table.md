# Table – cards

**Columns**
- `id UUID PK`
- `deck_id UUID FK decks`
- `front TEXT NOT NULL`
- `back TEXT NOT NULL`
- `notes TEXT NULL` (future use)
- `is_deleted BOOLEAN NOT NULL DEFAULT FALSE`
- `created_at`, `updated_at`

**Indexes**
- `idx_cards_deck_id`
- `idx_cards_is_deleted`

**Notes**
- Plain text only; consider trigram index for search (future).
- SRS data stored in `card_box_positions`.

**Claude tips**
- When deleting, mark `is_deleted` and cascade updates to `card_box_positions`.
