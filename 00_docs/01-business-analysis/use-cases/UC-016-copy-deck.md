# UC-016: Copy Deck

## 1. Brief Description

Authenticated user creates a copy of an existing deck into a destination folder (or root). The copy includes all cards from the source deck. Large decks are copied asynchronously with progress tracking.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** Deck Service, Job Runner/Queue

## 3. Preconditions

- User is authenticated with valid access token
- Source deck exists and belongs to the user
- Destination folder exists and belongs to user (if copying to folder)
- User has access to deck management interface
- Source deck is not soft-deleted

## 4. Postconditions

### Success Postconditions

- New deck created as independent copy of source deck
- All cards from source deck copied to new deck
- New deck appears in destination location
- For sync copy (<= 1000 cards), response returns the new deck
- For async copy (1001–10,000 cards), job_id returned and completion notification shown
- Success message displayed

### Failure Postconditions

- No deck or cards created
- Error message displayed
- If async job fails, any partial work is rolled back (best effort)

## 5. Main Success Scenario (Basic Flow)

1. User navigates to deck list or folder view
2. User selects a deck to copy (e.g., "IELTS Words" with 500 cards)
3. User clicks "Copy" option from deck menu or context menu
4. System displays Copy Deck dialog showing:
   - Source deck name and card count
   - Destination picker (folder tree or dropdown)
   - Option to copy to root level
5. User selects destination folder "Backup"
6. User clicks "Copy Here" button
7. System counts cards in source deck
8. System determines copy mode:
   - <= 1000 cards: synchronous copy
   - 1001–10,000 cards: asynchronous background job
   - > 10,000 cards: reject
9. For sync copy (<= 1000 cards):
   - System validates destination
   - System checks for name conflicts (append "(copy)" suffix if needed)
   - System creates new deck with copied name
   - System copies all cards in transaction
   - System returns 200 OK with new deck object
10. For async copy (1001–10,000 cards):
    - System validates destination
    - System enqueues background job
    - System returns 202 Accepted with job_id
    - Background job processes cards in batches
    - System sends completion notification when done
11. Client receives response
12. For sync: Client updates deck list with new deck
13. For async: Client shows progress indicator and polls job status
14. System displays success message: "Deck copied successfully"
15. Dialog closes, user sees copied deck in destination

## 6. Alternative Flows

### 6a. Deck Too Large

**Trigger:** Step 8 - Card count > 10,000

1. System counts cards in source deck
2. Card count > 10,000
3. System returns 400 Bad Request: "Deck too large to copy (max 10,000 cards)"
4. Client displays error message
5. Use case ends (failure)

### 6b. Destination Invalid

**Trigger:** Step 9 - Destination validation fails

1. System validates destination folder ID
2. Destination folder not found or belongs to another user
3. System returns 404/403 error
4. Client displays error message
5. Use case ends (failure)

### 6c. Name Conflicts

**Trigger:** Step 9 - Name conflict in destination

1. System checks for deck with same name in destination
2. Name conflict found
3. System appends suffix "(copy)" or timestamp to deck name
4. System continues with copy operation
5. Response includes applied naming policy
6. Continue to Step 10 (Main Flow)

### 6d. Source Deck Not Found or Forbidden

**Trigger:** Step 9 - Source deck validation fails

1. System validates source deck ownership
2. Deck not found or belongs to another user
3. System returns 404/403 error
4. Client displays error message
5. Use case ends (failure)

### 6e. User Cancels Copy

**Trigger:** Step 6 - User clicks "Cancel"

1. User clicks "Cancel" button
2. Dialog closes
3. No API request sent
4. Use case ends (no changes)

### 6f. Network or Database Error (Sync Copy)

**Trigger:** Step 9 - Request fails during sync copy

1. Network error or database error occurs
2. System returns 500 error or client catches network error
3. UI displays error message
4. User can retry copy operation
5. Use case ends (failure)

### 6g. Async Job Failure

**Trigger:** Step 10 - Background job encounters error

1. Job processes cards but encounters error
2. System marks job as failed
3. System attempts rollback of partial work
4. System sends failure notification
5. UI shows notification: "Copy failed. Please try again later."
6. Use case ends (failure)

### 6h. Async Job Timeout

**Trigger:** Step 10 - Job exceeds time limit

1. Copy operation takes longer than 10 minutes
2. System marks job as timeout
3. System attempts rollback
4. System sends timeout notification
5. UI shows error: "Copy timed out. Please try again."
6. Use case ends (failure)

### 6i. Session Expired

**Trigger:** Step 9 - Access token expired

1. Token expired during copy operation
2. Backend returns 401 Unauthorized
3. Client auto-refreshes token (UC-003)
4. Client retries request with new token
5. Continue to Step 9 (Main Flow)

## 7. Special Requirements

### 7.1 Performance

- Sync copy: Response time < 2 seconds for 1000 cards
- Async copy: Progress tracking with itemsProcessed/totalItems
- Batch processing: Process cards in batches of 100-500
- Timeout: Copy max ~10 minutes for async jobs

### 7.2 Validation

- Source deck must exist and belong to user
- Destination folder must exist and belong to user (or null for root)
- Card count <= 10,000 (reject if exceeded)
- Name conflict resolution: append "(copy)" suffix or timestamp

### 7.3 Usability

- Show estimated card count and copy mode (sync/async) in dialog
- For async: Progress indicator with cancel option (optional)
- Clear status messages during copy operation
- Success notification with deck location

## 8. Technology and Data Variations

### 8.1 Copy Modes

- **Synchronous (<= 1000 cards):**
  - Immediate response with new deck
  - All cards copied in single transaction
  - Better UX for small decks

- **Asynchronous (1001–10,000 cards):**
  - Job enqueued, returns job_id immediately
  - Background processing in batches
  - Progress tracking via polling
  - Completion notification

### 8.2 Name Conflict Resolution

- Check destination for existing deck with same name
- If conflict: append "(copy)" suffix or timestamp
- Example: "IELTS Words" → "IELTS Words (copy)" or "IELTS Words (2025-01-31)"

### 8.3 Card Copying Strategy

- Copy all card fields: front, back, box_number, last_reviewed_at, etc.
- Reset SRS state: box_number = 1, last_reviewed_at = null (optional)
- Or preserve SRS state: copy box_number and last_reviewed_at (future option)

## 9. Frequency of Occurrence

- Expected: 1-5 deck copies per day per active user
- Common during initial setup (creating backup copies)
- Less frequent after setup (1-2 per month)

## 10. Open Issues

- Conflict resolution policy configurable (rename vs. error)
- Option to preserve SRS state when copying
- Bulk copy multiple decks at once
- Copy deck structure only (without cards)

## 11. Related Use Cases

- [UC-013: Create Deck](UC-013-create-deck.md) - Create new deck
- [UC-015: Move Deck](UC-015-move-deck.md) - Move deck instead of copy
- [UC-017: Delete Deck](UC-017-delete-deck.md) - Delete source deck after copy
- [UC-018: Create Card](UC-018-create-card.md) - Create cards in new deck

## 12. Business Rules References

- **BR-DECK-COPY-01:** Sync copy if <= 1000 cards
- **BR-DECK-COPY-02:** Async copy if 1001–10,000 cards
- **BR-DECK-COPY-03:** Reject > 10,000 cards
- **BR-DECK-COPY-04:** Name conflict resolved with suffix

## 13. UI Mockup Notes

- Copy dialog with source deck info, destination picker, and copy button
- Show estimated card count and copy mode (sync/async)
- For async: Progress bar with cancel option
- Success message after copy completion

## 14. API Endpoints

### Copy Deck

```http
POST /api/decks/{deckId}/copy
```

**Request Headers:**

```http
Authorization: Bearer <access-token>
Content-Type: application/json
```

**Request Body:**

```json
{
  "destinationFolderId": "dest-folder-uuid",
  "renamePolicy": "appendCopySuffix"
}
```

Or copy to root:

```json
{
  "destinationFolderId": null,
  "renamePolicy": "appendCopySuffix"
}
```

**Success Response - Sync (200 OK):**

```json
{
  "id": "new-deck-uuid",
  "name": "IELTS Words (copy)",
  "description": "Vocabulary list",
  "folderId": "dest-folder-uuid",
  "cardCount": 500,
  "createdAt": "2025-01-31T16:00:00Z",
  "updatedAt": "2025-01-31T16:00:00Z"
}
```

**Success Response - Async (202 Accepted):**

```json
{
  "jobId": "job-uuid-123",
  "totalItems": 5000,
  "status": "PENDING",
  "message": "Deck copy started. Check job status for progress."
}
```

**Error Responses:**

400 Bad Request - Deck too large:

```json
{
  "error": "Deck too large",
  "message": "Deck too large to copy (max 10,000 cards)"
}
```

400 Bad Request - Invalid request:

```json
{
  "error": "Validation failed",
  "details": [
    {
      "field": "destinationFolderId",
      "message": "Invalid destination folder"
    }
  ]
}
```

404 Not Found - Source deck not found:

```json
{
  "error": "Deck not found",
  "message": "Source deck does not exist"
}
```

404 Not Found - Destination folder not found:

```json
{
  "error": "Folder not found",
  "message": "Destination folder does not exist"
}
```

403 Forbidden:

```json
{
  "error": "Access denied",
  "message": "You don't have permission to copy this deck"
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
  "message": "Failed to copy deck. Please try again later."
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
  "itemsProcessed": 2500,
  "totalItems": 5000,
  "progress": 50,
  "message": "Copying cards... 2500/5000"
}
```

**Status values:** PENDING, RUNNING, COMPLETED, FAILED, TIMEOUT

## 15. Test Cases

### TC-016-001: Sync Copy with 500 Cards Successfully

- **Given:** User has deck "IELTS Words" with 500 cards
- **When:** User copies deck to folder "Backup"
- **Then:** New deck created with 500 cards, returns deck object immediately

### TC-016-002: Async Copy with 5000 Cards Successfully

- **Given:** User has deck "Large Deck" with 5000 cards
- **When:** User copies deck to folder "Backup"
- **Then:** Returns jobId, job processes in background, completion notification shown

### TC-016-003: Deck Too Large (> 10,000 cards)

- **Given:** User has deck with 15,000 cards
- **When:** User attempts to copy deck
- **Then:** 400 error with message "Deck too large to copy (max 10,000 cards)"

### TC-016-004: Destination Not Found

- **Given:** User has invalid folderId in request
- **When:** User submits copy request
- **Then:** 404 error with message "Destination folder does not exist"

### TC-016-005: Name Conflict Resolution

- **Given:** User has deck "Grammar" in folder "English"
- **And:** Another deck "Grammar" already exists in folder "Backup"
- **When:** User copies "Grammar" from "English" to "Backup"
- **Then:** New deck created with name "Grammar (copy)"

### TC-016-006: Copy to Root Level

- **Given:** User has deck "IELTS Words" in folder "English"
- **When:** User copies deck to root level
- **Then:** New deck created at root with folderId = null

### TC-016-007: Source Deck Not Found

- **Given:** User has invalid deckId in request
- **When:** User submits copy request
- **Then:** 404 error with message "Source deck does not exist"

### TC-016-008: Cancel Copy Operation

- **Given:** User opens copy dialog
- **When:** User clicks Cancel
- **Then:** Dialog closes, no copy operation initiated

### TC-016-009: Async Job Progress Tracking

- **Given:** User copies deck with 5000 cards (async mode)
- **When:** User polls job status
- **Then:** Progress updates: 0%, 25%, 50%, 75%, 100%

### TC-016-010: Async Job Failure

- **Given:** Async copy job encounters database error
- **When:** Job fails
- **Then:** Job marked as FAILED, notification shown, partial work rolled back

### TC-016-011: Async Job Timeout

- **Given:** Async copy job exceeds 10 minutes
- **When:** Job times out
- **Then:** Job marked as TIMEOUT, notification shown

### TC-016-012: Session Expired During Copy

- **Given:** User's access token expires
- **When:** User submits copy request
- **Then:** Token auto-refreshed, copy operation proceeds

### TC-016-013: Cards Copied Correctly

- **Given:** User has deck with 500 cards (various content)
- **When:** User copies deck
- **Then:** New deck has 500 cards with identical content

### TC-016-014: Copy Deck with Description

- **Given:** User has deck "IELTS Words" with description "Vocabulary list"
- **When:** User copies deck
- **Then:** New deck has same description
