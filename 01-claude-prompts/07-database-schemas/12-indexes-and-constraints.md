# Index & Constraint Strategy (Claude Prompt)

**Source**: `00_docs/03-design/database/indexing-strategy.md`.

## Highlights
- Critical index: `idx_card_box_user_due` to fetch due cards quickly.
- Composite unique constraints: folder name per parent, deck name per folder.
- Soft delete pattern: include `is_deleted` in indexes to filter active records.
- Use `ON DELETE SET NULL` for deck → folder relation when parent deleted.
- Apply check constraints for enum text columns (language, theme, strategies) where supported.

## Claude tips
- Reference this when creating new migrations to keep indexes consistent.
