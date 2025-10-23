# Use Case Map – Import & Export

**Use cases**: `UC-015` Import cards, `UC-016` Export cards.

## Flow → API → UI
- Upload file → POST `/api/imports/cards` (multipart) → Web `ImportWizardStepUpload`, Mobile simplified upload.
- Validate preview → GET `/api/imports/{jobId}` to fetch validation results.
- Confirm import → POST `/api/imports/{jobId}/confirm`.
- Check status → GET `/api/imports/{jobId}/status`.
- Export → POST `/api/exports/cards` (scope) → Poll `/api/exports/{jobId}` → Download link.

## Data touchpoints
- Entities: `ImportJob`, `ImportJobItem`, `ExportJob`.
- DTOs: `ImportUploadResponse`, `ImportPreviewResponse`, `JobStatusResponse`.

## Acceptance highlights
- Import remains pending until user confirms after preview.
- Job errors include row numbers/messages; UI must render table with up to 200 errors.
- Export should respect folder/deck filters and timezone.

## Claude tips
- Provide only job status schema to keep prompts concise.
- Remind Claude to handle async flows via polling intervals (3s default) and to clean temp files on completion.
