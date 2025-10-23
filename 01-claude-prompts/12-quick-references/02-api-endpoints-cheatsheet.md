# API Endpoints Cheatsheet

**Source**: `06-api-specifications/00-api-overview.md`.

| Domain | Endpoint | Notes |
|--------|----------|-------|
| Auth | `POST /api/auth/login` | Returns access token, sets refresh cookie |
| Folders | `POST /api/folders/{id}/copy` | Async job, returns jobId |
| Decks | `POST /api/decks/{id}/move` | Move deck to new folder |
| Cards | `POST /api/decks/{id}/cards` | Create card |
| Reviews | `GET /api/reviews/session` | Load SRS queue |
| Statistics | `GET /api/statistics/overview` | Dashboard data |
| Settings | `PUT /api/settings/srs` | Update SRS defaults |
| Import | `POST /api/imports/cards` | Upload file |
| Export | `GET /api/exports/{jobId}` | Download export |

Use table as quick index before diving into full spec.
