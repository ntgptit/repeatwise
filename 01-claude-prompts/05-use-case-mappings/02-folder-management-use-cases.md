# Use Case Map – Folder Management

**Use cases**: `UC-005` Create hierarchy, `UC-006` Rename, `UC-007` Move, `UC-008` Copy, `UC-009` Delete, `UC-010` View stats.

## Flow → API → UI
- Create folder → POST `/api/folders` → Web `FolderToolbar`, Mobile `FolderActionsSheet`.
- Rename → PUT `/api/folders/{id}`.
- Move → POST `/api/folders/{id}/move` (body: targetId) – triggers async job if subtree large.
- Copy → POST `/api/folders/{id}/copy` (options include includeCards) – async job with status endpoint.
- Delete → DELETE `/api/folders/{id}` (soft delete).
- List/stats → GET `/api/folders/{id}/children`, `/api/folders/{id}/stats`.

## Data touchpoints
- Entities: `Folder`, `Deck`, `FolderStats`.
- Async queue: `BackgroundJob` entries for copy/move.
- DTOs: `CreateFolderRequest`, `MoveFolderRequest`, `CopyFolderRequest`.

## Acceptance highlights
- Depth capped at 10; UI must prevent invalid moves.
- Copy progress available via `/api/jobs/{jobId}`.
- Stats endpoint aggregates child decks/cards; cached TTL 5m.

## Claude tips
- Quote specific pseudocode from `03-business-logic-flows.md` when implementing move/copy.
- For UI breadcrumbs, check `07-frontend-web-specs.md` section "Library Navigation".
