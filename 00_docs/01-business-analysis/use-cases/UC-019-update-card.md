# UC-019: Update Card

## 1. Brief Description

User edits an existing card’s front/back content.

## 2. Actors

- Primary Actor: Authenticated User
- Secondary Actor: Card Service

## 3. Preconditions

- User is authenticated
- Card exists, not soft-deleted, and belongs to the user (via deck)

## 4. Postconditions

### Success Postconditions

- Card front/back updated; updated_at changed
- SRS fields unchanged (unless explicitly designed otherwise)

### Failure Postconditions

- No changes saved
- Error displayed

## 5. Main Success Scenario (Basic Flow)

1. User opens a card in edit mode
2. System shows current Front/Back
3. User edits content
4. User clicks “Save”
5. System validates Front/Back (required, <= 5000)
6. System updates the card and returns 200 OK
7. UI shows updated content

## 6. Alternative Flows

### 6a. Validation Error

Trigger: Step 5

1. Empty or too long Front/Back
2. Return 400 with field errors
3. User corrects and retries

### 6b. Card Not Found / Forbidden

Trigger: Step 6

1. Card id invalid or not owned by user
2. Return 404 or 403

### 6c. Conflict (Optimistic Lock)

Trigger: Step 6

1. Version mismatch (if using versioning)
2. Return 409 Conflict

## 7. Special Requirements

- Preserve formatting; do not strip meaningful whitespace
- Optional: show diff/preview before saving (future)

## 8. Business Rules / Constraints

- BR-CARD-01: Front/Back required, max length 5000
- BR-CARD-04: Editing does not reset SRS box by default

## 9. Frequency of Occurrence

- Common for refining content

## 10. Open Issues

- Whether significant edits should reset box/due_date (MVP: no)

## 11. Related Use Cases

- UC-018: Create Card
- UC-020: Delete Card
- UC-023: Review Cards (SRS)

## 12. Business Rules References

- BR-CARD-01, BR-CARD-04

## 13. UI Mockup Notes

- Autosave indicator (future); Save/Cancel buttons

## 14. API Endpoint

```
PATCH /api/cards/{cardId}
```

Request Body:

```json
{ "front": "...", "back": "..." }
```

Success (200): returns updated card

Errors:

- 400 validation error
- 404 not found
- 403 forbidden
- 409 conflict

## 15. Test Cases

- TC-019-001: Update both front/back -> 200
- TC-019-002: Empty front -> 400
- TC-019-003: Back > 5000 chars -> 400
- TC-019-004: Card not found -> 404
