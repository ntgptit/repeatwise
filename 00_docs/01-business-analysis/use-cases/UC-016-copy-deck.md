# UC-016: Copy Deck

## 1. Brief Description

User creates a copy of an existing deck into a destination folder (or root). Large decks are copied asynchronously with progress tracking.

## 2. Actors

- Primary Actor: Authenticated User
- Secondary Actor: Deck Service, Job Runner/Queue

## 3. Preconditions

- User is authenticated
- Source deck exists and belongs to the user
- Destination folder exists (or root selected)

## 4. Postconditions

### Success Postconditions

- A new deck is created as an independent copy of the source deck
- For sync copy (<= 1000 cards), response returns the new deck
- For async copy (1001–10,000 cards), a job_id is returned; completion notification shown

### Failure Postconditions

- No changes applied; if async job fails, any partial work is rolled back (best effort)

## 5. Main Success Scenario (Basic Flow)

1. User selects a deck and clicks "Copy"
2. User picks destination folder (or root)
3. System counts cards in the source deck
4. System determines mode:
   - <= 1000 cards: synchronous copy
   - 1001–10,000 cards: asynchronous background job
   - > 10,000 cards: reject
5. For sync: system duplicates the deck (and its cards) within a transaction
6. For async: system enqueues a job and returns job_id
7. On completion, UI refreshes destination and shows success

## 6. Alternative Flows

### 6a. Deck Too Large

Trigger: Step 4

1. Card count > 10,000
2. Return 400: "Deck too large to copy (max 10,000 cards)"

### 6b. Destination Invalid

Trigger: Step 2

1. Destination not found or forbidden
2. Return 404/403

### 6c. Name Conflicts

Trigger: Step 5/6

1. If destination has a deck with same name, system may append suffix "(copy)" or timestamp
2. Return applied naming policy in response

### 6d. Async Job Failure

Trigger: Step 6

1. Job encounters an error; system marks job failed, attempts rollback
2. UI shows notification: "Copy failed. Please try again later."

## 7. Special Requirements

- Progress tracking: itemsProcessed/totalItems
- Timeouts: copy max ~10 minutes

## 8. Business Rules / Constraints

- BR-DECK-COPY-01: Sync copy if <= 1000 cards
- BR-DECK-COPY-02: Async copy if 1001–10,000 cards
- BR-DECK-COPY-03: Reject > 10,000 cards

## 9. Frequency of Occurrence

- Occasional

## 10. Open Issues

- Conflict resolution policy configurable?

## 11. Related Use Cases

- UC-015: Move Deck
- UC-017: Delete Deck

## 12. Business Rules References

- BR-DECK-COPY-01..03

## 13. UI Mockup Notes

- Dialog shows estimated card count and mode (sync/async)
- Async progress indicator with cancel (optional)

## 14. API Endpoints

```
POST /api/decks/{deckId}/copy
```

Request Body:

```json
{ "destinationFolderId": "<uuid-or-null>", "renamePolicy": "appendCopySuffix" }
```

Responses:

- 200 (sync): new deck info
- 202 (async): { "jobId": "...", "totalItems": 4321 }
- 400 too large / invalid request

Job Status:

```
GET /api/jobs/{jobId}
```

```json
{ "status": "RUNNING", "itemsProcessed": 600, "totalItems": 1200 }
```

## 15. Test Cases

- TC-016-001: Sync copy with 500 cards -> success
- TC-016-002: Async copy with 5000 cards -> returns jobId and completes
- TC-016-003: > 10,000 cards -> 400
- TC-016-004: Destination not found -> 404
- TC-016-005: Name conflict -> suffix policy applied
