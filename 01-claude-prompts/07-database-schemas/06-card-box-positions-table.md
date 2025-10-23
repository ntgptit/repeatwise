# Table – card_box_positions

**Columns**
- `card_id UUID PK FK cards`
- `user_id UUID PK FK users`
- `current_box SMALLINT NOT NULL`
- `interval_days SMALLINT NOT NULL`
- `due_date DATE NOT NULL`
- `last_reviewed_at TIMESTAMP NULL`
- `streak_count SMALLINT NOT NULL DEFAULT 0`
- `is_active BOOLEAN NOT NULL DEFAULT TRUE`

**Indexes**
- `idx_card_box_user_due` (user_id, due_date, is_active)
- `idx_card_box_user_box` (user_id, current_box)

**Notes**
- Composite PK `(card_id, user_id)` ensures one position per user.
- When card deleted, set `is_active=false`.

**Claude tips**
- Use these indexes when querying due cards.
