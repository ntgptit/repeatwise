# UC-021: Import Cards

## 1. Brief Description

Authenticated user imports multiple cards into a deck from a CSV or Excel (.xlsx) file with validation and error reporting. Large imports run asynchronously with progress tracking.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** Import Service, Job Runner/Queue

## 3. Preconditions

- User is authenticated with valid access token
- Destination deck exists and belongs to the user
- Input file prepared with required columns (Front, Back)
- User has internet connection

## 4. Postconditions

### Success Postconditions

- Valid rows are inserted as cards into the target deck
- For large files, a job is created and progress can be monitored
- An import summary is returned with counts (imported, skipped, failed)
- Optional error report downloadable for failed rows
- Success message displayed

### Failure Postconditions

- No cards imported (if fatal error before processing)
- Detailed validation errors returned or error report provided
- Error message displayed

## 5. Main Success Scenario (Basic Flow)

1. User navigates to a deck and selects "Import Cards"
2. System shows upload dialog with instructions and template download link
3. User uploads a CSV/XLSX file and selects options:
   - Duplicate handling: SKIP / REPLACE / KEEP_BOTH
4. System validates file:
   - Size <= 50MB
   - Rows <= 10,000
   - Format: CSV (UTF-8, comma) or .xlsx
   - Required columns: Front, Back
5. System determines mode:
   - <= 5,000 rows: synchronous import
   - 5,001–10,000 rows: asynchronous background job
6. System processes rows (streaming, batches of ~1,000):
   - Skip empty rows
   - Validate each row (Front/Back required, <= 5000 chars each)
   - Apply duplicate policy within destination deck
   - Insert valid cards in batches
7. On completion:
   - Sync: System returns 200 OK with summary
   - Async: System returns 202 Accepted with job_id; user polls for status or gets notification
8. Client displays import summary: X imported, Y skipped, Z failed
9. If errors exist, client shows download link for error report
10. Success message displayed

## 6. Alternative Flows

### 6a. Validation Errors per Row

**Trigger:** Step 6 - Invalid rows detected

1. Invalid rows collected with row number and message
2. Summary includes counts and error types
3. Error report CSV generated with details
4. System continues processing valid rows
5. Summary includes failed count and error report URL
6. Continue to Step 7 (Main Flow)

### 6b. File Too Large or Too Many Rows

**Trigger:** Step 4 - File validation fails

1. File size > 50MB or rows > 10,000
2. System returns 400 Bad Request: "File too large (max 50MB) or too many rows (max 10,000)"
3. Client displays error message
4. User must reduce file size or split file
5. Use case ends (failure)

### 6c. Unsupported Format

**Trigger:** Step 4 - File format invalid

1. File is not CSV/XLSX or has wrong encoding
2. System returns 400 Bad Request: "Unsupported file format. Please use CSV or XLSX."
3. Client displays error message
4. Use case ends (failure)

### 6d. Missing Required Columns

**Trigger:** Step 4 - Required columns not found

1. System checks for Front and Back columns
2. One or both columns missing
3. System returns 400 Bad Request: "Missing required columns: Front, Back"
4. Client displays error message
5. Use case ends (failure)

### 6e. Deck Not Found or Forbidden

**Trigger:** Step 5 - Deck validation fails

1. System validates deck ownership
2. Deck not found or belongs to another user
3. System returns 404/403 error
4. Client displays error message
5. Use case ends (failure)

### 6f. Async Job Failure

**Trigger:** Step 7 - Background job encounters error

1. Job processes cards but encounters error
2. System marks job as FAILED
3. System attempts rollback of partial work
4. System sends failure notification
5. UI shows notification: "Import failed. Please try again later."
6. Use case ends (failure)

### 6g. Async Job Timeout

**Trigger:** Step 7 - Job exceeds time limit

1. Import operation takes longer than 2 minutes
2. System marks job as TIMEOUT
3. System attempts rollback
4. System sends timeout notification
5. UI shows error: "Import timed out. Please try again with smaller file."
6. Use case ends (failure)

### 6h. User Cancels Import

**Trigger:** Step 3 - User clicks "Cancel"

1. User cancels upload dialog
2. No file uploaded
3. No import operation initiated
4. Use case ends (no changes)

### 6i. Duplicate Handling - SKIP

**Trigger:** Step 6 - Duplicate policy = SKIP

1. System detects card with same Front/Back already exists in deck
2. System skips this row
3. Row counted as "skipped" in summary
4. Continue processing other rows

### 6j. Duplicate Handling - REPLACE

**Trigger:** Step 6 - Duplicate policy = REPLACE

1. System detects card with same Front/Back already exists in deck
2. System updates existing card with new content
3. Row counted as "imported" in summary
4. Continue processing other rows

### 6k. Duplicate Handling - KEEP_BOTH

**Trigger:** Step 6 - Duplicate policy = KEEP_BOTH

1. System detects card with same Front/Back already exists in deck
2. System creates new card anyway
3. Row counted as "imported" in summary
4. Continue processing other rows

### 6l. Session Expired

**Trigger:** Step 5 - Access token expired

1. Token expired during import process
2. Backend returns 401 Unauthorized
3. Client auto-refreshes token (UC-003)
4. Client retries request with new token
5. Continue to Step 6 (Main Flow)

## 7. Special Requirements

### 7.1 Performance

- Stream processing to limit memory usage
- Batch inserts (e.g., 1000 rows/transaction)
- Progress updates every ~500 rows for async jobs
- Response time < 2 seconds for sync import (up to 5000 rows)

### 7.2 Validation

- File size <= 50MB
- Row limit <= 10,000
- Required columns: Front, Back
- Front/Back required, <= 5000 chars each
- Validate encoding (UTF-8 for CSV)

### 7.3 Usability

- Show sample template and mapping hints
- Progress bar for async imports
- Clear error messages with row numbers
- Downloadable error report CSV
- Template download link

## 8. Technology and Data Variations

### 8.1 File Format Support

- **CSV:** UTF-8 encoding, comma delimiter, first row as header
- **XLSX:** Standard Excel format, first row as header
- Future: Support TSV, JSON, Anki format

### 8.2 Import Modes

- **Synchronous (<= 5,000 rows):**
  - Immediate response with summary
  - Better UX for small files

- **Asynchronous (5,001–10,000 rows):**
  - Job enqueued, returns job_id immediately
  - Background processing in batches
  - Progress tracking via polling
  - Completion notification

### 8.3 Duplicate Detection

- Compare Front and Back content (exact match, case-sensitive)
- Within same destination deck only
- Policy applied per row during import

## 9. Frequency of Occurrence

- Occasional; spikes when bootstrapping decks
- Expected: 1-5 imports per user per month
- Peak: 10-20 imports per user during initial setup

## 10. Open Issues

- Additional optional columns (e.g., tags, box_number) - future
- Custom column mapping - future
- Import from Anki/Quizlet - future
- Import validation preview before import - future

## 11. Related Use Cases

- [UC-018: Create Card](UC-018-create-card.md) - Create single card
- [UC-022: Export Cards](UC-022-export-cards.md) - Export cards to file
- [UC-013: Create Deck](UC-013-create-deck.md) - Create deck before import

## 12. Business Rules References

- **BR-IMP-01:** File size <= 50MB
- **BR-IMP-02:** Row limit <= 10,000
- **BR-IMP-03:** Required columns: Front, Back
- **BR-CARD-01:** Front/Back required, <= 5000 chars

## 13. UI Mockup Notes

- Upload dialog with drag-and-drop area
- File format selector (CSV/XLSX)
- Duplicate handling dropdown
- Template download button
- Progress bar for async imports
- Import summary with counts
- Error report download link (if errors)

## 14. API Endpoint

```http
POST /api/decks/{deckId}/import
```

**Request Headers:**

```http
Authorization: Bearer <access-token>
Content-Type: multipart/form-data
```

**Request Body (multipart form):**

```
file: <uploaded-file>
duplicatePolicy: SKIP | REPLACE | KEEP_BOTH
```

**Success Response - Sync (200 OK):**

```json
{
  "imported": 4800,
  "skipped": 100,
  "failed": 100,
  "duplicatePolicy": "SKIP",
  "errorReportUrl": "/api/imports/reports/abc-uuid.csv"
}
```

**Success Response - Async (202 Accepted):**

```json
{
  "jobId": "job-uuid-123",
  "totalRows": 9000,
  "status": "PENDING",
  "message": "Import started. Check job status for progress."
}
```

**Error Responses:**

400 Bad Request - File too large:

```json
{
  "error": "File too large",
  "message": "File size exceeds 50MB limit"
}
```

400 Bad Request - Too many rows:

```json
{
  "error": "Too many rows",
  "message": "File contains more than 10,000 rows (max 10,000)"
}
```

400 Bad Request - Invalid format:

```json
{
  "error": "Invalid format",
  "message": "Unsupported file format. Please use CSV or XLSX."
}
```

400 Bad Request - Missing columns:

```json
{
  "error": "Missing columns",
  "message": "Required columns not found: Front, Back"
}
```

404 Not Found - Deck not found:

```json
{
  "error": "Deck not found",
  "message": "The specified deck does not exist"
}
```

403 Forbidden:

```json
{
  "error": "Access denied",
  "message": "You do not have permission to import cards to this deck"
}
```

401 Unauthorized:

```json
{
  "error": "Unauthorized",
  "message": "Authentication required"
}
```

500 Internal Server Error:

```json
{
  "error": "Internal server error",
  "message": "Failed to import cards. Please try again later."
}
```

### Get Job Status

```http
GET /api/jobs/{jobId}
```

**Success Response (200 OK):**

```json
{
  "jobId": "job-uuid-123",
  "status": "RUNNING",
  "rowsProcessed": 4500,
  "totalRows": 9000,
  "progress": 50,
  "message": "Importing cards... 4500/9000"
}
```

**Status values:** PENDING, RUNNING, COMPLETED, FAILED, TIMEOUT

## 15. Test Cases

### TC-021-001: CSV Import 1000 Rows Successfully (Sync)

- **Given:** User uploads CSV file with 1000 valid rows
- **When:** User submits import request
- **Then:** 1000 cards imported, returns summary immediately

### TC-021-002: XLSX Import 8000 Rows Successfully (Async)

- **Given:** User uploads XLSX file with 8000 rows
- **When:** User submits import request
- **Then:** Returns jobId, job processes in background, completion notification shown

### TC-021-003: File Too Large

- **Given:** User uploads file > 50MB
- **When:** User submits import request
- **Then:** 400 error "File size exceeds 50MB limit"

### TC-021-004: Too Many Rows

- **Given:** User uploads file with 15,000 rows
- **When:** User submits import request
- **Then:** 400 error "File contains more than 10,000 rows"

### TC-021-005: Missing Front Column

- **Given:** User uploads CSV without Front column
- **When:** User submits import request
- **Then:** 400 error "Required columns not found: Front"

### TC-021-006: Duplicate Handling SKIP

- **Given:** User uploads file with duplicates, policy = SKIP
- **When:** Import processes
- **Then:** Duplicates skipped, counted in skipped count

### TC-021-007: Duplicate Handling REPLACE

- **Given:** User uploads file with duplicates, policy = REPLACE
- **When:** Import processes
- **Then:** Existing cards updated, counted in imported count

### TC-021-008: Validation Errors in Rows

- **Given:** User uploads file with some invalid rows (empty Front, too long Back)
- **When:** Import processes
- **Then:** Valid rows imported, invalid rows counted as failed, error report generated

### TC-021-009: Empty File

- **Given:** User uploads empty file
- **When:** User submits import request
- **Then:** 400 error "File is empty"

### TC-021-010: Invalid File Format

- **Given:** User uploads .txt file
- **When:** User submits import request
- **Then:** 400 error "Unsupported file format"

### TC-021-011: Deck Not Found

- **Given:** User has invalid deckId
- **When:** User submits import request
- **Then:** 404 error "Deck not found"

### TC-021-012: Async Job Progress Tracking

- **Given:** User imports file with 8000 rows (async mode)
- **When:** User polls job status
- **Then:** Progress updates: 0%, 25%, 50%, 75%, 100%

### TC-021-013: Async Job Failure

- **Given:** Async import job encounters database error
- **When:** Job fails
- **Then:** Job marked as FAILED, notification shown, partial work rolled back

### TC-021-014: Session Expired

- **Given:** User's access token expires
- **When:** User submits import request
- **Then:** Token auto-refreshed, import operation proceeds

