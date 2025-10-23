# API – Statistics

**Source**: `00_docs/03-design/api/statistics-apis.md`.

## Endpoints
- `GET /api/statistics/overview` – returns streaks, cards reviewed/new today, due counts, box distribution.
- `GET /api/statistics/history` – optional timeframe for charts (if defined in spec; confirm before use).
- `GET /api/folders/{id}/stats` – folder-specific metrics.

## Notes
- Responses contain `boxDistribution` array of length 7.
- Cache-control header indicates TTL 300 seconds.

## Claude tips
- When building charts, request only the fields required (avoid streaming entire dataset).
- Remind Claude to handle empty states gracefully when user has no activity.
