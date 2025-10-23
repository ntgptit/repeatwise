# Import & Export Domain (Claude Brief)

**Sources**
- Use cases: `UC-015` Import cards, `UC-016` Export cards, `UC-008` Copy folder (async reuse)
- Detail specs: `00_docs/04-detail-design/02-api-request-response-specs.md` (import/export endpoints + CSV schema), `05-validation-rules.md` (import validation), `03-business-logic-flows.md` (import pipeline)

## Responsibilities
- Allow users to bulk import cards via CSV/Excel template with validation preview.
- Export cards (optionally by folder/deck) including due date and box number.
- Reuse background job infrastructure for long-running imports.

## Key entities & invariants
- `ImportJob`: tracks status (`PENDING`, `VALIDATING`, `FAILED`, `COMPLETED`), total rows, invalid rows, error report link.
- `ExportJob`: similar status; generates downloadable file with timestamp.
- Template columns: `front`, `back`, optional `hint`, `extraNotes`. No HTML or media allowed.

## Implementation checkpoints
1. **Upload** – accept file, store temporarily, run validation (size limit 5MB, max 5k rows) before queueing persistence job.
2. **Validation** – apply field rules to each row; collect errors with row numbers; stop after 200 errors to cap token usage.
3. **Persistence** – create cards + SRS positions in batches (e.g. 200), update deck stats incrementally.
4. **Export** – allow filters (folder/deck/all), stream file; include header row and timezone-aware due dates.

## Claude usage tips
- Reference only the section of `02-api-request-response-specs.md` covering `/import` and `/export` when designing payloads.
- When Claude proposes background workers, remind it jobs run via Spring `@Async` + queue table (see `08-architecture-decisions/009-why-spring-async-background-jobs.md`).
- Emphasise import is idempotent per file checksum to avoid duplicates (see business logic flow notes).
