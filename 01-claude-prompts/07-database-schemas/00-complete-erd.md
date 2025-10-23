# ERD Overview (Claude Prompt)

**Source**: `00_docs/03-design/database/schema.md`.

## Key aggregates
- **User aggregate**: `users`, `refresh_tokens`, `srs_settings`, `user_stats`, `user_notification_settings`.
- **Content aggregate**: `folders`, `decks`, `cards`, `card_box_positions`, `deck_settings`.
- **Review logging**: `review_sessions`, `review_logs`.
- **Jobs**: `background_jobs`, `import_jobs`, `import_job_items`, `export_jobs`.

## Relationships
- User 1—N folders/decks; folder uses materialized path for hierarchy.
- Deck 1—N cards; card 1—1 card_box_position per user.
- Review session 1—N review logs.

## Claude tips
- Use this overview to pick the specific table prompt you need.
