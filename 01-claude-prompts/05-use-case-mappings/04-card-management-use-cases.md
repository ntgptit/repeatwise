# Use Case Map – Card Management

**Use cases**: `UC-015` Import, `UC-016` Export, `UC-017` Create/Edit, `UC-018` Delete.

## Flow → API → UI
- Create card → POST `/api/decks/{id}/cards` → Web `CardEditor`, Mobile `CardFormModal`.
- Update card → PUT `/api/cards/{id}`.
- Delete card → DELETE `/api/cards/{id}` (soft).
- Import cards → POST `/api/imports/cards` (file upload) → Web `ImportWizard`, Mobile `ImportSheet` (simplified).
- Export cards → POST `/api/exports/cards` (scope) → Provide download link when job complete.

## Data touchpoints
- Entities: `Card`, `CardBoxPosition`, `ReviewLog` (when editing meaningfully).
- DTOs: `CreateCardRequest`, `UpdateCardRequest`, `ImportJobStatus`.
- Validation: front/back required, 2k char limit, import file constraints.

## Acceptance highlights
- Card creation auto seeds Box 1 entry due today.
- Import provides validation preview before commit.
- Export returns 202 with job ID; download once `status=COMPLETED`.

## Claude tips
- When implementing import pipeline, refer to `03-business-logic-flows.md` sections "Card Import".
- UI should poll job status using interval defined in `08-import-export-domain` prompt (default 3s).
