# Entity Constraints (Claude Prompt)

**Source**: `00_docs/04-detail-design/05-validation-rules.md` §1–2 + `01-entity-specifications.md`.

## Highlights
- **User**: Email unique, <=255 chars, stored lowercase; password hash (bcrypt), timezone valid IANA string, enums limited to `VI/EN`, `LIGHT/DARK/SYSTEM`.
- **Folder**: Name required, trimmed, <=100 chars; depth ≤10; unique per parent; `materialized_path` validated for correct delimiter (`/`).
- **Deck**: Name required, <=100 chars; optional description <=500 chars; uniqueness within folder; `reviewLimit` 10–1000, `newCardLimit` 1–200.
- **Card**: Front/back required, trimmed, <=2000 chars; no HTML; duplicates allowed but flagged during import preview.
- **SrsSettings**: Daily limits within bounds above; strategies must be valid enums.
- **NotificationSettings**: Reminder time between 05:00–23:00 local; push token optional but when present <=255 chars.

## Claude tips
- When writing entity annotations, translate these rules into JPA + Bean Validation (`@Size`, `@Pattern`, `@Enumerated`).
- For migrations, ensure constraints match (e.g. `varchar(255)` for email, check constraints for enums if using text).
