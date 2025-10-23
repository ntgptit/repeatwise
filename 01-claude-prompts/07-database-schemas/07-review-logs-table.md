# Table – review_logs

**Columns**
- `id UUID PK`
- `review_session_id UUID FK review_sessions`
- `card_id UUID FK cards`
- `user_id UUID FK users`
- `rating VARCHAR(10) NOT NULL`
- `previous_box SMALLINT NOT NULL`
- `new_box SMALLINT NOT NULL`
- `previous_due_date DATE`
- `new_due_date DATE`
- `elapsed_seconds SMALLINT`
- `reviewed_at TIMESTAMP NOT NULL`

**Indexes**
- `idx_review_logs_user_date` (user_id, reviewed_at)
- `idx_review_logs_card` (card_id)

**Notes**
- Used for analytics; keep history even if cards deleted.

**Claude tips**
- When inserting logs, copy values before mutating `card_box_positions`.
