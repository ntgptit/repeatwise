# API – Folders

**Source**: `00_docs/03-design/api/folder-apis.md`.

## Endpoints
- `GET /api/folders/root` – list root folders & decks.
- `GET /api/folders/{id}` – folder detail + breadcrumbs.
- `GET /api/folders/{id}/children` – paginated children (folders + decks).
- `POST /api/folders` – create folder.
- `PUT /api/folders/{id}` – rename/update.
- `POST /api/folders/{id}/move` – move to new parent.
- `POST /api/folders/{id}/copy` – async copy (returns job info).
- `DELETE /api/folders/{id}` – soft delete.
- `GET /api/folders/{id}/stats` – aggregated metrics.

## Notes
- Request/response schemas include `materializedPath`, `depth`, `childCounts`.
- Async operations return `{ jobId, status }`.

## Claude tips
- Include only the endpoints you’re modifying; reference job polling spec if needed.
