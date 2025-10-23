# API – Cards

**Source**: `00_docs/03-design/api/card-apis.md`.

## Endpoints
- `GET /api/decks/{id}/cards` – paginated list with filter by status (due/new) and search.
- `GET /api/cards/{id}` – card detail including box info.
- `POST /api/decks/{id}/cards` – create card.
- `PUT /api/cards/{id}` – update front/back.
- `DELETE /api/cards/{id}` – soft delete.
- `POST /api/imports/cards` – upload file (delegates to import job).
- `POST /api/exports/cards` – create export job.

## Notes
- Responses include `boxNumber`, `dueDate`, `lastReviewedAt` for convenience.
- Import/export handled asynchronously—see job endpoints for polling.

## Claude tips
- Provide only the relevant request/response schema to keep tokens low.
- Remind Claude that rich text fields are not supported; treat content as plain text.
