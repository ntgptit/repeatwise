# UC-019: Update Card

## 1. Brief Description

Authenticated user edits an existing flashcard's front and back content to improve or correct the card information. SRS fields remain unchanged.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** Card Service

## 3. Preconditions

- User is authenticated with valid access token
- Card exists in the system
- Card is not soft-deleted
- Card belongs to a deck owned by the user
- User has internet connection

## 4. Postconditions

### Success Postconditions

- Card's front and/or back content updated in database
- Card's `updated_at` timestamp changed to current time
- SRS fields remain unchanged (current_box, due_date, review_count)
- UI reflects the updated content
- Success message displayed

### Failure Postconditions

- No changes persisted to database
- Error message displayed
- User remains on edit form with current data

## 5. Main Success Scenario (Basic Flow)

1. User views a card and clicks "Edit" button
2. System retrieves current card data
3. System displays Edit Card form pre-filled with:
   - Front (current content)
   - Back (current content)
   - Character count indicators
4. User modifies front and/or back content
5. User clicks "Save" or presses Ctrl+Enter
6. System validates inputs:
   - Front is not empty or whitespace-only
   - Back is not empty or whitespace-only
   - Front length <= 5000 characters
   - Back length <= 5000 characters
7. System trims leading/trailing whitespace from both fields
8. System queries database to verify:
   - Card exists and is not soft-deleted
   - Card belongs to a deck owned by the user
9. System updates card record: front, back, updated_at (SRS fields unchanged)
10. System returns 200 OK with updated card data
11. UI updates to show new content
12. UI displays success message: "Card updated successfully"
13. UI closes edit form or navigates back to card list

## 6. Alternative Flows

### 6a. Empty Front Content

**Trigger:** Step 6 - Front is empty or whitespace-only

1. Client-side validation detects empty front field
2. UI displays inline error: "Front is required"
3. "Save" button remains disabled
4. User must enter valid front content
5. Return to Step 4 (Main Flow)

### 6b. Empty Back Content

**Trigger:** Step 6 - Back is empty or whitespace-only

1. Client-side validation detects empty back field
2. UI displays inline error: "Back is required"
3. "Save" button remains disabled
4. User must enter valid back content
5. Return to Step 4 (Main Flow)

### 6c. Content Too Long

**Trigger:** Step 6 - Front or Back exceeds 5000 characters

1. System validates content length
2. UI displays inline error: "Maximum 5000 characters allowed"
3. Character counter turns red
4. User must reduce content length
5. Return to Step 4 (Main Flow)

### 6d. Card Not Found

**Trigger:** Step 8 - Card does not exist or was deleted

1. System queries database for card
2. Card not found or soft-deleted
3. System returns 404 Not Found
4. UI displays error: "Card not found. It may have been deleted."
5. Use case ends (failure)

### 6e. Card Forbidden

**Trigger:** Step 8 - Card belongs to another user's deck

1. System checks card ownership via deck
2. User does not have permission to edit
3. System returns 403 Forbidden
4. UI displays error: "You do not have permission to edit this card"
5. Use case ends (failure)

### 6f. No Changes Made

**Trigger:** Step 6 - User did not modify content

1. System detects front and back identical to database values
2. System skips database update
3. System returns 200 OK with unchanged card
4. UI displays info message: "No changes made"
5. UI closes edit form

### 6g. User Cancels Edit

**Trigger:** Step 4 - User clicks "Cancel"

1. UI discards changes
2. UI closes edit form or navigates back
3. No database updates
4. Use case ends (no changes)

### 6h. Network or Database Error

**Trigger:** Step 5-9 - Request fails

1. Network error or database error occurs
2. System returns 500 error or client catches network error
3. UI displays error message
4. User remains on edit form with data preserved
5. Use case ends (failure)

### 6i. Session Expired

**Trigger:** Step 8 - Access token expired

1. Token expired during form editing
2. Backend returns 401 Unauthorized
3. Client auto-refreshes token (UC-003)
4. Client retries request with new token
5. Continue to Step 9 (Main Flow)

## 7. Special Requirements

### 7.1 Performance

- Response time < 1 second for card update
- Form validation should be near-instant (client-side)

### 7.2 Validation

- Front and Back: Required, max 5000 characters each, trim whitespace
- Client-side validation for instant feedback
- Server-side validation as final authority
- Detect no-change scenarios to avoid unnecessary updates

### 7.3 Usability

- Tab navigation between fields
- Ctrl+Enter keyboard shortcut to save
- Esc key to cancel
- Character count indicators with color coding
- Unsaved changes indicator
- Confirm before discarding changes if user navigates away

### 7.4 Data Integrity

- SRS fields (current_box, due_date, review_count) remain unchanged
- Only front, back, and updated_at are modified
- Preserve formatting and special characters

## 8. Technology and Data Variations

### 8.1 Database Update

Update only modified fields:
```sql
UPDATE cards
SET front = ?, back = ?, updated_at = CURRENT_TIMESTAMP
WHERE id = ? AND deleted_at IS NULL
```

### 8.2 SRS Fields Preservation

Editing a card does NOT reset SRS progress:
- current_box: Unchanged
- due_date: Unchanged
- review_count: Unchanged
- last_reviewed_at: Unchanged
- status: Unchanged

Rationale: Minor content corrections should not penalize learning progress.

## 9. Frequency of Occurrence

- Expected: 5-50 card edits per active user per day
- Common for refining/correcting content
- Higher during initial content creation phase

## 10. Open Issues

- Reset SRS on major edit, edit history, collaborative editing, rich text diff - future features

## 11. Related Use Cases

- [UC-018: Create Card](UC-018-create-card.md) - Initial card creation
- [UC-020: Delete Card](UC-020-delete-card.md) - Remove card
- [UC-027: Edit Card During Review](UC-027-edit-card-during-review.md) - Quick edit during study session
- [UC-023: Review Cards (SRS)](UC-023-review-cards-srs.md) - Study cards

## 12. Business Rules References

- **BR-CARD-01:** Front and Back are required, maximum 5000 characters each
- **BR-CARD-04:** Editing does not reset SRS box or due_date by default
- **BR-CARD-05:** Only front, back, and updated_at are modified during normal edit

## 13. UI Mockup Notes

- Modal dialog or inline edit mode with pre-filled form
- "Save" and "Cancel" buttons
- Unsaved changes indicator
- Confirmation dialog if user tries to close with unsaved changes
- Loading spinner when saving

## 14. API Endpoint

```http
PATCH /api/cards/{cardId}
```

**Request Headers:**

```http
Authorization: Bearer <access-token>
Content-Type: application/json
```

**Request Body:**

```json
{
  "front": "What is a closure in JavaScript? (Updated)",
  "back": "A closure is a function that has access to variables in its outer lexical scope."
}
```

**Success Response (200 OK):**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "deckId": "660e8400-e29b-41d4-a716-446655440001",
  "front": "What is a closure in JavaScript? (Updated)",
  "back": "A closure is a function that has access to variables in its outer lexical scope.",
  "currentBox": 3,
  "status": "REVIEWING",
  "dueDate": "2025-02-05",
  "reviewCount": 5,
  "lastReviewedAt": "2025-01-30T14:20:00Z",
  "createdAt": "2025-01-15T10:30:00Z",
  "updatedAt": "2025-01-31T11:45:00Z"
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

404 Not Found - Card not found:

```json
{
  "error": "Card not found",
  "message": "The specified card does not exist or has been deleted"
}
```

403 Forbidden - No permission:

```json
{
  "error": "Access denied",
  "message": "You do not have permission to edit this card"
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
  "message": "Failed to update card. Please try again later."
}
```

## 15. Test Cases

### TC-019-001: Successful Card Update

- **Given:** User is authenticated, card exists and belongs to user
- **When:** User modifies front/back and submits valid content
- **Then:** Card updated, updated_at changed, SRS fields unchanged, success message shown

### TC-019-002: Update Front Only

- **Given:** Valid card
- **When:** User changes only front content
- **Then:** Front updated, back unchanged, updated_at changed

### TC-019-003: Update Back Only

- **Given:** Valid card
- **When:** User changes only back content
- **Then:** Back updated, front unchanged, updated_at changed

### TC-019-004: Empty Front Field

- **Given:** User clears front field
- **When:** User tries to save
- **Then:** Validation error "Front is required", button disabled

### TC-019-005: Empty Back Field

- **Given:** User clears back field
- **When:** User tries to save
- **Then:** Validation error "Back is required", button disabled

### TC-019-006: Content Exceeds 5000 Characters

- **Given:** User enters 5001+ characters in front or back
- **When:** User tries to save
- **Then:** Validation error "Maximum 5000 characters allowed"

### TC-019-007: Card Not Found

- **Given:** Card ID does not exist or was deleted
- **When:** User tries to update
- **Then:** 404 error "Card not found"

### TC-019-008: Card Belongs to Another User

- **Given:** Card exists but belongs to different user
- **When:** User tries to update
- **Then:** 403 error "Access denied"

### TC-019-009: No Changes Made

- **Given:** User opens edit form
- **When:** User saves without making any changes
- **Then:** 200 OK, info message "No changes made", no database update

### TC-019-010: SRS Fields Unchanged

- **Given:** Card in box 5 with due_date tomorrow, review_count = 10
- **When:** User updates front/back
- **Then:** Box, due_date, review_count remain unchanged

### TC-019-011: Cancel Edit

- **Given:** User makes changes
- **When:** User clicks Cancel
- **Then:** Changes discarded, no database update

### TC-019-012: Session Expired

- **Given:** User's access token expires
- **When:** User submits update request
- **Then:** Token auto-refreshed, card updated successfully
