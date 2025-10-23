# DTO Validation Rules (Claude Prompt)

**Source**: `00_docs/04-detail-design/05-validation-rules.md` §1 + `02-api-request-response-specs.md`.

## Key DTOs
- **Auth**: `RegisterRequest` requires `email`, `password`, `name`; `LoginRequest` requires `email`, `password`. Both trim email, do not trim password.
- **Folder/Deck**: Create/rename payloads require `name`; move/copy require valid target IDs; copy allows `includeSubfolders` boolean.
- **Card**: `CreateCardRequest` requires `front`, `back`; optional hints; update uses same constraints.
- **Review**: `SubmitReviewRequest` requires `cardId`, `rating` enum, optional `elapsedSeconds` (0–600).
- **Settings**: `UpdateSrsSettingsRequest` ensures limits within ranges; `UpdateNotificationSettingsRequest` enforces time format `HH:mm`.
- **Import**: `UploadImportRequest` ensures file present, MIME type `text/csv` or `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`.

## Claude tips
- Map DTO validations to class-validator (TS) or Yup/Zod schemas on frontend; keep messages aligned with backend for consistency.
- Backend should use `@Valid` + custom validators when range checks exceed simple annotations.
