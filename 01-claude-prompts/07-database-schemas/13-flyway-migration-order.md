# Flyway Migration Order (Claude Prompt)

**Source**: `00_docs/03-design/database/flyway-migration-order.md`.

## Sequence
1. Base schema (`V1__initial.sql`) – users, folders, decks, cards.
2. SRS tables – `card_box_positions`, `review_sessions`, `review_logs`.
3. Settings tables – `srs_settings`, `deck_settings`, `user_notification_settings`.
4. Jobs & import/export tables.
5. Index/constraint adjustments.
6. Seed/reference data (dev only) via repeatable migrations `R__*.sql`.

## Claude tips
- When adding new table, append to latest version and update this list to maintain clarity.
