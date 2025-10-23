# API – Reviews

**Source**: `00_docs/03-design/api/review-apis.md`.

## Endpoints
- `GET /api/reviews/session` – parameters: `scope` (all/folder/deck), optional `limit`. Returns queue with cards + sessionId.
- `POST /api/reviews/{sessionId}/cards/{cardId}` – submit rating & optional `elapsedSeconds`.
- `POST /api/reviews/{sessionId}/complete` – finalise session, returns summary.
- `GET /api/reviews/cram` / `POST /api/reviews/cram` – cram mode flows.
- `GET /api/reviews/random` / `POST /api/reviews/random` – random mode flows.

## Notes
- Responses include `currentBox`, `nextDueDate`, `sessionProgress`.
- Errors: `REVIEW_QUEUE_EMPTY`, `REVIEW_SESSION_EXPIRED`.

## Claude tips
- Include rating options table when necessary (Again/Hard/Good/Easy).
- Remind Claude to send timezone header so backend schedules correctly.
