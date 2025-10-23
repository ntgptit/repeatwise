# Use Case Map – Deck Management

**Use cases**: `UC-011` Create deck, `UC-012` Move deck, `UC-013` Copy deck, `UC-014` Delete deck.

## Flow → API → UI
- Create deck → POST `/api/decks` → Web `DeckCreateDialog`, Mobile `DeckCreateSheet`.
- Move deck → POST `/api/decks/{id}/move`.
- Copy deck → POST `/api/decks/{id}/copy` (async job similar to folder copy).
- Delete deck → DELETE `/api/decks/{id}`.
- List decks → GET `/api/folders/{id}/decks` with pagination.

## Data touchpoints
- Entities: `Deck`, `Card`, `CardBoxPosition` (for copy), `DeckSettings` overrides.
- DTOs: `CreateDeckRequest`, `MoveDeckRequest`, `CopyDeckRequest`.
- Validation: name uniqueness within folder, description length, limit overrides.

## Acceptance highlights
- Copy includes all cards + SRS positions; job returns status.
- Move must update breadcrumbs/stats; ensure due counts recalc.
- Delete is soft; cards remain accessible via trash (future) but flagged `is_deleted`.

## Claude tips
- Use `03-business-logic-flows.md` deck sections when coding service.
- Frontend should refresh TanStack Query caches for folder + deck lists after operations.
