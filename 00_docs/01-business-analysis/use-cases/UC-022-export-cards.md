# UC-022: Export Cards

## 1. Brief Description

User exports cards from a deck to CSV or Excel (.xlsx). Large exports run asynchronously.

## 2. Actors

- Primary Actor: Authenticated User
- Secondary Actor: Export Service, Job Runner/Queue

## 3. Preconditions

- User is authenticated
- Deck exists and belongs to the user

## 4. Postconditions

### Success Postconditions

- Export file generated and downloadable
- For large datasets, a job is created and user downloads when ready

### Failure Postconditions

- No file generated
- Error message displayed

## 5. Main Success Scenario (Basic Flow)

1. User opens a deck and selects “Export Cards”
2. System shows options:
   - Format: CSV or XLSX
   - Scope: ALL (default) or DUE_ONLY
3. User confirms export
4. System counts rows and determines mode:
   - <= 5,000 cards: synchronous export
   - > 5,000 cards (max 50,000): asynchronous export job
5. System generates file
6. Sync: System returns file download (200)
7. Async: System returns 202 with job_id; user polls or gets notification

## 6. Alternative Flows

### 6a. Too Many Cards

1. If total > 50,000
2. Return 400: "Too many cards to export (max 50,000)"

### 6b. Format Not Supported

1. Return 400

### 6c. Async Job Failure/Timeout

1. Job fails or times out (~30s sync limit, async allowed longer)
2. Job marked FAILED; user sees error on polling

## 7. Special Requirements

- Streaming/buffered writing; correct UTF-8 for CSV
- Reasonable column set: Front, Back, Box, DueDate

## 8. Business Rules / Constraints

- BR-EXP-01: Max export 50,000 cards
- BR-EXP-02: Async export if > 5,000 cards

## 9. Frequency of Occurrence

- Occasional; backups or sharing

## 10. Open Issues

- Additional filters (by box/date) in future

## 11. Related Use Cases

- UC-021: Import Cards
- UC-023: Review Cards (SRS)

## 12. Business Rules References

- BR-EXP-01..02

## 13. UI Mockup Notes

- Offer a quick link to previous exports (history) (future)

## 14. API Endpoint

```
GET /api/decks/{deckId}/export?format=csv|xlsx&scope=ALL|DUE_ONLY
```

Sync (200): response body is file stream; headers:

```
Content-Type: text/csv or application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
Content-Disposition: attachment; filename="deck-<id>-<timestamp>.csv"
```

Async (202):

```json
{ "jobId": "<uuid>", "total": 12000 }
```

Job Status:

```
GET /api/jobs/{jobId}
```

```json
{ "status": "SUCCEEDED", "downloadUrl": "/api/exports/<id>.csv" }
```

Errors:

- 400 invalid format/limit
- 404/403 deck not found/forbidden
- 500 internal error

## 15. Test Cases

- TC-022-001: Export CSV 1000 cards -> immediate download
- TC-022-002: Export XLSX 10,500 cards -> async job -> download available
- TC-022-003: > 50,000 cards -> 400
