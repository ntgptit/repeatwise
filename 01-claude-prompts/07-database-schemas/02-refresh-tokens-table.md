# Table – refresh_tokens

**Columns**
- `id UUID PK`
- `user_id UUID FK users(id)`
- `token_hash VARCHAR(255) UNIQUE NOT NULL`
- `expires_at TIMESTAMP NOT NULL`
- `revoked_at TIMESTAMP NULL`
- `created_at`, `updated_at`

**Indexes**
- `idx_refresh_tokens_user_id`

**Notes**
- Store bcrypt hash, never raw token.
- Tokens older than 30 days should be purged via background job.

**Claude tips**
- When revoking, set `revoked_at=NOW()`; queries check both expiry and revoked.
