# UC-027: Edit Card During Review

## 1. Brief Description
User edits the card content (front/back) while in an active review session without resetting the SRS state.

## 2. Actors
- Primary Actor: Authenticated User
- Secondary Actor: Card Service, Review Service

## 3. Preconditions
- User is authenticated
- An active review session exists with a current card

## 4. Postconditions
### Success Postconditions
- Card content updated; updated_at changed
- SRS state preserved (unless explicitly reset by user in advanced options)
- User returns to the same review step for the card

### Failure Postconditions
- No changes saved
- Error displayed

## 5. Main Success Scenario (Basic Flow)
1. User clicks “Edit” on the current card during review
2. System opens an edit panel/modal with Front/Back fields
3. User modifies Front and/or Back
4. User clicks “Save and Continue”
5. System validates (Front/Back required, <= 5000 chars)
6. System updates the card; returns 200 OK
7. UI returns to review flow with updated content

## 6. Alternative Flows
### 6a. Validation Error
1. Empty or too long fields
2. Return 400 with field errors
3. User corrects and retries

### 6b. Concurrency Conflict
1. Another tab updated the card
2. Return 409 Conflict; UI offers refresh and retry

### 6c. Cancel Edit
1. User cancels editing
2. No changes; return to review flow

## 7. Special Requirements
- Keep draft state while editing; preserve user input on errors
- Accessibility: focus trapping in modal, labels, keyboard shortcuts

## 8. Business Rules / Constraints
- BR-CARD-01: Front/Back required, <= 5000 chars
- BR-CARD-04: Editing does not reset SRS state by default

## 9. Frequency of Occurrence
- Occasional; when refining content during study

## 10. Open Issues
- Optional “Reset box to 1 after major edits” (future)

## 11. Related Use Cases
- UC-019: Update Card
- UC-023: Review Cards (SRS)

## 12. Business Rules References
- BR-CARD-01, BR-CARD-04

## 13. UI Mockup Notes
- Inline edit modal with “Save and Continue” and “Cancel” buttons

## 14. API Endpoint
```
PATCH /api/cards/{cardId}
```
Request Body:
```json
{ "front": "...", "back": "..." }
```
Success (200): updated card JSON

Errors:
- 400 validation error
- 404 not found
- 409 conflict

## 15. Test Cases
- TC-027-001: Edit valid fields -> 200 and resume review
- TC-027-002: Empty front -> 400
- TC-027-003: Concurrent update -> 409
