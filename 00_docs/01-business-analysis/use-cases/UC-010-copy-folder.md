# UC-010: Copy Folder

## 1. Brief Description

User creates a deep copy of a folder, including its sub-folders and decks, into a chosen destination. Large copies run as background jobs with progress tracking.

## 2. Actors

- Primary Actor: Authenticated User
- Secondary Actor: Folder Service, Job Runner/Queue

## 3. Preconditions

- User is authenticated
- Source folder exists and belongs to user
- Destination folder exists (or root selected)

## 4. Postconditions

### Success Postconditions

- A new folder subtree is created in destination with independent copies of nodes
- For sync copy (<= 50 items), response returns the new root folder
- For async copy (51–500 items), a job_id is returned; notification on completion

### Failure Postconditions

- No changes applied; if async job fails, any partial work is rolled back (best effort)

## 5. Main Success Scenario (Basic Flow)

1. User selects folder, clicks “Copy”, and chooses destination
2. System counts total items in subtree (folders + decks)
3. System determines mode:
   - <= 50 items: synchronous copy
   - 51–500 items: asynchronous background job
   - > 500 items: reject
4. For sync mode, system duplicates subtree within a transaction
5. For async mode, system enqueues a copy job and returns job_id
6. On completion (either mode), UI shows success and refreshes destination tree

## 6. Alternative Flows

### 6a. Too Large Subtree

Trigger: Step 3

1. Total items > 500
2. Return 400: "Folder too large to copy (max 500 items)"

### 6b. Destination Invalid

Trigger: Step 1

1. Destination not found or forbidden
2. Return 404/403

### 6c. Name Conflicts

Trigger: Step 4/5

1. If destination has existing names, system may append suffix "(copy)" or a timestamp
2. Return naming policy in response

### 6d. Async Job Failure

Trigger: Step 5

1. Job encounters error; system marks job failed, attempts rollback
2. UI displays notification: "Copy failed. Please try again later."

## 7. Special Requirements

- Progress tracking for async jobs: itemsProcessed/totalItems
- Reasonable timeouts: folder copy max ~5 minutes

## 8. Business Rules / Constraints

- BR-COPY-01: Sync copy if <= 50 items
- BR-COPY-02: Async copy if 51–500 items
- BR-COPY-03: Reject > 500 items

## 9. Frequency of Occurrence

- Occasional; spikes when reorganizing content

## 10. Open Issues

- Conflict resolution strategy for names configurable?

## 11. Related Use Cases

- UC-009: Move Folder
- UC-011: Delete Folder

## 12. Business Rules References

- BR-COPY-01..03

## 13. UI Mockup Notes

- Confirmation dialog shows estimated size and mode (sync/async)
- Async flow shows a progress toast/banner

## 14. API Endpoints

```
POST /api/folders/{folderId}/copy
```

Request Body:

```json
{ "destinationFolderId": "<uuid-or-null>", "renamePolicy": "appendCopySuffix" }
```

Responses:

- 200 (sync) => new folder info
- 202 (async) => { "jobId": "...", "totalItems": 123 }
- 400 too large / invalid request

Job Status:

```
GET /api/jobs/{jobId}
```

```json
{ "status": "RUNNING", "itemsProcessed": 60, "totalItems": 120 }
```

## 15. Test Cases

- TC-010-001: Sync copy with 10 items -> success
- TC-010-002: Async copy with 200 items -> returns jobId, then completes
- TC-010-003: > 500 items -> 400
- TC-010-004: Destination not found -> 404
- TC-010-005: Name conflict -> suffix policy applied
