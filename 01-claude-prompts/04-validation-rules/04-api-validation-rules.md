# API-Level Validation (Claude Prompt)

**Source**: `00_docs/04-detail-design/05-validation-rules.md` §4 + `02-api-request-response-specs.md`.

## Highlights
- Use consistent error format: `{ "errorCode": "FIELD_REQUIRED", "message": "..." }` with HTTP 400/422 as defined in `06-error-handling-specs.md`.
- Ensure pagination params default to `page=0`, `size=20`; enforce `size` 1–100.
- Sorting fields allow whitelist per endpoint (e.g. folder list: `name`, `createdAt`). Reject unsupported values with `INVALID_SORT`.
- Filter syntax uses `filter=field:value`; parse via reusable helper.
- Authentication endpoints respond with 401/403 using codes `AUTH_INVALID_CREDENTIALS`, `AUTH_TOKEN_EXPIRED`.
- Import/export endpoints return 202 for async processing with job status payloads.

## Claude tips
- Always mention the error codes when asking Claude to implement validations—prevents it from inventing new codes.
- Provide only the relevant section of the API spec to avoid long prompts.
