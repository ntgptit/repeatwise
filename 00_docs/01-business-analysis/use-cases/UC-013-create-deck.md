# UC-013: Create Deck

## 1. Brief Description

Authenticated user creates a new deck (bộ thẻ) to organize flashcards for learning. A deck can be placed at the root level or inside a folder. Each deck is a container for flashcards and serves as the primary organizational unit for learning content.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** Deck Service

## 3. Preconditions

- User is authenticated with valid access token
- User can access the deck management interface
- If creating deck inside folder, the folder must exist and belong to the user
- User is viewing the location where they want to create a deck

## 4. Postconditions

### Success Postconditions

- New deck record created in `decks` table with unique UUID, name, description, folder_id, and card_count = 0
- Deck appears in UI at correct location
- User can immediately add cards to the new deck
- Success message displayed

### Failure Postconditions

- No deck created in database
- Error message displayed to user
- User remains on current page

## 5. Main Success Scenario (Basic Flow)

1. User navigates to deck list or folder view
2. User clicks "New Deck" button or "+" icon
3. System displays Create Deck modal/form with fields:
   - Deck Name (required, max 100 chars)
   - Description (optional, max 500 chars)
   - Location (read-only, shows current folder or "Root")
4. User enters deck name "IELTS Academic Words"
5. User optionally enters description "Vocabulary for IELTS Writing Task 2"
6. User clicks "Create" button
7. System performs client-side validation:
   - Name is not empty
   - Name length <= 100 characters
   - Description length <= 500 characters (if provided)
8. Client sends POST request to API with deck data
9. Backend validates:
   - User is authenticated (valid JWT token)
   - Name is not empty and <= 100 characters
   - Description <= 500 characters (if provided)
   - Folder exists and belongs to user (if folder_id provided)
10. System checks name uniqueness within same folder/root (query decks table)
11. No duplicate found
12. System generates new UUID and inserts deck record with card_count = 0
13. System returns 201 Created with new deck object
14. Client updates deck list state
15. System displays success message: "Deck created successfully"
16. Modal closes, user sees new deck in list

## 6. Alternative Flows

### 6a. Deck Name Already Exists in Location

**Trigger:** Step 10 - Duplicate name detected

1. System detects duplicate name in same folder/root
2. System returns 400 Bad Request: "Deck name already exists in this location"
3. UI displays error below name field
4. User must enter different name
5. Return to Step 4 (Main Flow)

### 6b. Invalid Deck Name - Empty or Too Long

**Trigger:** Step 7 - Validation fails

1. Client-side validation detects empty name or length > 100 chars
2. UI displays inline error and disables "Create" button
3. User must enter valid name
4. Return to Step 4 (Main Flow)

### 6c. Description Too Long

**Trigger:** Step 7 - Description > 500 characters

1. Client-side validation detects length violation
2. UI displays character counter and error message
3. User must shorten description
4. Return to Step 5 (Main Flow)

### 6d. Folder Not Found or Forbidden

**Trigger:** Step 9 - Folder validation fails

1. System validates folder_id
2. Folder not found or belongs to another user
3. System returns 404/403 error
4. Client displays error message
5. Use case ends (failure)

### 6e. User Cancels Creation

**Trigger:** Step 6 - User clicks "Cancel"

1. User clicks "Cancel" or closes modal
2. Modal closes, no API request sent
3. Use case ends (no changes)

### 6f. Network or Database Error

**Trigger:** Step 8 or 12 - Request fails

1. Network error or database error occurs
2. System returns 500 error or client catches network error
3. UI displays error message
4. User can retry creation
5. Use case ends (failure)

### 6g. Session Expired

**Trigger:** Step 9 - Access token expired

1. Token expired during form filling
2. Backend returns 401 Unauthorized
3. Client auto-refreshes token (UC-003)
4. Client retries request with new token
5. Continue to Step 12 (Main Flow)

## 7. Special Requirements

### 7.1 Performance

- Response time < 500ms for deck creation
- UI update should be smooth, no full page reload

### 7.2 Validation

- Name: Required, 1-100 characters, unique within same folder/root, trim whitespace
- Description: Optional, max 500 characters, trim whitespace

### 7.3 Usability

- Inline validation with real-time feedback
- Character counters for name and description
- Keyboard shortcuts: Enter to submit, Esc to cancel
- Auto-focus on name field when modal opens
- Loading states and disable double-submit
- Proper ARIA labels and keyboard navigation support

## 8. Technology and Data Variations

### 8.1 Deck Organization

- Root level: folder_id = NULL
- Inside folders: folder_id = UUID
- Name uniqueness enforced within same folder/root (case-sensitive for MVP)
- New decks start with card_count = 0

## 9. Frequency of Occurrence

- Expected: 5-20 deck creations per day per active user (initial setup phase)
- Ongoing: 1-5 new decks per month per active user

## 10. Open Issues

- Deck templates, import, sharing, visual customization - future features
- Per-deck SRS settings override - future

## 11. Related Use Cases

- [UC-007: Create Folder](UC-007-create-folder.md) - Create folder to organize decks
- [UC-014: Update Deck](UC-014-update-deck.md) - Update deck name/description
- [UC-015: Move Deck](UC-015-move-deck.md) - Move deck to different folder
- [UC-016: Copy Deck](UC-016-copy-deck.md) - Duplicate deck with cards
- [UC-017: Delete Deck](UC-017-delete-deck.md) - Delete deck
- [UC-018: Create Card](UC-018-create-card.md) - Add cards to deck
- [UC-021: Import Cards](UC-021-import-cards.md) - Bulk import cards to deck

## 12. Business Rules References

- **BR-DECK-01:** Deck name unique within same folder/root
- **BR-DECK-02:** Deck name max length 100 characters, not empty
- **BR-DECK-03:** Deck can exist at root (folder_id NULL) or inside folder
- **BR-DECK-04:** New deck starts with card_count = 0

## 13. UI Mockup Notes

### Modal Layout (Create Deck)

```
┌─────────────────────────────────────────┐
│ Create New Deck                      [X]│
├─────────────────────────────────────────┤
│                                         │
│ Location: IELTS Preparation             │
│                                         │
│ Deck Name *                             │
│ ┌─────────────────────────────────┐    │
│ │ IELTS Academic Words            │    │
│ └─────────────────────────────────┘    │
│ 20/100                                  │
│                                         │
│ Description                             │
│ ┌─────────────────────────────────┐    │
│ │ Vocabulary for IELTS Writing    │    │
│ │ Task 2                          │    │
│ └─────────────────────────────────┘    │
│ 35/500                                  │
│                                         │
│                  [Cancel]  [Create Deck]│
└─────────────────────────────────────────┘
```

### Deck Card View (After Creation)

```
┌───────────────────────────────────┐
│ IELTS Academic Words          [•••]│
│ Vocabulary for IELTS Writing      │
│                                   │
│ 📚 0 cards                        │
│ 📅 Created: Jan 31, 2025          │
│                                   │
│ [+ Add Cards]      [Start Review] │
└───────────────────────────────────┘
```

## 14. API Endpoint

```http
POST /api/decks
```

**Request Headers:**

```http
Authorization: Bearer <access-token>
Content-Type: application/json
```

**Request Body:**

```json
{
  "name": "IELTS Academic Words",
  "description": "Vocabulary for IELTS Writing Task 2",
  "folderId": null
}
```

**Success Response (201 Created):**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "user-uuid",
  "name": "IELTS Academic Words",
  "description": "Vocabulary for IELTS Writing Task 2",
  "folderId": null,
  "cardCount": 0,
  "createdAt": "2025-01-31T10:30:00Z",
  "updatedAt": "2025-01-31T10:30:00Z",
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
  "message": "Deck name 'IELTS Academic Words' already exists in this location"
}
```

404 Not Found - Folder not found:

```json
{
  "error": "Folder not found",
  "message": "Folder does not exist"
}
```

403 Forbidden - Folder access denied:

```json
{
  "error": "Access denied",
  "message": "You don't have permission to create decks in this folder"
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
  "message": "Failed to create deck. Please try again later."
}
```

## 15. Test Cases

### TC-013-001: Create Deck at Root Level Successfully

- **Given:** User is authenticated and viewing deck list
- **When:** User creates deck with name "IELTS Academic Words" at root level
- **Then:** Deck created with folderId=null, cardCount=0, appears in list

### TC-013-002: Create Deck Inside Folder Successfully

- **Given:** User has existing folder "IELTS Preparation"
- **When:** User creates deck "Academic Words" inside folder
- **Then:** Deck created with correct folderId, appears under folder

### TC-013-003: Duplicate Deck Name in Same Location

- **Given:** User has deck "IELTS Words" at root level
- **When:** User attempts to create another deck "IELTS Words" at root level
- **Then:** 400 error with message "Deck name already exists in this location"

### TC-013-004: Same Deck Name in Different Locations

- **Given:** User has deck "Grammar" in folder "English"
- **When:** User creates deck "Grammar" in folder "Japanese"
- **Then:** Deck created successfully (different locations allow same names)

### TC-013-005: Empty Deck Name

- **Given:** User opens create deck form
- **When:** User leaves name field empty and clicks Create
- **Then:** Inline error "Deck name is required", button disabled

### TC-013-006: Deck Name Too Long

- **Given:** User enters name with 101 characters
- **When:** User tries to submit
- **Then:** Inline error "Deck name must be 100 characters or less", button disabled

### TC-013-007: Description Too Long

- **Given:** User enters description with 501 characters
- **When:** User tries to submit
- **Then:** Inline error "Description must be 500 characters or less", button disabled

### TC-013-008: Folder Not Found

- **Given:** User has folderId in request that doesn't exist
- **When:** User submits creation request
- **Then:** 404 error with message "Folder does not exist"

### TC-013-009: Session Expired During Creation

- **Given:** User's access token expires
- **When:** User submits create deck form
- **Then:** Token auto-refreshed, deck created successfully

### TC-013-010: Cancel Deck Creation

- **Given:** User opens create deck modal
- **When:** User clicks "Cancel" button
- **Then:** Modal closes, no deck created
