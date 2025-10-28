# UC-032: View Box Distribution

## 1. Brief Description

User views the distribution of cards across SRS boxes (e.g., Box 1..7) for a selected scope (overall, by folder, or by deck).

## 2. Actors

- Primary Actor: Authenticated User
- Secondary Actor: Statistics Service

## 3. Preconditions

- User is authenticated
- Cards exist in the selected scope

## 4. Postconditions

### Success Postconditions

- Distribution chart/data displayed

### Failure Postconditions

- Error shown; no data displayed

## 5. Main Success Scenario (Basic Flow)

1. User selects “Box Distribution” and scope (All|Folder|Deck)
2. System aggregates counts by current_box within the scope
3. System returns data for boxes 1..N (N = total_boxes)
4. UI renders a bar chart or list with counts

## 6. Alternative Flows

### 6a. Empty Scope

1. System returns zeros for all boxes
2. UI shows an empty state message

### 6b. Cached vs. Live

1. If using cache, show last updated time and a refresh action

## 7. Special Requirements

- Efficient aggregation; optional materialized view for large datasets
- Respect soft-deleted filters (exclude deleted)

## 8. Business Rules / Constraints

- BR-BOX-01: Only non-deleted cards are counted
- BR-SRS-01: Total boxes from user settings (default 7)

## 9. Frequency of Occurrence

- Frequent; used to gauge progress and backlog

## 10. Open Issues

- Add filter by due vs. not-due (future)

## 11. Related Use Cases

- UC-023: Review Cards (SRS)
- UC-031: View User Statistics

## 12. Business Rules References

- BR-BOX-01, BR-SRS-01

## 13. UI Mockup Notes

- Bar chart with labels (Box 1..N) and total

## 14. API Endpoint

```
GET /api/stats/box-distribution?scopeType=ALL|FOLDER|DECK&scopeId=<uuid>
```

Success (200):

```json
{
  "totalBoxes": 7,
  "counts": [120, 300, 450, 230, 90, 40, 10]
}
```

Errors:

- 400 invalid scope
- 404 scope not found

## 15. Test Cases

- TC-032-001: Overall distribution -> sums match total cards
- TC-032-002: Folder scope -> recursive counts correct
- TC-032-003: Empty deck -> all zeros
