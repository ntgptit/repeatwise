# UC-015: Move Deck

## 1. Brief Description

User moves a deck from its current location to another folder or to the root level.

## 2. Actors

- Primary Actor: Authenticated User
- Secondary Actor: Deck Service

## 3. Preconditions

- User is authenticated
- Deck exists and belongs to the user
- Destination folder exists (or root selected)

## 4. Postconditions

### Success Postconditions

- Deck's parent folder reference updated to destination (or null for root)
- UI shows deck under the new location

### Failure Postconditions

- No changes applied
- Error displayed

## 5. Main Success Scenario (Basic Flow)

1. User selects a deck and chooses "Move"
2. User picks destination folder (or root)
3. System validates destination is accessible and owned by user
4. System updates deck.folder_id accordingly
5. System returns 200 OK
6. UI re-renders deck under destination

## 6. Alternative Flows

### 6a. Destination Not Found / Forbidden

Trigger: Step 3

1. System returns 404 or 403
2. UI shows error message

### 6b. No-op Move

Trigger: Step 4

1. Destination equals current location
2. System may return 200 with no change or 204 No Content

## 7. Special Requirements

- Operation within a transaction
- Keep deck metadata unchanged

## 8. Business Rules / Constraints

- BR-DECK-04: Deck can be moved between any folders owned by the user or to root

## 9. Frequency of Occurrence

- Occasional; when reorganizing content

## 10. Open Issues

- None for MVP

## 11. Related Use Cases

- UC-013: Create Deck
- UC-014: Update Deck
- UC-016: Copy Deck

## 12. Business Rules References

- BR-DECK-04

## 13. UI Mockup Notes

- Destination picker shows breadcrumb and recent destinations

## 14. API Endpoint

```
POST /api/decks/{deckId}/move
```

Request Body:

```json
{ "destinationFolderId": "<uuid-or-null-for-root>" }
```

Success (200): returns updated deck

Errors:

- 404/403 destination not found/forbidden

## 15. Test Cases

- TC-015-001: Move to root -> success
- TC-015-002: Move to another folder -> success
- TC-015-003: Destination not found -> 404
- TC-015-004: No-op move -> 200/204
