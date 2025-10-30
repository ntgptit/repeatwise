# UC-015: Move Deck

## 1. Brief Description

Authenticated user moves a deck from its current location (folder or root) to a different folder or to the root level. This operation changes only the deck's parent folder reference without affecting the deck's content or cards.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** Deck Service

## 3. Preconditions

- User is authenticated with valid access token
- Deck exists and belongs to the user
- Deck is not soft-deleted
- Destination folder exists and belongs to user (if moving to folder)
- User has access to deck management interface

## 4. Postconditions

### Success Postconditions

- Deck's `folder_id` updated to destination (or NULL for root)
- `updated_at` timestamp refreshed
- Deck appears in UI under new location
- Deck name, description, and cards remain unchanged
- Success message displayed

### Failure Postconditions

- Deck remains in original location
- No changes to database
- Error message displayed

## 5. Main Success Scenario (Basic Flow)

1. User navigates to deck list or folder view
2. User selects a deck to move (e.g., "IELTS Words" in folder "English")
3. User clicks "Move" option from deck menu or context menu
4. System displays Move Deck dialog showing:
   - Current location: "English" folder
   - Destination picker (folder tree or dropdown)
   - Option to move to root level
5. User selects destination folder "IELTS Preparation"
6. User clicks "Move Here" button
7. System performs client-side validation:
   - Destination is different from current location
   - Destination is accessible
8. Client sends POST request to API with destination folder ID
9. Backend validates:
   - User is authenticated
   - Deck exists and belongs to user
   - Destination folder exists and belongs to user (or null for root)
   - Destination is different from current location
10. System checks for name conflicts in destination (query decks table, exclude current deck)
11. No conflict found
12. System updates deck record: folder_id set to destination, updated_at refreshed
13. System returns 200 OK with updated deck
14. Client updates deck location in UI (removes from old location, adds to new location)
15. System displays success message: "Deck moved successfully"
16. Dialog closes, user sees deck in new location

## 6. Alternative Flows

### 6a. Name Conflict in Destination

**Trigger:** Step 10 - Deck with same name exists in destination

1. System detects name conflict
2. System returns 400 Bad Request: "A deck named 'IELTS Words' already exists in this location"
3. Client displays error message
4. User can choose different destination, cancel, or rename deck first
5. Return to Step 5 (Main Flow) or cancel

### 6b. Move to Root Level

**Trigger:** Step 5 - User selects "Root" as destination

1. User selects "Move to Root" option
2. User confirms move
3. Client sends request with `destinationFolderId: null`
4. System validates and checks for conflicts at root
5. System updates `folder_id` to NULL
6. Continue to Step 13 (Main Flow)

### 6c. Move to Same Location (No-Op)

**Trigger:** Step 7 - Destination equals current location

1. User selects current folder as destination
2. Client-side validation detects no change
3. System displays info message: "Deck is already in this location"
4. "Move Here" button disabled
5. User must select different destination or cancel
6. Return to Step 5 (Main Flow) or cancel

### 6d. Destination Folder Not Found

**Trigger:** Step 9 - Destination folder doesn't exist

1. System validates destination folder ID
2. Folder not found or soft-deleted
3. System returns 404 Not Found: "Destination folder does not exist"
4. Client displays error message
5. Use case ends (failure)

### 6e. Destination Folder Forbidden

**Trigger:** Step 9 - Destination folder belongs to another user

1. System validates folder ownership
2. Folder belongs to another user
3. System returns 403 Forbidden
4. Client displays error message
5. Use case ends (failure)

### 6f. Deck Not Found or Forbidden

**Trigger:** Step 9 - Deck validation fails

1. System validates deck ownership
2. Deck not found or belongs to another user
3. System returns 404/403 error
4. Client displays error message
5. Use case ends (failure)

### 6g. User Cancels Move

**Trigger:** Step 6 - User clicks "Cancel"

1. User clicks "Cancel" button
2. Dialog closes
3. No API request sent
4. Deck remains in original location
5. Use case ends (no changes)

### 6h. Network or Database Error

**Trigger:** Step 8 or 12 - Request fails

1. Network error or database error occurs
2. System returns 500 error or client catches network error
3. UI displays error message
4. User can retry move operation
5. Use case ends (failure)

### 6i. Session Expired

**Trigger:** Step 9 - Access token expired

1. Token expired during move operation
2. Backend returns 401 Unauthorized
3. Client auto-refreshes token (UC-003)
4. Client retries request with new token
5. Continue to Step 12 (Main Flow)

## 7. Special Requirements

### 7.1 Performance

- Response time < 500ms for single deck move
- Optimistic UI update for better UX
- Rollback UI if server returns error

### 7.2 Validation

- Destination must be different from current location
- Destination folder must exist and belong to user
- No name conflicts in destination
- Deck must belong to user

### 7.3 Usability

- Folder picker with folder tree and search
- Recent destinations shown for quick access
- Breadcrumbs showing full path of destination folder
- Keyboard shortcuts: Enter to confirm, Esc to cancel
- Visual feedback: highlight destination, show loading state
- Optional undo after move

## 8. Technology and Data Variations

### 8.1 Move Operation

- Simple update of `folder_id` field (NULL for root, UUID for folder)
- Optimistic UI update: update UI immediately, rollback if error

### 8.2 Name Conflict Detection

- Check destination before move (query decks table, exclude current deck)
- If COUNT > 0, name conflict exists

## 9. Frequency of Occurrence

- Expected: 5-20 deck moves per day per active user (during organization)
- Less frequent after initial setup (1-5 per week)

## 10. Open Issues

- Bulk move, drag and drop, auto-organize, move history - future features

## 11. Related Use Cases

- [UC-007: Create Folder](UC-007-create-folder.md) - Create destination folder
- [UC-009: Move Folder](UC-009-move-folder.md) - Move entire folder (affects decks inside)
- [UC-013: Create Deck](UC-013-create-deck.md) - Create deck in specific location
- [UC-014: Update Deck](UC-014-update-deck.md) - Update deck name if conflict
- [UC-016: Copy Deck](UC-016-copy-deck.md) - Copy instead of move
- [UC-017: Delete Deck](UC-017-delete-deck.md) - Delete deck

## 12. Business Rules References

- **BR-DECK-01:** Deck name unique within same folder/root
- **BR-DECK-07:** Deck can be moved between folders owned by user
- **BR-DECK-08:** Move operation only changes location, not content
- **BR-FOLD-05:** Only folder owner can move decks into folder

## 13. UI Mockup Notes

- Move dialog with current location, destination picker (folder tree), and move button
- Warning shown if name conflict detected
- Success message after move
- Deck appears in new location immediately

## 14. API Endpoint

```http
POST /api/decks/{deckId}/move
```

**Request Headers:**

```http
Authorization: Bearer <access-token>
Content-Type: application/json
```

**Request Body:**

```json
{
  "destinationFolderId": "dest-folder-uuid"
}
```

Or move to root:

```json
{
  "destinationFolderId": null
}
```

**Success Response (200 OK):**

```json
{
  "id": "deck-uuid-123",
  "name": "IELTS Words",
  "description": "Vocabulary list",
  "folderId": "dest-folder-uuid",
  "cardCount": 150,
  "createdAt": "2025-01-15T10:00:00Z",
  "updatedAt": "2025-01-31T15:00:00Z"
}
```

**Error Responses:**

400 Bad Request - Name conflict:

```json
{
  "error": "Name conflict",
  "message": "A deck named 'IELTS Words' already exists in this location"
}
```

400 Bad Request - Same location:

```json
{
  "error": "Same location",
  "message": "Deck is already in this location"
}
```

404 Not Found - Deck not found:

```json
{
  "error": "Deck not found",
  "message": "Deck does not exist"
}
```

404 Not Found - Folder not found:

```json
{
  "error": "Folder not found",
  "message": "Destination folder does not exist"
}
```

403 Forbidden:

```json
{
  "error": "Access denied",
  "message": "You don't have permission to move this deck"
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
  "message": "Failed to move deck. Please try again later."
}
```

## 15. Test Cases

### TC-015-001: Move Deck from Folder to Another Folder

- **Given:** Deck "IELTS Words" in folder "English"
- **When:** User moves to folder "IELTS Preparation"
- **Then:** Deck moved successfully, appears in "IELTS Preparation"

### TC-015-002: Move Deck from Folder to Root

- **Given:** Deck "Daily Review" in folder "Active"
- **When:** User moves to root level
- **Then:** Deck moved to root, folder_id set to NULL

### TC-015-003: Move Deck from Root to Folder

- **Given:** Deck "Quick Notes" at root level
- **When:** User moves to folder "Archive"
- **Then:** Deck moved to "Archive" folder

### TC-015-004: Name Conflict in Destination

- **Given:** Deck "Grammar" in folder "English"
- **And:** Another deck "Grammar" already exists in folder "Japanese"
- **When:** User tries to move "Grammar" from "English" to "Japanese"
- **Then:** 400 error with message "A deck named 'Grammar' already exists in this location"

### TC-015-005: Move to Same Location (No-Op)

- **Given:** Deck "Vocabulary" in folder "IELTS"
- **When:** User selects "IELTS" as destination
- **Then:** Info message "Deck is already in this location", button disabled

### TC-015-006: Destination Folder Not Found

- **Given:** User has deckId and invalid folderId in request
- **When:** User submits move request
- **Then:** 404 error with message "Destination folder does not exist"

### TC-015-007: Deck Not Found

- **Given:** Invalid deckId in request
- **When:** User submits move request
- **Then:** 404 error with message "Deck does not exist"

### TC-015-008: Cancel Move Operation

- **Given:** User opens move dialog
- **When:** User clicks Cancel
- **Then:** Dialog closes, deck remains in original location

### TC-015-009: Cards Unchanged After Move

- **Given:** Deck has 150 cards
- **When:** User moves deck to different folder
- **Then:** Deck moved successfully, card count remains 150, card content unchanged

### TC-015-010: Updated Timestamp Refreshed

- **Given:** Deck with updated_at = "2025-01-15T10:00:00Z"
- **When:** User moves deck
- **Then:** updated_at refreshed to current timestamp

### TC-015-011: Optimistic UI Update

- **Given:** User moves deck "IELTS Words"
- **When:** User confirms move
- **Then:** UI immediately shows deck in new location, API request sent in background

### TC-015-012: Rollback on Error

- **Given:** User moves deck, UI updates optimistically
- **When:** Server returns error (e.g., name conflict)
- **Then:** UI rolls back deck to original location, error displayed

### TC-015-013: Session Expired During Move

- **Given:** User opens move dialog after 15+ minutes
- **When:** User submits move request
- **Then:** Token auto-refreshed, deck moved successfully
