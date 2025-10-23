# Use Case Map – Settings & Notifications

**Use cases**: `UC-022` Configure SRS settings, `UC-024` Manage notifications.

## Flow → API → UI
- Fetch/update SRS settings → GET/PUT `/api/settings/srs` → Web `SrsSettingsForm`, Mobile `SettingsScreen`.
- Deck overrides → GET/PUT `/api/decks/{id}/settings` (optional per deck) → Deck detail page.
- Notifications → GET/PUT `/api/settings/notifications`, POST `/api/settings/notifications/token` for push registration.

## Data touchpoints
- Entities: `SrsSettings`, `DeckSettings`, `UserNotificationSettings`.
- DTOs: `UpdateSrsSettingsRequest`, `UpdateNotificationSettingsRequest`, `RegisterPushTokenRequest`.

## Acceptance highlights
- Validate numeric ranges; respond with 400 + error codes if out of bounds.
- Reminder time stored in user timezone; UI must display localised time.
- Disabling notifications should revoke push token.

## Claude tips
- Provide only the JSON schema needed for the form; avoid pasting entire spec.
- Remind Claude to update scheduler caches after settings changes.
