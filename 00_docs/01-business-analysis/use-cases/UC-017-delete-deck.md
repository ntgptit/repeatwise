# UC-017: Delete Deck

## 1. Brief Description

Authenticated user deletes a deck. Deletion uses soft delete to allow recovery within a grace period (30 days). After the grace period, the deck and its cards are permanently deleted by a background cleanup job.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** Deck Service, Background Cleanup Job

## 3. Preconditions

- User is authenticated with valid access token
- Deck exists and belongs to the user
- User has access to deck management interface
- Deck is not already soft-deleted

## 4. Postconditions

### Success Postconditions

- Deck marked as soft-deleted (`deleted_at` set to current timestamp)
- Related queries exclude the deck by default
- Deck disappears from active deck list
- Cards within deck remain intact (for recovery)
- Success message displayed
- Background cleanup job scheduled (if not already running)

### Failure Postconditions

- Deck remains unchanged
- Error message displayed
- User remains on current page

## 5. Main Success Scenario (Basic Flow)

1. User navigates to deck list or folder view
2. User selects a deck to delete (e.g., "IELTS Words" with 150 cards)
3. User clicks "Delete" option from deck menu or context menu
4. System displays confirmation dialog:
   - Deck name and card count shown
   - Warning message: "This will delete the deck and all its cards. This action can be undone within 30 days."
   - Options: "Cancel" and "Delete Deck"
5. User confirms deletion by clicking "Delete Deck"
6. Client sends DELETE request to API
7. Backend validates:
   - User is authenticated (valid JWT token)
   - Deck exists and belongs to user
   - Deck is not already soft-deleted
8. System performs soft delete:
   - Updates deck record: `deleted_at = CURRENT_TIMESTAMP`
   - Cards remain unchanged (not deleted)
9. System returns 200 OK with success message
10. Client receives response
11. Client removes deck from active deck list
12. System displays success message: "Deck deleted successfully. You can restore it within 30 days."
13. Dialog closes
14. User sees deck list without deleted deck

## 6. Alternative Flows

### 6a. User Cancels Deletion

**Trigger:** Step 5 - User clicks "Cancel"

1. User clicks "Cancel" button or closes dialog
2. Dialog closes
3. No API request sent
4. Deck remains unchanged
5. Use case ends (no changes)

### 6b. Deck Not Found or Forbidden

**Trigger:** Step 7 - Deck validation fails

1. System validates deck ownership
2. Deck not found or belongs to another user
3. System returns 404/403 error
4. Client displays error message
5. Use case ends (failure)

### 6c. Deck Already Deleted

**Trigger:** Step 7 - Deck already soft-deleted

1. System checks deleted_at field
2. deleted_at IS NOT NULL (already deleted)
3. System returns 400 Bad Request: "Deck is already deleted"
4. Client displays error message
5. Use case ends (failure)

### 6d. Empty Deck Deletion

**Trigger:** Step 4 - Deck has 0 cards

1. System checks card_count
2. card_count = 0
3. Confirmation dialog shows: "Delete empty deck 'IELTS Words'?"
4. User confirms
5. Continue to Step 6 (Main Flow)

### 6e. Network or Database Error

**Trigger:** Step 6 or 8 - Request fails

1. Network error or database error occurs
2. System returns 500 error or client catches network error
3. UI displays error message
4. User can retry deletion
5. Use case ends (failure)

### 6f. Session Expired

**Trigger:** Step 7 - Access token expired

1. Token expired during deletion process
2. Backend returns 401 Unauthorized
3. Client auto-refreshes token (UC-003)
4. Client retries request with new token
5. Continue to Step 8 (Main Flow)

### 6g. Permanent Cleanup (Background Job)

**Trigger:** Background job runs after 30 days

1. Background cleanup job runs daily
2. Job finds decks with deleted_at < NOW() - INTERVAL '30 days'
3. Job permanently deletes deck and all its cards:
   - DELETE FROM cards WHERE deck_id = ?
   - DELETE FROM decks WHERE id = ? AND deleted_at < ?
4. Job logs permanent deletion
5. Use case ends (outside main flow)

**Note:** This is automatic background cleanup, not part of user-initiated deletion.

## 7. Special Requirements

### 7.1 Performance

- Response time < 500ms for soft delete
- Background cleanup job runs daily during off-peak hours
- Batch deletion for expired soft-deleted decks

### 7.2 Validation

- Deck must exist and belong to user
- Deck must not be already soft-deleted
- Confirmation required for decks with cards

### 7.3 Usability

- Clear confirmation dialog with deck name and card count
- Warning message about 30-day recovery period
- Success message includes recovery information
- Provide restore option in Trash view (future use case)

### 7.4 Security

- Only deck owner can delete deck
- Soft delete prevents accidental permanent loss
- 30-day grace period allows recovery
- Permanent cleanup prevents database bloat

## 8. Technology and Data Variations

### 8.1 Soft Delete Implementation

- Set `deleted_at` timestamp instead of actual deletion
- All queries filter by `deleted_at IS NULL` by default
- Cards remain linked to deck (for recovery)
- Index on `deleted_at` for cleanup job efficiency

### 8.2 Background Cleanup Job

- Runs daily via scheduled job (cron/scheduler)
- Finds decks with `deleted_at < NOW() - INTERVAL '30 days'`
- Permanently deletes deck and all cards in transaction
- Logs deletion for audit trail

### 8.3 Recovery Strategy

- User can restore deck within 30 days (future use case)
- Restore operation: SET `deleted_at = NULL`
- Cards automatically become accessible again

## 9. Frequency of Occurrence

- Expected: 1-5 deck deletions per day per active user
- Common during initial organization (removing unwanted decks)
- Less frequent after setup (1-2 per month)
- Background cleanup: Runs daily, processes 0-50 expired decks per day

## 10. Open Issues

- Trash/restore flow: Separate use case for restoring deleted decks
- Bulk delete: Delete multiple decks at once
- Permanent delete option: Immediate permanent deletion (skip grace period)
- Deletion confirmation: Make confirmation optional for empty decks
- Deletion history: Track deletion reason/notes

## 11. Related Use Cases

- [UC-013: Create Deck](UC-013-create-deck.md) - Create new deck
- [UC-015: Move Deck](UC-015-move-deck.md) - Move deck instead of delete
- [UC-016: Copy Deck](UC-016-copy-deck.md) - Copy deck before deletion
- [UC-021: Import Cards](UC-021-import-cards.md) - Import cards after restore
- [UC-022: Export Cards](UC-022-export-cards.md) - Export cards before deletion

## 12. Business Rules References

- **BR-DEL-01:** Soft delete using `deleted_at` timestamp
- **BR-DEL-02:** Permanent cleanup after 30 days
- **BR-DEL-03:** Only deck owner can delete deck
- **BR-DEL-04:** Confirmation required for decks with cards

## 13. UI Mockup Notes

- Confirmation dialog with danger styling (red/warning colors)
- Shows deck name, card count, and warning message
- Explicit warning when deck contains cards
- Success message mentions 30-day recovery period
- Trash view for browsing deleted decks (future)

## 14. API Endpoint

```http
DELETE /api/decks/{deckId}
```

**Request Headers:**

```http
Authorization: Bearer <access-token>
```

**Success Response (200 OK):**

```json
{
  "message": "Deck deleted successfully. You can restore it within 30 days.",
  "deletedAt": "2025-01-31T17:00:00Z"
}
```

**Error Responses:**

404 Not Found - Deck not found:

```json
{
  "error": "Deck not found",
  "message": "Deck does not exist"
}
```

400 Bad Request - Already deleted:

```json
{
  "error": "Already deleted",
  "message": "Deck is already deleted"
}
```

403 Forbidden:

```json
{
  "error": "Access denied",
  "message": "You don't have permission to delete this deck"
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
  "message": "Failed to delete deck. Please try again later."
}
```

## 15. Test Cases

### TC-017-001: Delete Empty Deck Successfully

- **Given:** User has empty deck "Test Deck" (0 cards)
- **When:** User confirms deletion
- **Then:** Deck soft-deleted, deleted_at set, deck removed from active list

### TC-017-002: Delete Deck with Cards Successfully

- **Given:** User has deck "IELTS Words" with 150 cards
- **When:** User confirms deletion after seeing warning
- **Then:** Deck soft-deleted, cards remain intact, deck removed from active list

### TC-017-003: Cancel Deletion

- **Given:** User opens delete confirmation dialog
- **When:** User clicks Cancel
- **Then:** Dialog closes, no deletion, deck remains unchanged

### TC-017-004: Deck Not Found

- **Given:** User has invalid deckId in request
- **When:** User submits delete request
- **Then:** 404 error with message "Deck does not exist"

### TC-017-005: Deck Already Deleted

- **Given:** Deck already has deleted_at set
- **When:** User attempts to delete again
- **Then:** 400 error with message "Deck is already deleted"

### TC-017-006: Deck Belongs to Another User

- **Given:** User attempts to delete deck owned by another user
- **When:** User submits delete request
- **Then:** 403 error with message "You don't have permission to delete this deck"

### TC-017-007: Confirmation Dialog Shows Card Count

- **Given:** User has deck "IELTS Words" with 150 cards
- **When:** User clicks Delete
- **Then:** Confirmation dialog shows "150 cards" in warning message

### TC-017-008: Success Message Mentions Recovery Period

- **Given:** User successfully deletes deck
- **When:** Delete operation completes
- **Then:** Success message includes "You can restore it within 30 days"

### TC-017-009: Deck Excluded from Active Lists

- **Given:** User deletes deck "IELTS Words"
- **When:** User views deck list
- **Then:** "IELTS Words" does not appear in active deck list

### TC-017-010: Cards Remain After Soft Delete

- **Given:** User has deck "IELTS Words" with 150 cards
- **When:** User deletes deck
- **Then:** Deck soft-deleted, all 150 cards remain in database (linked to deck)

### TC-017-011: Permanent Cleanup After 30 Days

- **Given:** Deck deleted 31 days ago (deleted_at = old timestamp)
- **When:** Background cleanup job runs
- **Then:** Deck and all cards permanently deleted from database

### TC-017-012: Session Expired During Deletion

- **Given:** User's access token expires
- **When:** User submits delete request
- **Then:** Token auto-refreshed, deck deleted successfully

### TC-017-013: Network Error During Deletion

- **Given:** User confirms deletion but network disconnected
- **When:** Request fails
- **Then:** Error message shown, user can retry deletion

### TC-017-014: Database Error During Deletion

- **Given:** Database temporarily unavailable
- **When:** User submits delete request
- **Then:** 500 error with message "Failed to delete deck. Please try again later."

### TC-017-015: Multiple Deletions in Sequence

- **Given:** User has multiple decks to delete
- **When:** User deletes deck A, then deck B, then deck C
- **Then:** All decks soft-deleted successfully, all removed from active list
