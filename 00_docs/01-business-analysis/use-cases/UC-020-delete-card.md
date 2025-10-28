# UC-020: Delete Card

## 1. Brief Description

User deletes a card. Deletion uses soft delete to allow recovery within a grace period.

## 2. Actors

- Primary Actor: Authenticated User
- Secondary Actor: Card Service

## 3. Preconditions

- User is authenticated
- Card exists and belongs to the user (via deck)

## 4. Postconditions

### Success Postconditions

- Card marked as soft-deleted (set `deleted_at`)
- Card excluded from all normal queries and review queues

### Failure Postconditions

- Card remains unchanged
- Error displayed

## 5. Main Success Scenario (Basic Flow)

1. User selects a card and clicks “Delete”
2. System shows confirmation dialog
3. User confirms deletion
4. System sets `deleted_at` for the card inside a transaction
5. System returns 200 OK with success message
6. UI removes the card from the list

## 6. Alternative Flows

### 6a. User Cancels Deletion

Trigger: Step 3

1. User cancels
2. No changes made

### 6b. Card Not Found / Forbidden

1. Card id invalid or belongs to another user
2. Return 404/403

### 6c. Permanent Cleanup

1. Background job permanently deletes soft-deleted cards after 30 days (outside this UC)

## 7. Special Requirements

- Confirmation dialog should warn that the card will not appear in reviews

## 8. Business Rules / Constraints

- BR-DEL-01: Soft delete using `deleted_at`
- BR-DEL-02: Permanent cleanup after 30 days

## 9. Frequency of Occurrence

- Occasional

## 10. Open Issues

- Trash/restore flow handled in future scope

## 11. Related Use Cases

- UC-018: Create Card
- UC-019: Update Card

## 12. Business Rules References

- BR-DEL-01..02

## 13. UI Mockup Notes

- Danger-styled confirm dialog; optional undo toast (future)

## 14. API Endpoint

```
DELETE /api/cards/{cardId}
```

Success (200):

```json
{ "message": "Card deleted (soft delete)" }
```

Errors:

- 404 not found
- 403 forbidden

## 15. Test Cases

- TC-020-001: Delete existing card -> success
- TC-020-002: Cancel confirm -> no change
- TC-020-003: Card not found -> 404
