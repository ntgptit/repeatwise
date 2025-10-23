# API – Error Responses

**Source**: `00_docs/03-design/api/error-responses.md` & `04-detail-design/06-error-handling-specs.md`.

## Format
```json
{
  "timestamp": "2025-01-10T10:00:00Z",
  "path": "/api/...",
  "errorCode": "FIELD_REQUIRED",
  "message": "Human readable",
  "details": ["optional context"]
}
```

## Common codes
- `FIELD_REQUIRED`, `FIELD_TOO_LONG`, `INVALID_FORMAT`
- `AUTH_INVALID_CREDENTIALS`, `AUTH_TOKEN_EXPIRED`, `AUTH_FORBIDDEN`
- `RESOURCE_NOT_FOUND`, `FOLDER_DEPTH_LIMIT`, `IMPORT_TOO_LARGE`
- `INTERNAL_ERROR` for unhandled exceptions (logged server-side)

## Claude tips
- Always reference this file when adding new errors—avoid inventing codes.
- Mention the exact code in prompts so Claude uses the canonical one.
