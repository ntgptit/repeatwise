# API – Decks

**Source**: `00_docs/03-design/api/deck-apis.md`.

## Endpoints
- `GET /api/folders/{id}/decks` – list decks in folder.
- `GET /api/decks/{id}` – deck detail including stats.
- `POST /api/decks` – create deck.
- `PUT /api/decks/{id}` – update name/description/limits.
- `POST /api/decks/{id}/move` – move to another folder or root.
- `POST /api/decks/{id}/copy` – async duplicate.
- `DELETE /api/decks/{id}` – soft delete.
- `GET/PUT /api/decks/{id}/settings` – optional overrides (daily limits, order).

## Notes
- Responses include `cardCount`, `dueCount`, `newCount`, `parentFolderBreadcrumb`.
- Copy returns job info similar to folders.

## Claude tips
- When implementing copy/move, ensure stats endpoints are refreshed; mention this in prompt.
