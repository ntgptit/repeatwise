# Import & Export Validation (Claude Prompt)

**Source**: `00_docs/04-detail-design/05-validation-rules.md` §8 + import/export business flows.

## Import
- Accept CSV/XLSX only; size ≤5 MB; ≤5,000 rows.
- Validate headers exactly match template; additional columns rejected.
- For each row: require `front`, `back`; trim whitespace; limit 2,000 chars; record row number + message for errors.
- Stop validation after 200 errors; provide downloadable report.

## Export
- Require scope (`folderId`, `deckId`, or `ALL`); ensure user owns resource.
- Include due date (ISO) and box number; ensure timezone conversion before writing file.

## Claude tips
- When coding import pipeline, ask Claude to output streaming/batch approach per `03-business-logic-flows.md` to avoid memory spikes.
- Remind Claude to return 202 Accepted with job status payload.
