# UC-017: Delete Deck

## 1. Brief Description

User deletes a deck. Deletion uses soft delete to allow recovery within a grace period.

## 2. Actors

- Primary Actor: Authenticated User
- Secondary Actor: Deck Service

## 3. Preconditions

- User is authenticated
- Deck exists and belongs to the user

## 4. Postconditions

### Success Postconditions

- Deck marked as soft-deleted (`deleted_at` set)
- Related queries exclude the deck by default

### Failure Postconditions

- Deck remains unchanged
- Error displayed

## 5. Main Success Scenario (Basic Flow)

1. User selects a deck and clicks "Delete"
2. System shows confirmation dialog (warns if deck contains cards)
3. User confirms deletion
4. System performs soft delete (set `deleted_at`) in a transaction
5. System returns 200 OK with success message
6. UI removes the deck from active lists

## 6. Alternative Flows

### 6a. User Cancels Deletion

Trigger: Step 3

1. User cancels the dialog
2. No changes made

### 6b. Deck Not Found / Forbidden

1. Deck id invalid or belongs to another user
2. Return 404/403

### 6c. Permanent Cleanup

1. Background job permanently deletes deck after 30 days (outside this use case)

## 7. Special Requirements

- Confirmation dialog indicates number of cards if available
- Provide restore option in Trash (future use case)

## 8. Business Rules / Constraints

- BR-DEL-01: Soft delete using `deleted_at`
- BR-DEL-02: Permanent cleanup after 30 days

## 9. Frequency of Occurrence

- Infrequent

## 10. Open Issues

- Trash/restore flow defined in future scope

## 11. Related Use Cases

- UC-015: Move Deck
- UC-016: Copy Deck
- UC-021: Import Cards
- UC-022: Export Cards

## 12. Business Rules References

- BR-DEL-01..02

## 13. UI Mockup Notes

- Danger-styled confirm with explicit warning when deck has cards

## 14. API Endpoint

```
DELETE /api/decks/{deckId}
```

Success (200):

```json
{ "message": "Deck deleted (soft delete)" }
```

Errors:

- 404 not found
- 403 forbidden

## 15. Test Cases

- TC-017-001: Delete empty deck -> success
- TC-017-002: Delete deck with cards -> success after confirm
- TC-017-003: Cancel at confirm -> no change
- TC-017-004: Not found -> 404
