# API Overview (Claude Prompt)

**Source**: `00_docs/03-design/api/api-endpoints-summary.md`.

## Basics
- Base URL: `/api`, version implicit (v1).
- Auth: Bearer JWT for most endpoints; refresh via HttpOnly cookie.
- Content-Type: `application/json` unless noted (import upload uses multipart).
- Pagination: standard params `page`, `size`, `sort`; filtering via `filter` string.
- Error format: `{ errorCode, message, details? }` (see `10-error-responses.md`).

## Resource groups
- `auth`, `users`, `folders`, `decks`, `cards`, `reviews`, `statistics`, `settings`, `imports`, `exports`.

## Claude tips
- Use this overview to orient Claude, then share the specific resource prompt.
- Remind Claude that WebSockets/real-time sync are out of scope for MVP.
