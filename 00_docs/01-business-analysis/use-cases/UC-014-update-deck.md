# UC-014: Update Deck

## 1. Brief Description

Authenticated user updates an existing deck's properties including name and description. This use case only modifies deck metadata; cards within the deck remain unchanged.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** Deck Service

## 3. Preconditions

- User is authenticated with valid access token
- Deck exists and belongs to the user
- User has access to deck management interface
- Deck is not soft-deleted

## 4. Postconditions

### Success Postconditions

- Deck name and/or description updated in database
- `updated_at` timestamp refreshed
- UI reflects latest deck information
- Success message displayed
- Cards within deck remain unchanged
- Deck remains in same folder/location

### Failure Postconditions

- No changes saved to database
- Error message displayed to user
- User remains on current page
- Deck information unchanged

## 5. Main Success Scenario (Basic Flow)

1. User navigates to deck list or folder view
2. User selects a deck to edit (e.g., "IELTS Words")
3. User clicks "Edit" icon/button or deck settings menu
4. System fetches current deck details from database
5. System displays Edit Deck modal/form with fields pre-filled:
   - Deck Name (current value)
   - Description (current value or empty)
   - Location (read-only, shows current folder)
6. User updates deck name and/or description
7. User clicks "Save" button
8. System performs client-side validation:
   - Name is not empty
   - Name length <= 100 characters
   - Description length <= 500 characters (if provided)
   - At least one field has changed
9. Client sends PATCH request to API with updated data
10. Backend validates:
    - User is authenticated (valid JWT token)
    - Deck exists and belongs to user
    - Name is not empty and <= 100 characters
    - Description <= 500 characters (if provided)
11. System checks name uniqueness within same folder/root (if name changed, exclude current deck)
12. No duplicate found
13. System updates deck record with new name/description and refreshed updated_at
14. System returns 200 OK with updated deck object
15. Client updates deck in local state
16. System displays success message: "Deck updated successfully"
17. Modal closes, user sees updated deck information

## 6. Alternative Flows

### 6a. Duplicate Deck Name in Location

**Trigger:** Step 11 - Duplicate name detected when name is changed

1. System detects duplicate name in same location
2. System returns 400 Bad Request: "Deck name already exists in this location"
3. UI displays error below name field
4. User must enter different name
5. Return to Step 6 (Main Flow)

### 6b. Invalid Deck Name - Empty or Too Long

**Trigger:** Step 8 - Validation fails

1. Client-side validation detects empty name or length > 100 chars
2. UI displays inline error and disables "Save" button
3. User must enter valid name
4. Return to Step 6 (Main Flow)

### 6c. Description Too Long

**Trigger:** Step 8 - Description > 500 characters

1. Client-side validation detects length violation
2. UI displays character counter and error message
3. User must shorten description
4. Return to Step 6 (Main Flow)

### 6d. No Changes Made

**Trigger:** Step 8 - All fields remain unchanged

1. Client-side validation detects no changes
2. "Save" button disabled or shows "No changes"
3. User can click "Cancel" to close form
4. Return to Step 6 (Main Flow) or close form

### 6e. Deck Not Found or Forbidden

**Trigger:** Step 10 - Deck validation fails

1. System validates deck ownership
2. Deck not found or belongs to another user
3. System returns 404/403 error
4. Client displays error message
5. Use case ends (failure)

### 6f. User Cancels Update

**Trigger:** Step 7 - User clicks "Cancel"

1. User clicks "Cancel" or closes modal
2. If changes exist, confirmation dialog: "Discard changes?"
3. User confirms, modal closes
4. No API request sent
5. Use case ends (no changes)

### 6g. Network or Database Error

**Trigger:** Step 9 or 13 - Request fails

1. Network error or database error occurs
2. System returns 500 error or client catches network error
3. UI displays error message
4. User can retry update
5. Use case ends (failure)

### 6h. Concurrent Update Conflict

**Trigger:** Step 13 - Another update occurred since form was opened

1. Deck was modified by another session since form loaded
2. System detects updated_at mismatch
3. System returns 409 Conflict: "Deck was modified by another session"
4. Client displays error with refresh option
5. User must refresh and retry
6. Use case ends (failure, requires retry)

### 6i. Session Expired

**Trigger:** Step 10 - Access token expired

1. Token expired during form editing
2. Backend returns 401 Unauthorized
3. Client auto-refreshes token (UC-003)
4. Client retries request with new token
5. Continue to Step 13 (Main Flow)

## 7. Special Requirements

### 7.1 Performance

- Response time < 500ms for deck update
- Optimistic UI update with rollback on error

### 7.2 Validation

- Name: Required, 1-100 characters, unique within same folder/root (exclude current deck), trim whitespace
- Description: Optional, max 500 characters, trim whitespace
- Change detection: Disable save button if no changes detected

### 7.3 Usability

- Inline validation with real-time feedback
- Character counters for name and description
- Keyboard shortcuts: Enter to submit, Esc to cancel (with confirmation if changes exist)
- Auto-focus on name field when modal opens
- Unsaved changes warning
- Loading states and disable double-submit

## 8. Technology and Data Variations

### 8.1 Update Strategy

- Only send changed fields in PATCH request (partial update)
- Optimistic UI update: update UI immediately, rollback if server returns error

### 8.2 Concurrent Update Handling

- Use optimistic locking with `updated_at` timestamp
- If `updated_at` doesn't match, return 409 Conflict

## 9. Frequency of Occurrence

- Expected: 5-15 deck updates per day per active user
- Less frequent after initial setup (1-5 per week)

## 10. Open Issues

- Edit history, bulk edit, inline editing, auto-save, undo/redo - future features

## 11. Related Use Cases

- [UC-013: Create Deck](UC-013-create-deck.md) - Create new deck
- [UC-015: Move Deck](UC-015-move-deck.md) - Change deck location
- [UC-016: Copy Deck](UC-016-copy-deck.md) - Duplicate deck
- [UC-017: Delete Deck](UC-017-delete-deck.md) - Delete deck
- [UC-019: Update Card](UC-019-update-card.md) - Update cards in deck

## 12. Business Rules References

- **BR-DECK-01:** Deck name unique within same folder/root
- **BR-DECK-02:** Deck name max length 100 characters, not empty
- **BR-DECK-05:** Only deck owner can update deck
- **BR-DECK-06:** Update operation only modifies name/description, not cards

## 13. UI Mockup Notes

- Modal with pre-filled form fields: Location (read-only), Deck Name (required, with character counter), Description (optional, with character counter)
- Save button disabled if no changes detected
- Confirmation dialog if user cancels with unsaved changes
- Success message shown after update

## 14. API Endpoint

```http
PATCH /api/decks/{deckId}
```

**Request Headers:**

```http
Authorization: Bearer <access-token>
Content-Type: application/json
```

**Request Body (partial update):**

```json
{
  "name": "IELTS Academic Vocabulary",
  "description": "Advanced vocabulary for IELTS Band 7+"
}
```

**Success Response (200 OK):**

```json
{
  "id": "deck-uuid-123",
  "userId": "user-uuid",
  "name": "IELTS Academic Vocabulary",
  "description": "Advanced vocabulary for IELTS Band 7+",
  "folderId": null,
  "cardCount": 150,
  "createdAt": "2025-01-15T10:00:00Z",
  "updatedAt": "2025-01-31T14:30:00Z",
  "deletedAt": null
}
```

**Error Responses:**

400 Bad Request - Validation error:

```json
{
  "error": "Validation failed",
  "details": [
    {
      "field": "name",
      "message": "Deck name is required"
    }
  ]
}
```

400 Bad Request - Duplicate name:

```json
{
  "error": "Duplicate deck name",
  "message": "Deck name 'IELTS Academic Vocabulary' already exists in this location"
}
```

404 Not Found:

```json
{
  "error": "Deck not found",
  "message": "Deck does not exist"
}
```

403 Forbidden:

```json
{
  "error": "Access denied",
  "message": "You don't have permission to update this deck"
}
```

409 Conflict - Concurrent update:

```json
{
  "error": "Conflict",
  "message": "Deck was modified by another session. Please refresh and try again."
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
  "message": "Failed to update deck. Please try again later."
}
```

## 15. Test Cases

### TC-014-001: Update Deck Name Successfully

- **Given:** User has deck "IELTS Words" with 150 cards
- **When:** User updates name to "IELTS Academic Vocabulary"
- **Then:** Deck name updated, updated_at refreshed, cards unchanged

### TC-014-002: Update Deck Description Successfully

- **Given:** User has deck with description "Old description"
- **When:** User updates description to "New description"
- **Then:** Description updated, name unchanged, cards unchanged

### TC-014-003: Update Both Name and Description

- **Given:** User has deck "IELTS Words" with description "Vocabulary"
- **When:** User updates both fields
- **Then:** Both fields updated successfully

### TC-014-004: Duplicate Name in Same Location

- **Given:** User has two decks "Deck A" and "Deck B" at root
- **When:** User renames "Deck B" to "Deck A"
- **Then:** 400 error with message "Deck name already exists in this location"

### TC-014-005: Same Name in Different Location (Allowed)

- **Given:** User has deck "Grammar" in folder "English"
- **When:** User renames another deck in folder "Japanese" to "Grammar"
- **Then:** Update succeeds (different folders allow same names)

### TC-014-006: Empty Deck Name

- **Given:** User opens edit form for deck "IELTS Words"
- **When:** User clears name field and tries to save
- **Then:** Inline error "Deck name is required", button disabled

### TC-014-007: Deck Name Too Long

- **Given:** User opens edit form
- **When:** User enters name with 101 characters
- **Then:** Inline error "Deck name must be 100 characters or less", button disabled

### TC-014-008: No Changes Made

- **Given:** User opens edit form
- **When:** User makes no changes and clicks Save
- **Then:** Save button disabled or shows "No changes" message

### TC-014-009: Deck Not Found

- **Given:** User has deckId in request that doesn't exist
- **When:** User submits update request
- **Then:** 404 error with message "Deck does not exist"

### TC-014-010: Cancel with Unsaved Changes

- **Given:** User makes changes to deck name
- **When:** User clicks Cancel
- **Then:** Confirmation dialog "Discard changes?", if confirmed, modal closes without saving

### TC-014-011: Concurrent Update Conflict

- **Given:** User A and B (same account) edit same deck simultaneously
- **When:** User A saves first, then User B tries to save
- **Then:** User B gets 409 error "Deck was modified by another session"

### TC-014-012: Session Expired During Update

- **Given:** User opens form and makes changes after 15+ minutes
- **When:** User submits update
- **Then:** Token auto-refreshed, deck updated successfully
