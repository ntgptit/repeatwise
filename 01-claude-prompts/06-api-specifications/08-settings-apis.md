# API – Settings

**Source**: `00_docs/03-design/api/settings-apis.md`.

## Endpoints
- `GET /api/settings/srs`, `PUT /api/settings/srs` – manage user defaults.
- `GET /api/decks/{id}/settings`, `PUT /api/decks/{id}/settings` – per-deck overrides.
- `GET /api/settings/notifications`, `PUT /api/settings/notifications` – toggle reminders and update time.
- `POST /api/settings/notifications/token` – register push token; `DELETE` to unregister.

## Notes
- Payload includes enums `reviewOrderStrategy`, `forgottenCardAction`.
- Reminder time uses `HH:mm` (24h) string; backend stores as LocalTime with timezone.

## Claude tips
- Provide only the JSON schema needed; emphasise range checks for daily limits.
