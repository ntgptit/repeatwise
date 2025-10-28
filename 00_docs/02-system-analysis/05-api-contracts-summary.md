# API Contracts Summary (MVP)

This is a concise summary of key REST endpoints. Detailed contracts should be maintained in `00_docs/06-api/` and OpenAPI (`openapi-spec.yml`).

## Auth

- POST `/api/auth/register` — Register user
- POST `/api/auth/login` — Login, returns access token + Set‑Cookie(refresh)
- POST `/api/auth/refresh` — Rotate refresh token, return new access token
- POST `/api/auth/logout` — Logout current device (revoke current refresh token)
- POST `/api/auth/logout-all` — Revoke all refresh tokens for user

## Users & Settings

- GET `/api/users/me` — Get profile
- PATCH `/api/users/me` — Update profile (name, timezone, language, theme)
- PATCH `/api/users/me/password` — Change password
- GET `/api/srs-settings` — Get SRS settings
- PATCH `/api/srs-settings` — Update SRS settings

## Folders

- GET `/api/folders?parentId=...` — List folders/decks under a parent (paginated)
- POST `/api/folders` — Create folder (validate depth <= 10, unique per parent)
- PATCH `/api/folders/{folderId}` — Rename/update description
- POST `/api/folders/{folderId}/move` — Move folder (not into self/descendants; depth <= 10)
- POST `/api/folders/{folderId}/copy` — Copy folder (sync/async; <= 500 items)
- DELETE `/api/folders/{folderId}` — Soft delete folder (recursive)
- GET `/api/folders/{folderId}/stats` — Recursive statistics (decks/cards/due)

## Decks

- GET `/api/decks?folderId=...` — List decks (paginated)
- POST `/api/decks` — Create deck (folderId nullable for root)
- GET `/api/decks/{deckId}` — Deck details
- PATCH `/api/decks/{deckId}` — Update deck (name, description)
- POST `/api/decks/{deckId}/move` — Move deck to folder/root
- POST `/api/decks/{deckId}/copy` — Copy deck (sync/async; <= 10,000 cards)
- DELETE `/api/decks/{deckId}` — Soft delete deck

## Cards

- GET `/api/decks/{deckId}/cards?page=...` — List cards (paginated)
- POST `/api/decks/{deckId}/cards` — Create card (front/back)
- GET `/api/cards/{cardId}` — Card details
- PATCH `/api/cards/{cardId}` — Update front/back
- DELETE `/api/cards/{cardId}` — Soft delete card

## Import/Export

- POST `/api/decks/{deckId}/import` — CSV/XLSX import (<= 10,000 rows; async for large)
- GET `/api/decks/{deckId}/export?format=csv|xlsx&scope=ALL|DUE_ONLY` — Export
- GET `/api/jobs/{jobId}` — Job status for async operations

## Review (SRS)

- POST `/api/review/sessions` — Start session (scope: DECK or FOLDER)
- GET `/api/review/sessions/{sessionId}/next` — Get next card (if client pulls)
- POST `/api/review/sessions/{sessionId}/rate` — Rate current card (AGAIN/HARD/GOOD/EASY)
- POST `/api/review/sessions/{sessionId}/undo` — Undo last rating (windowed)
- POST `/api/review/sessions/{sessionId}/skip` — Skip current card (no SRS change)

## Statistics

- GET `/api/users/me/stats` — User KPIs (totals, streak, trends)
- GET `/api/stats/box-distribution?scopeType=ALL|FOLDER|DECK&scopeId=...` — Box counts

## Common Behaviors

- All endpoints (except public auth) require JWT access token.
- Soft‑deleted records are excluded by default.
- Pagination defaults: folders/decks 50 per page, cards 100 per page.
- Validation errors return 400 with field details; 401/403 for authz errors; 404 if not found; 409 for conflicts; 429 for rate limits (future).
