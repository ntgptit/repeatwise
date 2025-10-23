# Card Domain (Claude Brief)

**Sources**
- `00_docs/02-system-analysis/domain-model.md` (Card entity)
- Use cases: `UC-015` Import, `UC-016` Export, `UC-017` Create/Edit, `UC-018` Delete
- Detail specs: `00_docs/04-detail-design/01-entity-specifications.md` (Card + CardContent), `02-api-request-response-specs.md` (card DTOs), `05-validation-rules.md` (card fields)

## Responsibilities
- Manage flashcard content (front/back plain text) scoped to a deck.
- Provide CRUD plus bulk import/export through CSV/Excel.
- Coordinate with SRS domain for scheduling (card creation seeds `CardBoxPosition` in Box 1 with due date = today).

## Key entities & invariants
- `Card`: `id`, `deck_id`, `front`, `back`, `tags (future)`, `is_deleted`, timestamps.
- Plain text only; front/back required, trimmed, max length 2000 characters; duplicates allowed but flagged in import validation.
- Import expects template columns (`front`, `back`, optional `hint`, `extraNotes` reserved for future).

## Implementation checkpoints
1. **Create/Edit** – apply validation, update `CardBoxPosition` only when content meaningfully changes (see SRS doc for triggers).
2. **Delete** – soft delete card and mark `CardBoxPosition.is_active=false`; stats update required.
3. **Import** – parse file, validate rows, stage preview, persist via batch job; on success, create cards + SRS positions.
4. **Export** – filter by folder/deck selection, include due date + box number for user reference.

## Claude usage tips
- Pull only the specific validation tables for card fields from `05-validation-rules.md` when coding forms.
- For import/export formats, quote the relevant sections from `04-detail-design/02-api-request-response-specs.md`.
- Remind Claude not to introduce rich text/media support—explicitly deferred in MVP scope.
