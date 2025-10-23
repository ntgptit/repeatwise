# API – Import & Export

**Source**: `00_docs/03-design/api/import-export-apis.md`.

## Endpoints
- `POST /api/imports/cards` – upload file. Returns job `{ jobId, status, totalRows }`.
- `GET /api/imports/{jobId}` – validation preview (errors with row numbers).
- `POST /api/imports/{jobId}/confirm` – start persistence.
- `GET /api/imports/{jobId}/status` – poll job progress.
- `POST /api/exports/cards` – create export job.
- `GET /api/exports/{jobId}` – download link when ready.

## Notes
- Import job statuses: `UPLOADED`, `VALIDATING`, `READY`, `PROCESSING`, `COMPLETED`, `FAILED`.
- Export job includes `downloadUrl` + expiry timestamp.

## Claude tips
- Mention file size/row limits from validation prompt when relevant.
- Encourage Claude to implement exponential backoff when polling to save API calls.
