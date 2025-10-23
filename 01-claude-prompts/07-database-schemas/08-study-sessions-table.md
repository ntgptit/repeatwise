# Table – review_sessions

**Columns**
- `id UUID PK`
- `user_id UUID FK users`
- `session_type VARCHAR(20)` (`SRS`, `CRAM`, `RANDOM`)
- `scope_type VARCHAR(20)` (`ALL`, `FOLDER`, `DECK`)
- `scope_id UUID NULL`
- `total_cards INT`
- `completed_cards INT`
- `started_at TIMESTAMP`
- `completed_at TIMESTAMP NULL`

**Indexes**
- `idx_review_sessions_user_started`

**Notes**
- Session created when queue generated; mark `completed_at` when user finishes or aborts.

**Claude tips**
- Use session ID in review APIs to correlate logs.
