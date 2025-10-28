# UC-018: Create Card

## 1. Brief Description

User creates a new flashcard (front/back) inside a specific deck.

## 2. Actors

- Primary Actor: Authenticated User
- Secondary Actor: Card Service

## 3. Preconditions

- User is authenticated
- Target deck exists and belongs to the user

## 4. Postconditions

### Success Postconditions

- New card persisted with front/back content and metadata
- Card initialized for SRS (e.g., current_box = 1, status = NEW)
- UI list for the deck shows the new card

### Failure Postconditions

- No card created
- Error message displayed

## 5. Main Success Scenario (Basic Flow)

1. User navigates to a deck and selects “New Card”
2. System shows Create Card form with fields:
   - Front (required, max 5000 chars)
   - Back (required, max 5000 chars)
3. User fills in Front/Back
4. User clicks “Create”
5. System validates inputs:
   - Front and Back not empty
   - Length <= 5000 characters each
6. System saves new card:
   - id (UUID), deck_id, front, back
   - srs fields: current_box = 1, status = NEW, due_date = current_date
   - created_at, updated_at
7. System returns 201 Created with new card data
8. UI inserts the card into the deck list

## 6. Alternative Flows

### 6a. Validation Error (Empty/Too Long)

Trigger: Step 5

1. Validation fails (front or back empty/too long)
2. System returns 400 with field errors
3. UI highlights invalid fields
4. User corrects and retries (Step 3)

### 6b. Deck Not Found / Forbidden

Trigger: Step 6

1. Deck id invalid or not owned by user
2. System returns 404 or 403

### 6c. Server Error

Trigger: Step 6-7

1. Unexpected error while saving
2. System returns 500 Internal Server Error
3. UI shows: "Unable to create card. Please try again later."

## 7. Special Requirements

- Support keyboard shortcuts (Ctrl+Enter to save)
- Auto-trim whitespace, preserve markdown/plain text as entered

## 8. Business Rules / Constraints

- BR-CARD-01: Front/Back required, max length 5000
- BR-CARD-02: Card belongs to exactly one deck
- BR-CARD-03: New cards start in box 1 (status NEW)

## 9. Frequency of Occurrence

- Frequent during content creation (1–100+/day per active user)

## 10. Open Issues

- Rich text/audio/image support is out of MVP scope

## 11. Related Use Cases

- UC-019: Update Card
- UC-021: Import Cards
- UC-023: Review Cards (SRS)

## 12. Business Rules References

- BR-CARD-01..03

## 13. UI Mockup Notes

- Two textareas (Front/Back) with character counters and validation

## 14. API Endpoint

```
POST /api/decks/{deckId}/cards
```

Request Body:

```json
{
  "front": "What is a closure in JavaScript?",
  "back": "A closure is the combination of a function and the lexical environment within which that function was declared."
}
```

Success (201):

```json
{
  "id": "<uuid>",
  "deckId": "<uuid>",
  "front": "What is a closure in JavaScript?",
  "back": "A closure is ...",
  "currentBox": 1,
  "status": "NEW",
  "dueDate": "2025-01-01",
  "createdAt": "2025-01-01T10:00:00Z",
  "updatedAt": "2025-01-01T10:00:00Z"
}
```

Errors:

- 400 validation error
- 404/403 deck not found/forbidden
- 500 internal error

## 15. Test Cases

- TC-018-001: Create with valid front/back -> 201
- TC-018-002: Empty front -> 400
- TC-018-003: Back > 5000 chars -> 400
- TC-018-004: Deck not found -> 404
