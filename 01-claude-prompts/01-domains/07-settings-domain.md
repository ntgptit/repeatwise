# Settings & Notifications Domain (Claude Brief)

**Sources**
- Use cases: `UC-022` Configure SRS settings, `UC-024` Manage notifications
- Specs: `00_docs/04-detail-design/02-api-request-response-specs.md` (settings payloads), `05-validation-rules.md` (settings constraints), `03-design/architecture/backend-detailed-design.md` (notification service)

## Responsibilities
- Manage per-user SRS preferences (daily limits, review order, forgotten-card strategy) and deck overrides.
- Store notification preferences (daily reminder time, timezone alignment, push token registration).
- Provide APIs for toggling reminders and updating device tokens.

## Key entities & invariants
- `SrsSettings`: `newCardsPerDay (1-200)`, `reviewCardsPerDay (10-1000)`, `reviewOrderStrategy` (`ASC`, `DESC`, `RANDOM`), `forgottenCardAction` (`RESET_TO_BOX_ONE`, `MOVE_DOWN_ONE`, `STAY`), `enableCramMode` boolean.
- `UserNotificationSettings`: `dailyReminderEnabled`, `dailyReminderTime`, `pushToken`, `lastSentAt`.
- Reminder time stored in user timezone; validations enforce 05:00–23:00 window.

## Implementation checkpoints
1. **GET/PUT `/settings/srs`** – merge payload with defaults; when limits change, regenerate scheduler caches.
2. **Deck overrides** – allow optional override per deck; fallback to user defaults when null.
3. **Notifications** – API to register/unregister device tokens; schedule cron job to enqueue push notifications using timezone aware calculations.
4. **Audit** – capture `updatedAt` for settings to detect stale configurations.

## Claude usage tips
- Quote only the relevant JSON schema from `02-api-request-response-specs.md` when building forms.
- Remind Claude that SMS/email notifications are out of scope; push only.
- When calculating reminder schedules, reuse helper described in `04-detail-design/03-business-logic-flows.md` (Notification flow).
