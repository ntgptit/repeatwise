# UC-012: View Folder Statistics

## 1. Brief Description

User views aggregated statistics for a folder, including total decks/cards (recursive) and due cards counts.

## 2. Actors

- Primary Actor: Authenticated User
- Secondary Actor: Statistics Service (or Folder Service)

## 3. Preconditions

- User is authenticated
- Target folder exists and belongs to user

## 4. Postconditions

### Success Postconditions

- Statistics are displayed to the user

### Failure Postconditions

- Error message displayed; no stats shown

## 5. Main Success Scenario (Basic Flow)

1. User navigates to a folder and opens the Statistics panel
2. System fetches aggregated stats (recursive):
   - Total decks
   - Total cards
   - Due cards today
   - Last updated timestamp (if cached)
3. System returns stats
4. UI displays metrics and basic charts (optional in MVP)

## 6. Alternative Flows

### 6a. Folder Not Found / Forbidden

1. Return 404/403
2. UI shows error state

### 6b. Stale Cache

1. If using cached stats, UI shows "Last updated at …" and a refresh action
2. On refresh, system recomputes stats (may run in background)

## 7. Special Requirements

- Aggregation should be efficient; consider denormalized `folder_stats` with periodic refresh
- Display should be responsive and readable

## 8. Business Rules / Constraints

- BR-STATS-01: Recursive counts include all descendants
- BR-STATS-02: Due cards = cards with due_date <= today and not deleted

## 9. Frequency of Occurrence

- Frequent during navigation/organization

## 10. Open Issues

- Exact refresh cadence for cached stats (e.g., every 5 minutes)

## 11. Related Use Cases

- UC-019: Update Card
- UC-023: Review Cards (SRS)

## 12. Business Rules References

- BR-STATS-01..02

## 13. UI Mockup Notes

- Compact card showing counts; tooltip explains definitions (recursive, due)

## 14. API Endpoint

```
GET /api/folders/{folderId}/stats
```

Success (200):

```json
{
  "folderId": "<uuid>",
  "totalDecks": 12,
  "totalCards": 3456,
  "dueCards": 123,
  "lastUpdatedAt": "2025-01-01T10:00:00Z"
}
```

Errors:

- 404 not found
- 403 forbidden

## 15. Test Cases

- TC-012-001: Stats for folder with children -> correct recursive counts
- TC-012-002: Stats for empty folder -> zeros
- TC-012-003: Not found -> 404
- TC-012-004: Cached stats shows last updated and refresh
