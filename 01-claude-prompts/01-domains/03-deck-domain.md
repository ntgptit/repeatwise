# Deck Domain (Claude Brief)

**Sources**
- `00_docs/02-system-analysis/domain-model.md` (Deck entity)
- Use cases: `UC-011` Create deck, `UC-012` Move deck, `UC-013` Copy deck, `UC-014` Delete deck
- Detail design: `00_docs/04-detail-design/01-entity-specifications.md` (Deck), `03-business-logic-flows.md` (Deck operations)

## Responsibilities
- Represent a collection of cards owned by one user and located in exactly one folder (nullable for root-level decks).
- Support CRUD, move between folders, copy (within same user), and soft delete.
- Provide deck-level statistics (card count, due count) surfaced via folder/deck listings.

## Key entities & invariants
- `Deck`: `id`, `user_id`, `folder_id (nullable)`, `name`, `description`, `is_deleted`, `new_card_limit`, `review_limit` (overrides optional), timestamps.
- Name must be unique within the same parent folder; deck cannot exceed 10,000 cards in MVP (see `system-spec.md` capacity limits).
- Moving or copying a deck must preserve card order, SRS positions, and associated settings.

## Implementation checkpoints
1. **Create deck** – validate folder ownership and ensure deck count per folder is within limits; optionally create deck-specific SRS overrides.
2. **Move deck** – update folder reference; if moving to root, set `folder_id=null`; adjust breadcrumb caches.
3. **Copy deck** – duplicate deck metadata, copy cards + SRS positions via background job when >500 cards.
4. **Delete deck** – soft delete deck and cascade to cards/SRS positions, update stats caches.

## Claude usage tips
- For DTO payloads, fetch deck sections from `04-detail-design/02-api-request-response-specs.md`.
- When Claude suggests cross-user sharing, remind it the MVP is single-tenant per user.
- Use the `05-validation-rules.md` deck section to enforce name length (max 100 chars) and description length (max 500 chars).
