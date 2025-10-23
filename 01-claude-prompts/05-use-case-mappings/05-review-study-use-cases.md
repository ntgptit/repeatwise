# Use Case Map – Review & Study Modes

**Use cases**: `UC-019` SRS review, `UC-020` Cram mode, `UC-021` Random mode.

## Flow → API → UI
- Load SRS session → GET `/api/reviews/session?scope=...` → Web `ReviewWorkspace`, Mobile `ReviewScreen`.
- Submit review → POST `/api/reviews/{sessionId}/cards/{cardId}` with `rating`, `elapsedSeconds`.
- Complete session → POST `/api/reviews/{sessionId}/complete` to finalise logs/stats.
- Cram mode → GET `/api/reviews/cram` (scope + count) + POST results (does not change boxes).
- Random mode → GET `/api/reviews/random` (count) + POST results.

## Data touchpoints
- Entities: `CardBoxPosition`, `ReviewLog`, `ReviewSession`, `UserStats`, `FolderStats`.
- Settings: `SrsSettings` for limits/order/forgotten strategy.

## Acceptance highlights
- Queue respects daily limits and review order; when empty, return `204` with message.
- Review response returns updated box + due date for UI feedback.
- Cram/Random sessions record logs but do not update box positions (except optional for random? spec says no).

## Claude tips
- Provide the rating transition summary from SRS prompt to avoid copying entire doc.
- Remind Claude to update stats after each submission and to handle concurrency by locking row or using `FOR UPDATE` queries.
