# UC-020: Delete Card

## 1. Brief Description

Authenticated user deletes a flashcard from a deck. Deletion uses soft delete mechanism to allow recovery within a grace period (30 days) before permanent removal.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** Card Service, Background Cleanup Job

## 3. Preconditions

- User is authenticated with valid access token
- Card exists in the system
- Card is not already soft-deleted
- Card belongs to a deck owned by the user
- User has internet connection

## 4. Postconditions

### Success Postconditions

- Card's `deleted_at` timestamp set to current time (soft delete)
- Card excluded from all normal queries (card lists, review queues)
- Card excluded from deck statistics and counts
- UI removes card from display
- Success message displayed
- Card remains in database for 30 days for potential recovery

### Failure Postconditions

- Card remains unchanged in database
- Error message displayed
- User remains on current view

## 5. Main Success Scenario (Basic Flow)

1. User views a card in deck or card list
2. User clicks "Delete" button or selects "Delete" from context menu
3. System displays confirmation dialog:
   - Title: "Delete Card?"
   - Message: "This card will be deleted and removed from your reviews. You can restore it from trash within 30 days."
   - Buttons: "Delete" (danger style), "Cancel"
4. User clicks "Delete" to confirm
5. System validates:
   - Card exists and is not already deleted
   - User has permission to delete (owns the deck)
6. System executes soft delete in database:
   - UPDATE cards SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND deleted_at IS NULL
7. System returns 200 OK with success message
8. UI removes card from current view
9. UI displays success toast: "Card deleted"
10. System excludes card from deck counts, review queues, search results, statistics

## 6. Alternative Flows

### 6a. User Cancels Deletion

**Trigger:** Step 4 - User clicks "Cancel"

1. UI closes confirmation dialog
2. No changes made to database
3. Card remains in list
4. Use case ends (no changes)

### 6b. Card Not Found

**Trigger:** Step 5 - Card does not exist

1. System queries database for card
2. Card not found
3. System returns 404 Not Found
4. UI displays error: "Card not found. It may have already been deleted."
5. Use case ends (failure)

### 6c. Card Already Deleted

**Trigger:** Step 5 - Card already has deleted_at timestamp

1. System detects deleted_at IS NOT NULL
2. System returns 410 Gone
3. UI displays info message: "Card was already deleted"
4. UI refreshes card list to remove stale entry
5. Use case ends (no changes)

### 6d. Card Forbidden

**Trigger:** Step 5 - User does not own the deck

1. System checks deck ownership
2. User lacks permission to delete
3. System returns 403 Forbidden
4. UI displays error: "You do not have permission to delete this card"
5. Use case ends (failure)

### 6e. Network or Database Error

**Trigger:** Step 6 - Request fails

1. Network error or database error occurs
2. System returns 500 error or client catches network error
3. UI displays error message
4. Use case ends (failure)

### 6f. Session Expired

**Trigger:** Step 5 - Access token expired

1. Token expired during deletion process
2. Backend returns 401 Unauthorized
3. Client auto-refreshes token (UC-003)
4. Client retries request with new token
5. Continue to Step 6 (Main Flow)

### 6g. Permanent Cleanup (Background Job)

**Trigger:** Scheduled job runs daily (separate from this use case)

1. Background job queries cards WHERE deleted_at < NOW() - INTERVAL '30 days'
2. Job permanently deletes these cards
3. Cards permanently removed from database
4. No recovery possible after this point

**Note:** This is automatic background cleanup, not part of user-initiated deletion.

## 7. Special Requirements

### 7.1 Performance

- Response time < 500ms for soft delete operation
- Confirmation dialog should appear immediately
- UI update should be instant (optimistic UI update)

### 7.2 Security

- Verify user ownership before deletion
- Use parameterized queries to prevent SQL injection
- Log deletion events for audit trail

### 7.3 Usability

- Clear confirmation dialog with warning message
- Danger-styled "Delete" button (red color)
- Keyboard shortcut: Enter to confirm, Esc to cancel
- Show card preview in confirmation dialog for verification

### 7.4 Data Retention

- Soft delete retains data for 30 days
- Permanent deletion occurs after grace period
- During grace period, cards can be restored (future feature)

## 8. Technology and Data Variations

### 8.1 Soft Delete Implementation

Update deleted_at timestamp instead of removing row:
```sql
UPDATE cards
SET deleted_at = CURRENT_TIMESTAMP
WHERE id = ? AND deleted_at IS NULL
```

### 8.2 Query Filtering

All card queries must exclude soft-deleted cards:
```sql
SELECT * FROM cards
WHERE deck_id = ? AND deleted_at IS NULL
ORDER BY created_at DESC
```

### 8.3 Permanent Deletion (Background Job)

Scheduled daily cleanup:
```sql
DELETE FROM cards
WHERE deleted_at IS NOT NULL
  AND deleted_at < NOW() - INTERVAL '30 days'
```

## 9. Frequency of Occurrence

- Expected: 5-20 card deletions per active user per day
- Common during content curation and deck maintenance
- Spike activity when users reorganize or clean up decks

## 10. Open Issues

- Trash/Recycle Bin UI for viewing and restoring deleted cards
- Bulk delete: Select and delete multiple cards at once
- Undo action: Immediate undo after deletion
- Retention period: Should 30-day grace period be configurable?

## 11. Related Use Cases

- [UC-018: Create Card](UC-018-create-card.md) - Create cards
- [UC-019: Update Card](UC-019-update-card.md) - Edit cards
- [UC-017: Delete Deck](UC-017-delete-deck.md) - Delete entire deck with cards
- [UC-021: Import Cards](UC-021-import-cards.md) - Bulk add cards

## 12. Business Rules References

- **BR-DEL-01:** Cards use soft delete mechanism (deleted_at timestamp)
- **BR-DEL-02:** Soft-deleted cards excluded from all normal queries and operations
- **BR-DEL-03:** Permanent deletion occurs 30 days after soft delete
- **BR-DEL-04:** Only deck owner can delete cards

## 13. UI Mockup Notes

- Confirmation modal dialog with card preview
- Danger-styled "Delete" button (red background)
- "Cancel" button (default style)
- Success toast notification
- Smooth fade-out animation when card removed from list

## 14. API Endpoint

```http
DELETE /api/cards/{cardId}
```

**Request Headers:**

```http
Authorization: Bearer <access-token>
```

**Success Response (200 OK):**

```json
{
  "message": "Card deleted successfully",
  "cardId": "550e8400-e29b-41d4-a716-446655440000",
  "deletedAt": "2025-01-31T12:30:00Z",
  "canRestore": true,
  "restoreDeadline": "2025-03-02T12:30:00Z"
}
```

**Error Responses:**

404 Not Found - Card not found:

```json
{
  "error": "Card not found",
  "message": "The specified card does not exist"
}
```

410 Gone - Card already deleted:

```json
{
  "error": "Card already deleted",
  "message": "This card was already deleted",
  "deletedAt": "2025-01-30T10:00:00Z"
}
```

403 Forbidden - No permission:

```json
{
  "error": "Access denied",
  "message": "You do not have permission to delete this card"
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
  "message": "Failed to delete card. Please try again later."
}
```

## 15. Test Cases

### TC-020-001: Successful Card Deletion

- **Given:** User is authenticated, card exists and belongs to user
- **When:** User confirms deletion
- **Then:** Card soft-deleted (deleted_at set), removed from UI, success message shown

### TC-020-002: Cancel Deletion

- **Given:** User clicks Delete button
- **When:** User clicks Cancel in confirmation dialog
- **Then:** Dialog closes, no changes made, card remains in list

### TC-020-003: Card Not Found

- **Given:** Card ID does not exist
- **When:** User tries to delete
- **Then:** 404 error "Card not found"

### TC-020-004: Card Already Deleted

- **Given:** Card was previously soft-deleted
- **When:** User tries to delete again
- **Then:** 410 error "Card already deleted", UI refreshes list

### TC-020-005: Card Belongs to Another User

- **Given:** Card exists but belongs to different user's deck
- **When:** User tries to delete
- **Then:** 403 error "Access denied"

### TC-020-006: Soft Delete Verification

- **Given:** Card is deleted
- **When:** System queries cards for deck
- **Then:** Deleted card excluded from results (WHERE deleted_at IS NULL)

### TC-020-007: Deleted Card Excluded from Review

- **Given:** Card was in review queue and is deleted
- **When:** User starts review session
- **Then:** Deleted card not included in due cards

### TC-020-008: Deck Count Updated

- **Given:** Deck has 100 cards
- **When:** User deletes 1 card
- **Then:** Deck card count shows 99

### TC-020-009: Permanent Deletion After 30 Days

- **Given:** Card soft-deleted 31 days ago
- **When:** Background cleanup job runs
- **Then:** Card permanently removed from database

### TC-020-010: Session Expired

- **Given:** User's access token expires
- **When:** User submits delete request
- **Then:** Token auto-refreshed, card deleted successfully
