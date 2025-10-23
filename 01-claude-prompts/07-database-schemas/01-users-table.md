# Table – users

**Columns**
- `id UUID PK`
- `email VARCHAR(255) UNIQUE NOT NULL` (stored lowercase)
- `password_hash VARCHAR(255) NOT NULL`
- `name VARCHAR(100) NOT NULL`
- `language VARCHAR(2) NOT NULL DEFAULT 'VI'`
- `theme VARCHAR(10) NOT NULL DEFAULT 'SYSTEM'`
- `timezone VARCHAR(100) NOT NULL DEFAULT 'Asia/Ho_Chi_Minh'`
- `created_at`, `updated_at`

**Indexes**
- `users_email_uindex`

**Relationships**
- 1—1 `srs_settings`, `user_stats`, `user_notification_settings`
- 1—N `refresh_tokens`, `folders`, `decks`

**Claude tips**
- Keep column names snake_case; align with JPA `@Column(name = "password_hash")`.
