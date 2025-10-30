# UC-018: Create Card

## 1. Brief Description

Authenticated user creates a new flashcard with front and back content inside a specific deck for spaced repetition learning.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** Card Service, SRS System

## 3. Preconditions

- User is authenticated with valid access token
- Target deck exists and belongs to the user
- Deck is not soft-deleted
- User has internet connection

## 4. Postconditions

### Success Postconditions

- New card record created in `cards` table with unique UUID
- Card initialized with SRS metadata: current_box = 1, status = NEW, due_date = current date, review_count = 0
- Deck's card count incremented
- UI updates to show the new card in the deck's card list
- Success message displayed

### Failure Postconditions

- No card created in database
- Error message displayed to user
- User remains on card creation form

## 5. Main Success Scenario (Basic Flow)

1. User navigates to a deck and clicks "Add Card" or "New Card" button
2. System displays Create Card form with fields:
   - Front (required, plain text, max 5000 characters)
   - Back (required, plain text, max 5000 characters)
   - Character count indicators for both fields
3. User enters front content (e.g., "What is a closure in JavaScript?")
4. User enters back content (e.g., "A closure is a function that has access to variables...")
5. User clicks "Create" or presses Ctrl+Enter
6. System validates inputs:
   - Front is not empty or whitespace-only
   - Back is not empty or whitespace-only
   - Front length <= 5000 characters
   - Back length <= 5000 characters
7. System trims leading/trailing whitespace from both fields
8. System queries database to verify deck exists and belongs to user
9. System creates new card record with UUID, deck_id, front, back, current_box = 1, status = NEW, due_date = current date, review_count = 0
10. System returns 201 Created with new card data
11. UI inserts the new card into the deck's card list
12. UI displays success message: "Card created successfully"
13. UI clears the form for next card creation or closes dialog

## 6. Alternative Flows

### 6a. Empty Front Content

**Trigger:** Step 6 - Front is empty or whitespace-only

1. Client-side validation detects empty front field
2. UI displays inline error: "Front is required"
3. "Create" button remains disabled
4. User must enter valid front content
5. Return to Step 3 (Main Flow)

### 6b. Empty Back Content

**Trigger:** Step 6 - Back is empty or whitespace-only

1. Client-side validation detects empty back field
2. UI displays inline error: "Back is required"
3. "Create" button remains disabled
4. User must enter valid back content
5. Return to Step 4 (Main Flow)

### 6c. Content Too Long

**Trigger:** Step 6 - Front or Back exceeds 5000 characters

1. System validates content length
2. UI displays inline error: "Maximum 5000 characters allowed"
3. Character counter turns red
4. User must reduce content length
5. Return to Step 3 or 4 (Main Flow)

### 6d. Deck Not Found or Forbidden

**Trigger:** Step 8 - Deck validation fails

1. System queries database for deck
2. Deck not found or belongs to another user
3. System returns 404/403 error
4. UI displays error: "Deck not found or access denied"
5. Use case ends (failure)

### 6e. Network or Database Error

**Trigger:** Step 5-9 - Request fails

1. Network error or database error occurs
2. System returns 500 error or client catches network error
3. UI displays error message
4. User can retry submission
5. Use case ends (failure)

### 6f. Session Expired

**Trigger:** Step 8 - Access token expired

1. Token expired during form filling
2. Backend returns 401 Unauthorized
3. Client auto-refreshes token (UC-003)
4. Client retries request with new token
5. Continue to Step 9 (Main Flow)

## 7. Special Requirements

### 7.1 Performance

- Response time < 1 second for card creation
- Form validation should be near-instant (client-side)

### 7.2 Validation

- Front and Back: Required, max 5000 characters each, trim whitespace
- Client-side validation for instant feedback
- Server-side validation as final authority

### 7.3 Usability

- Auto-focus on Front field when form opens
- Tab navigation between Front and Back fields
- Ctrl+Enter keyboard shortcut to submit form
- Character count indicators with color coding
- Proper ARIA labels for form fields
- Keyboard navigation fully supported

## 8. Technology and Data Variations

### 8.1 SRS Initialization

New cards start with:
- current_box: 1 (first box in SRS system)
- status: NEW (never reviewed)
- due_date: current date (immediately available)
- review_count: 0
- last_reviewed_at: null

### 8.2 Content Storage

- Plain text storage in MVP
- Future: Support Markdown formatting, LaTeX, images/audio attachments

## 9. Frequency of Occurrence

- Expected: 10-100 cards/day per active user during content creation
- Peak: 200-500 cards/day per user (bulk content entry)

## 10. Open Issues

- Rich text formatting, media attachments, tags/labels, templates, bulk creation, AI-assisted generation - future features

## 11. Related Use Cases

- [UC-013: Create Deck](UC-013-create-deck.md) - Create deck before adding cards
- [UC-019: Update Card](UC-019-update-card.md) - Edit existing card
- [UC-020: Delete Card](UC-020-delete-card.md) - Remove card
- [UC-021: Import Cards](UC-021-import-cards.md) - Bulk card creation
- [UC-023: Review Cards (SRS)](UC-023-review-cards-srs.md) - Study created cards

## 12. Business Rules References

- **BR-CARD-01:** Front and Back are required, maximum 5000 characters each
- **BR-CARD-02:** Card belongs to exactly one deck
- **BR-CARD-03:** New cards start in box 1 with status NEW
- **BR-SRS-01:** New cards are due immediately (due_date = current_date)

## 13. UI Mockup Notes

- Modal dialog or inline form with two large textarea fields
- Character counters below each field
- "Create" and "Cancel" buttons
- Optional: "Create & Add Another" button for rapid entry
- Loading spinner when submitting

## 14. API Endpoint

```http
POST /api/decks/{deckId}/cards
```

**Request Headers:**

```http
Authorization: Bearer <access-token>
Content-Type: application/json
```

**Request Body:**

```json
{
  "front": "What is a closure in JavaScript?",
  "back": "A closure is the combination of a function and the lexical environment within which that function was declared."
}
```

**Success Response (201 Created):**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "deckId": "660e8400-e29b-41d4-a716-446655440001",
  "front": "What is a closure in JavaScript?",
  "back": "A closure is the combination of a function and the lexical environment within which that function was declared.",
  "currentBox": 1,
  "status": "NEW",
  "dueDate": "2025-01-31",
  "reviewCount": 0,
  "lastReviewedAt": null,
  "createdAt": "2025-01-31T10:30:00Z",
  "updatedAt": "2025-01-31T10:30:00Z"
}
```

**Error Responses:**

400 Bad Request - Validation error:

```json
{
  "error": "Validation failed",
  "details": [
    {
      "field": "front",
      "message": "Front is required and cannot be empty"
    }
  ]
}
```

404 Not Found - Deck not found:

```json
{
  "error": "Deck not found",
  "message": "The specified deck does not exist"
}
```

403 Forbidden - No permission:

```json
{
  "error": "Access denied",
  "message": "You do not have permission to add cards to this deck"
}
```

401 Unauthorized:

```json
{
  "error": "Unauthorized",
  "message": "Authentication required"
}
```

500 Internal Server Error:

```json
{
  "error": "Internal server error",
  "message": "Failed to create card. Please try again later."
}
```

## 15. Test Cases

### TC-018-001: Successful Card Creation

- **Given:** User is authenticated, deck exists and belongs to user
- **When:** User submits valid front and back content
- **Then:** Card created with status NEW, box 1, due_date = today, success message shown

### TC-018-002: Empty Front Field

- **Given:** User enters empty or whitespace-only front
- **When:** User tries to submit
- **Then:** Client validation error "Front is required", button disabled

### TC-018-003: Empty Back Field

- **Given:** User enters empty or whitespace-only back
- **When:** User tries to submit
- **Then:** Client validation error "Back is required", button disabled

### TC-018-004: Front Exceeds 5000 Characters

- **Given:** User enters 5001+ characters in front
- **When:** User tries to submit
- **Then:** Validation error "Maximum 5000 characters allowed"

### TC-018-005: Back Exceeds 5000 Characters

- **Given:** User enters 5001+ characters in back
- **When:** User tries to submit
- **Then:** Validation error "Maximum 5000 characters allowed"

### TC-018-006: Deck Not Found

- **Given:** Deck ID does not exist
- **When:** User tries to create card
- **Then:** 404 error "Deck not found"

### TC-018-007: Deck Belongs to Another User

- **Given:** Deck exists but belongs to different user
- **When:** User tries to create card
- **Then:** 403 error "Access denied"

### TC-018-008: Whitespace Trimming

- **Given:** User enters "  front  " and "  back  "
- **When:** Card is created
- **Then:** Stored as "front" and "back" (trimmed)

### TC-018-009: Keyboard Shortcut

- **Given:** User fills in form
- **When:** User presses Ctrl+Enter
- **Then:** Form submits and card created

### TC-018-010: Session Expired

- **Given:** User's access token expires
- **When:** User submits create card form
- **Then:** Token auto-refreshed, card created successfully
