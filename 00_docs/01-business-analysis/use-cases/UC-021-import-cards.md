# UC-021: Import Cards

## 1. Brief Description

User imports multiple cards into a deck from a CSV or Excel (.xlsx) file with validation and error reporting. Large imports run asynchronously with progress tracking.

## 2. Actors

- Primary Actor: Authenticated User
- Secondary Actor: Import Service, Job Runner/Queue

## 3. Preconditions

- User is authenticated
- Destination deck exists and belongs to the user
- Input file prepared with required columns

## 4. Postconditions

### Success Postconditions

- Valid rows are inserted as cards into the target deck
- For large files, a job is created and progress can be monitored
- An import summary is returned; optional error report downloadable

### Failure Postconditions

- No cards imported (if fatal error before processing)
- Detailed validation errors returned or error report provided

## 5. Main Success Scenario (Basic Flow)

1. User goes to a deck and selects “Import Cards”
2. System shows upload dialog with instructions and template download
3. User uploads a CSV/XLSX file and selects options:
   - Duplicate handling: SKIP / REPLACE / KEEP_BOTH
4. System validates file:
   - Size <= 50MB; rows <= 10,000
   - Format: CSV (UTF-8, comma) or .xlsx
   - Required columns: Front, Back
5. System determines mode:
   - <= 5,000 rows: synchronous import
   - 5,001–10,000 rows: asynchronous job
6. System processes rows (streaming, batches of ~1,000):
   - Skip empty rows
   - Validate each row (Front/Back required, <= 5000 chars)
   - Apply duplicate policy within destination deck
7. On completion:
   - Sync: return 200 OK with summary
   - Async: return 202 with job_id; user polls for status or gets notification

## 6. Alternative Flows

### 6a. Validation Errors per Row

Trigger: Step 6

1. Invalid rows collected with row number and message
2. Summary includes counts and error types
3. Provide downloadable error report CSV

### 6b. File Too Large or Too Many Rows

Trigger: Step 4

1. Size > 50MB or rows > 10,000
2. Return 400 with clear message

### 6c. Unsupported Format

Trigger: Step 4

1. File is not CSV/XLSX
2. Return 400

### 6d. Async Job Failure/Timeout

Trigger: Step 7

1. Job fails or exceeds timeout (~2 minutes processing window)
2. Mark job as FAILED and return failure status on polling
3. Partial rows may be rolled back or logged as failed (implementation policy)

## 7. Special Requirements

- Stream processing to limit memory usage
- Progress updates every ~500 rows for async jobs
- Batch inserts (e.g., 1000 rows/transaction)

## 8. Business Rules / Constraints

- BR-IMP-01: File size <= 50MB
- BR-IMP-02: Row limit <= 10,000
- BR-IMP-03: Required columns: Front, Back
- BR-CARD-01: Front/Back required, <= 5000 chars

## 9. Frequency of Occurrence

- Occasional; spikes when bootstrapping decks

## 10. Open Issues

- Additional optional columns (e.g., tags) considered in future

## 11. Related Use Cases

- UC-022: Export Cards
- UC-018: Create Card

## 12. Business Rules References

- BR-IMP-01..03, BR-CARD-01

## 13. UI Mockup Notes

- Show sample template and mapping hints; progress bar for async

## 14. API Endpoint

```
POST /api/decks/{deckId}/import
```

Multipart form fields:

```
file=<uploaded-file>, duplicatePolicy=SKIP|REPLACE|KEEP_BOTH
```

Success (200 - sync):

```json
{
  "imported": 4800,
  "skipped": 100,
  "failed": 100,
  "duplicatePolicy": "SKIP",
  "errorReportUrl": "/api/imports/reports/abc.csv"
}
```

Accepted (202 - async):

```json
{ "jobId": "<uuid>", "totalRows": 9000 }
```

Job Status:

```
GET /api/jobs/{jobId}
```

```json
{ "status": "RUNNING", "rowsProcessed": 4500, "totalRows": 9000 }
```

Errors:

- 400 invalid file/format/limits
- 404/403 deck not found/forbidden
- 500 internal error

## 15. Test Cases

- TC-021-001: CSV 1000 rows -> sync success
- TC-021-002: XLSX 8000 rows -> async -> completes with summary
- TC-021-003: > 10,000 rows -> 400
- TC-021-004: Missing Front column -> 400
- TC-021-005: Duplicate handling = REPLACE -> existing rows replaced
