# UC-022: Export Cards

## 1. Brief Description

Authenticated user exports cards from a deck to CSV or Excel (.xlsx) format. Large exports run asynchronously with progress tracking and downloadable file.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** Export Service, Job Runner/Queue

## 3. Preconditions

- User is authenticated with valid access token
- Deck exists and belongs to the user
- User has internet connection

## 4. Postconditions

### Success Postconditions

- Export file generated and downloadable
- For large datasets, a job is created and user downloads when ready
- File contains cards with columns: Front, Back, Box, DueDate (and more)
- Success message displayed

### Failure Postconditions

- No file generated
- Error message displayed
- User remains on current page

## 5. Main Success Scenario (Basic Flow)

1. User opens a deck and selects "Export Cards"
2. System shows export options dialog:
   - Format: CSV or XLSX
   - Scope: ALL (default) or DUE_ONLY
3. User selects format and scope, clicks "Export"
4. System counts rows and determines mode:
   - <= 5,000 cards: synchronous export
   - > 5,000 cards (max 50,000): asynchronous export job
5. System queries cards from deck:
   - If ALL: all cards (excluding soft-deleted)
   - If DUE_ONLY: cards where due_date <= today
6. System generates file with columns:
   - Front, Back, Box, DueDate, ReviewCount, Status, CreatedAt
7. Sync: System returns file download (200 OK with file stream)
8. Async: System returns 202 Accepted with job_id; user polls for status or gets notification
9. Client initiates file download or shows download link
10. Success message displayed

## 6. Alternative Flows

### 6a. Too Many Cards

**Trigger:** Step 4 - Card count exceeds limit

1. System counts cards in deck
2. Total > 50,000 cards
3. System returns 400 Bad Request: "Too many cards to export (max 50,000)"
4. Client displays error message
5. User must use filters or split export
6. Use case ends (failure)

### 6b. No Cards to Export

**Trigger:** Step 5 - No cards found

1. System queries cards
2. No cards found (empty deck or all filtered out)
3. System returns 200 OK with message: "No cards to export"
4. Client displays info message
5. Use case ends (no file generated)

### 6c. Format Not Supported

**Trigger:** Step 3 - Invalid format selected

1. User selects unsupported format
2. System returns 400 Bad Request: "Unsupported export format"
3. Client displays error message
4. Use case ends (failure)

### 6d. Deck Not Found or Forbidden

**Trigger:** Step 4 - Deck validation fails

1. System validates deck ownership
2. Deck not found or belongs to another user
3. System returns 404/403 error
4. Client displays error message
5. Use case ends (failure)

### 6e. Async Job Failure

**Trigger:** Step 8 - Background job encounters error

1. Job processes cards but encounters error
2. System marks job as FAILED
3. System sends failure notification
4. UI shows notification: "Export failed. Please try again later."
5. Use case ends (failure)

### 6f. Async Job Timeout

**Trigger:** Step 8 - Job exceeds time limit

1. Export operation takes longer than 30 seconds (sync limit) or configured timeout
2. System marks job as TIMEOUT
3. System sends timeout notification
4. UI shows error: "Export timed out. Please try again."
5. Use case ends (failure)

### 6g. User Cancels Export

**Trigger:** Step 3 - User clicks "Cancel"

1. User cancels export dialog
2. No export operation initiated
3. Use case ends (no changes)

### 6h. Session Expired

**Trigger:** Step 4 - Access token expired

1. Token expired during export process
2. Backend returns 401 Unauthorized
3. Client auto-refreshes token (UC-003)
4. Client retries request with new token
5. Continue to Step 5 (Main Flow)

## 7. Special Requirements

### 7.1 Performance

- Streaming/buffered writing for large files
- Correct UTF-8 encoding for CSV
- Response time < 30 seconds for sync export (up to 5000 cards)
- Progress updates for async exports

### 7.2 File Format

- **CSV:** UTF-8 encoding, comma delimiter, first row as header
- **XLSX:** Standard Excel format, first row as header
- Columns: Front, Back, Box, DueDate, ReviewCount, Status, CreatedAt

### 7.3 Usability

- Clear export options dialog
- Progress indicator for async exports
- Download link or automatic download
- File naming: deck-{name}-{timestamp}.{ext}

## 8. Technology and Data Variations

### 8.1 Export Modes

- **Synchronous (<= 5,000 cards):**
  - Immediate file download
  - Better UX for small exports

- **Asynchronous (> 5,000 cards, max 50,000):**
  - Job enqueued, returns job_id immediately
  - Background file generation
  - Progress tracking via polling
  - Completion notification with download link

### 8.2 Export Scope

- **ALL:** All cards in deck (excluding soft-deleted)
- **DUE_ONLY:** Only cards due for review (due_date <= today)
- Future: Additional filters (by box, date range, tags)

### 8.3 File Generation

- Stream writing to avoid memory issues
- Temporary file storage for async exports
- Cleanup after download or expiration (24 hours)

## 9. Frequency of Occurrence

- Occasional; backups or sharing
- Expected: 1-3 exports per user per month
- Peak: 5-10 exports per user during initial setup

## 10. Open Issues

- Additional filters (by box/date range/tags) - future
- Custom column selection - future
- Export history / previous exports - future
- Export to Anki/Quizlet format - future

## 11. Related Use Cases

- [UC-021: Import Cards](UC-021-import-cards.md) - Import cards from file
- [UC-023: Review Cards (SRS)](UC-023-review-cards-srs.md) - Review cards
- [UC-018: Create Card](UC-018-create-card.md) - Create cards

## 12. Business Rules References

- **BR-EXP-01:** Max export 50,000 cards
- **BR-EXP-02:** Async export if > 5,000 cards
- **BR-EXP-03:** Export scope: ALL or DUE_ONLY

## 13. UI Mockup Notes

- Export dialog with format selector (CSV/XLSX)
- Scope selector (ALL/DUE_ONLY)
- Export button
- Progress bar for async exports
- Download link or automatic download
- Success message

## 14. API Endpoint

```http
GET /api/decks/{deckId}/export?format=csv|xlsx&scope=ALL|DUE_ONLY
```

**Request Headers:**

```http
Authorization: Bearer <access-token>
```

**Success Response - Sync (200 OK):**

Response body is file stream with headers:
```
Content-Type: text/csv; charset=utf-8
Content-Disposition: attachment; filename="deck-ielts-words-2025-01-31.csv"
```

Or for XLSX:
```
Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
Content-Disposition: attachment; filename="deck-ielts-words-2025-01-31.xlsx"
```

**Success Response - Async (202 Accepted):**

```json
{
  "jobId": "job-uuid-123",
  "total": 12000,
  "status": "PENDING",
  "message": "Export started. Check job status for progress."
}
```

**Error Responses:**

400 Bad Request - Too many cards:

```json
{
  "error": "Too many cards",
  "message": "Too many cards to export (max 50,000)"
}
```

400 Bad Request - Invalid format:

```json
{
  "error": "Invalid format",
  "message": "Unsupported export format. Please use CSV or XLSX."
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
  "message": "You do not have permission to export cards from this deck"
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
  "message": "Failed to export cards. Please try again later."
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
  "status": "SUCCEEDED",
  "downloadUrl": "/api/exports/job-uuid-123.csv",
  "filename": "deck-ielts-words-2025-01-31.csv",
  "expiresAt": "2025-02-01T12:00:00Z"
}
```

**Status values:** PENDING, RUNNING, SUCCEEDED, FAILED, TIMEOUT

## 15. Test Cases

### TC-022-001: Export CSV 1000 Cards Successfully (Sync)

- **Given:** User has deck with 1000 cards
- **When:** User exports with format CSV, scope ALL
- **Then:** File downloaded immediately with 1000 rows

### TC-022-002: Export XLSX 10,500 Cards Successfully (Async)

- **Given:** User has deck with 10,500 cards
- **When:** User exports with format XLSX, scope ALL
- **Then:** Returns jobId, job processes in background, download link provided when complete

### TC-022-003: Too Many Cards (> 50,000)

- **Given:** User has deck with 60,000 cards
- **When:** User tries to export
- **Then:** 400 error "Too many cards to export (max 50,000)"

### TC-022-004: Export DUE_ONLY Scope

- **Given:** User has deck with 1000 cards, 200 due today
- **When:** User exports with scope DUE_ONLY
- **Then:** File contains only 200 due cards

### TC-022-005: Export ALL Scope

- **Given:** User has deck with 1000 cards, 200 due today
- **When:** User exports with scope ALL
- **Then:** File contains all 1000 cards

### TC-022-006: Empty Deck

- **Given:** User has deck with 0 cards
- **When:** User tries to export
- **Then:** 200 OK with message "No cards to export"

### TC-022-007: Deck Not Found

- **Given:** User has invalid deckId
- **When:** User tries to export
- **Then:** 404 error "Deck not found"

### TC-022-008: Invalid Format

- **Given:** User selects unsupported format
- **When:** User submits export request
- **Then:** 400 error "Unsupported export format"

### TC-022-009: Async Job Progress Tracking

- **Given:** User exports deck with 10,000 cards (async mode)
- **When:** User polls job status
- **Then:** Progress updates: 0%, 25%, 50%, 75%, 100%

### TC-022-010: Async Job Failure

- **Given:** Async export job encounters error
- **When:** Job fails
- **Then:** Job marked as FAILED, notification shown

### TC-022-011: File Download After Async Complete

- **Given:** Async export job completed successfully
- **When:** User accesses download URL
- **Then:** File downloaded with correct content

### TC-022-012: File Expiration

- **Given:** Export file generated 25 hours ago
- **When:** User tries to download
- **Then:** 410 error "Export file expired"

### TC-022-013: Session Expired

- **Given:** User's access token expires
- **When:** User submits export request
- **Then:** Token auto-refreshed, export operation proceeds

### TC-022-014: CSV Format Correct

- **Given:** User exports deck with cards containing special characters
- **When:** CSV file generated
- **Then:** File is UTF-8 encoded, special characters preserved correctly

