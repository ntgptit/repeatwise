# UC-027: Edit Card During Review

## 1. Brief Description

User edits the card content (front/back) while in an active review session without resetting the SRS state. The card's content is updated, and the user returns to the same review step for the card.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** Card Service, Review Service

## 3. Preconditions

- User is authenticated with valid access token
- An active review session exists with sessionId
- A current card is displayed (Front or Back view)
- User has permission to edit the card (owns the card)

## 4. Postconditions

### Success Postconditions

- Card content updated in database:
  - front text updated
  - back text updated
  - updated_at timestamp refreshed
- SRS state preserved (current_box, due_date, last_reviewed_at unchanged)
- User returns to the same review step for the card (Front or Back view)
- Card remains in current position in session queue
- Success message displayed

### Failure Postconditions

- No changes saved to card content
- Card SRS state unchanged
- Error message displayed
- User remains on current card

## 5. Main Success Scenario (Basic Flow)

1. User is in active review session viewing a card
2. System displays card (Front or Back side)
3. User notices an error or wants to improve the card content
4. User clicks "Edit" button (or presses keyboard shortcut "E")
5. Client opens edit modal/panel with form fields:
   - Front (textarea, required)
   - Back (textarea, required)
   - Save and Continue button
   - Cancel button
6. System pre-populates form with current card content:
   - Front: current card front text
   - Back: current card back text
7. User modifies Front text (e.g., fixes typo: "capitol" → "capital")
8. User modifies Back text (e.g., adds more detail)
9. User clicks "Save and Continue" button
10. Client validates input:
    - Front is not empty
    - Back is not empty
    - Front length <= 5000 characters
    - Back length <= 5000 characters
11. Client sends PATCH request to update card endpoint:
    ```json
    {
      "front": "What is the capital of France?",
      "back": "Paris is the capital and largest city of France."
    }
    ```
12. Backend validates request:
    - User is authenticated
    - Card exists and belongs to user
    - Card is not deleted
    - Front and Back are not empty
    - Front and Back length <= 5000 characters
13. System starts database transaction
14. System retrieves current card version (for optimistic locking):
    ```sql
    SELECT id, front, back, updated_at, version
    FROM cards
    WHERE id = ? AND user_id = ? AND deleted_at IS NULL
    ```
15. System updates card record:
    ```sql
    UPDATE cards
    SET front = ?,
        back = ?,
        updated_at = CURRENT_TIMESTAMP,
        version = version + 1
    WHERE id = ? AND user_id = ?
    ```
16. System commits transaction
17. System returns 200 OK with updated card:
    ```json
    {
      "card": {
        "id": "card-uuid-123",
        "front": "What is the capital of France?",
        "back": "Paris is the capital and largest city of France.",
        "current_box": 3,
        "due_date": "2025-01-20",
        "updated_at": "2025-01-28T14:45:00Z"
      },
      "message": "Card updated successfully"
    }
    ```
18. Client receives response
19. Client closes edit modal
20. Client updates card display with new content:
    - Front view updated with new front text
    - Back view updated with new back text (if Back was visible)
21. Client maintains current review state:
    - If Front was shown, Front remains shown
    - If Back was shown, Back remains shown
22. User sees updated card content and continues reviewing

## 6. Alternative Flows

### 6a. Validation Error - Empty Front

**Trigger:** Step 10 or 12 - Front field is empty

1. Client validates or backend validates
2. Front.trim().length === 0
3. System returns 400 Bad Request:
   ```json
   {
     "error": "Validation failed",
     "details": [
       {
         "field": "front",
         "message": "Front cannot be empty"
       }
     ]
   }
   ```
4. Client displays validation error below Front field
5. Edit modal remains open
6. User must enter front text
7. Return to Step 7 (Main Flow)

### 6b. Validation Error - Empty Back

**Trigger:** Step 10 or 12 - Back field is empty

1. Client validates or backend validates
2. Back.trim().length === 0
3. System returns 400 Bad Request:
   ```json
   {
     "error": "Validation failed",
     "details": [
       {
         "field": "back",
         "message": "Back cannot be empty"
       }
     ]
   }
   ```
4. Client displays validation error below Back field
5. Edit modal remains open
6. User must enter back text
7. Return to Step 8 (Main Flow)

### 6c. Validation Error - Content Too Long

**Trigger:** Step 10 or 12 - Front or Back exceeds 5000 characters

1. System validates length
2. front.length > 5000 OR back.length > 5000
3. System returns 400 Bad Request:
   ```json
   {
     "error": "Validation failed",
     "details": [
       {
         "field": "front",
         "message": "Front must be 5000 characters or less"
       }
     ]
   }
   ```
4. Client displays validation error with character count
5. Edit modal remains open
6. User must shorten content
7. Return to Step 7 (Main Flow)

### 6d. Cancel Edit

**Trigger:** Step 9 - User clicks Cancel button

1. User decides not to save changes
2. User clicks "Cancel" button
3. Client closes edit modal without saving
4. No API request sent
5. User returns to review view with original card content
6. Review state unchanged
7. Use case ends (no changes)

### 6e. Card Not Found or Deleted

**Trigger:** Step 14 - Card doesn't exist or was deleted

1. System queries for card
2. Card not found or deleted_at IS NOT NULL
3. System returns 404 Not Found:
   ```json
   {
     "error": "Card not found",
     "message": "Card has been deleted and cannot be edited"
   }
   ```
4. Client displays error
5. Client refreshes session (removes deleted card from queue)
6. Use case ends (failure)

### 6f. Unauthorized Access - Card Belongs to Another User

**Trigger:** Step 14 - Card doesn't belong to authenticated user

1. System queries for card with user_id check
2. Card exists but user_id doesn't match
3. System returns 403 Forbidden:
   ```json
   {
     "error": "Forbidden",
     "message": "You do not have permission to edit this card"
   }
   ```
4. Client displays error
5. Edit modal closed
6. Use case ends (failure)

### 6g. Concurrency Conflict - Card Modified Concurrently

**Trigger:** Step 15 - Card was modified by another request

1. System attempts to update card
2. Optimistic locking detects version mismatch
3. Card was updated in another tab/session
4. System returns 409 Conflict:
   ```json
   {
     "error": "Concurrent modification",
     "message": "Card was modified in another session. Please refresh and try again.",
     "currentVersion": {
       "front": "Current front text",
       "back": "Current back text"
     }
   }
   ```
5. Client displays conflict dialog:
   - Shows current version
   - Offers options: "Refresh and retry" or "Cancel"
6. If user chooses refresh:
   - Client reloads card content
   - Edit modal reopens with current content
   - User can modify and save again
7. If user chooses cancel:
   - Edit modal closes
   - Review continues with current card content
8. Use case ends (failure or retry)

### 6h. Session Expired During Edit

**Trigger:** Step 12 - Access token expired

1. User's access token expires while editing
2. System returns 401 Unauthorized
3. Client attempts token refresh (UC-003)
4. If refresh succeeds:
   - Retry update request with new token
   - Continue to Step 17 (Main Flow)
5. If refresh fails:
   - Client redirects to login
   - Edit changes lost (user must re-enter)
   - Use case ends (failure)

### 6i. Database Transaction Failure

**Trigger:** Step 16 - Database error during commit

1. System attempts to commit transaction
2. Database error occurs (connection lost, constraint violation)
3. Transaction automatically rolled back
4. No card content updated
5. System logs error with details
6. System returns 500 Internal Server Error:
   ```json
   {
     "error": "Internal server error",
     "message": "Failed to update card. Please try again."
   }
   ```
7. Client displays error message
8. Edit modal remains open with user's input preserved
9. User can retry save
10. Use case ends (failure)

### 6j. No Changes Made

**Trigger:** Step 11 - User saves without modifying content

1. System detects front and back unchanged
2. System can either:
   - Option A: Return success immediately without DB update
   - Option B: Proceed with update (refresh updated_at timestamp)
3. MVP choice: Proceed with update, refresh updated_at
4. Card updated with same content, updated_at refreshed
5. Success message displayed
6. Use case ends (success)

## 7. Special Requirements

### 7.1 Performance

- Response time < 500ms for card update
- Edit modal should open instantly (< 100ms)
- Database transaction should be atomic
- Optimistic locking to prevent conflicts

### 7.2 Content Validation

- **Front:** Required, 1-5000 characters, trim whitespace
- **Back:** Required, 1-5000 characters, trim whitespace
- **Rich text:** MVP supports plain text only, markdown/HTML support in future
- **Character encoding:** UTF-8 support for international characters

### 7.3 SRS State Preservation

- **Critical:** Editing must NOT reset SRS state
- current_box remains unchanged
- due_date remains unchanged
- last_reviewed_at remains unchanged
- Rationale: User should be able to fix typos without losing progress

### 7.4 Usability

- **Draft preservation:** Preserve user input if validation fails
- **Auto-save:** Optional auto-save draft (future enhancement)
- **Keyboard shortcuts:** 
  - Ctrl+S / Cmd+S: Save
  - Esc: Cancel
- **Focus management:** Auto-focus on first field when modal opens
- **Accessibility:** 
  - Modal focus trap
  - ARIA labels
  - Keyboard navigation
  - Screen reader announcements

### 7.5 Edit Modal Behavior

- **Non-blocking:** User can still see card behind modal (semi-transparent overlay)
- **Responsive:** Modal adapts to screen size (mobile-friendly)
- **Scrollable:** Long content can be scrolled within modal
- **Character counter:** Show remaining characters (5000 - current length)

## 8. Technology and Data Variations

### 8.1 Optimistic Locking

```sql
-- Add version column to cards table
ALTER TABLE cards ADD COLUMN version INTEGER DEFAULT 1;

-- Update with version check
UPDATE cards
SET front = ?,
    back = ?,
    updated_at = CURRENT_TIMESTAMP,
    version = version + 1
WHERE id = ? 
  AND user_id = ?
  AND version = ?; -- Expected version

-- If rows affected = 0, version mismatch occurred
```

### 8.2 Content Validation

```typescript
interface CardUpdateRequest {
  front: string;
  back: string;
}

const validateCardContent = (
  request: CardUpdateRequest
): ValidationResult => {
  const errors: ValidationError[] = [];

  if (!request.front || request.front.trim().length === 0) {
    errors.push({
      field: 'front',
      message: 'Front cannot be empty'
    });
  }

  if (request.front.length > 5000) {
    errors.push({
      field: 'front',
      message: 'Front must be 5000 characters or less'
    });
  }

  if (!request.back || request.back.trim().length === 0) {
    errors.push({
      field: 'back',
      message: 'Back cannot be empty'
    });
  }

  if (request.back.length > 5000) {
    errors.push({
      field: 'back',
      message: 'Back must be 5000 characters or less'
    });
  }

  return {
    isValid: errors.length === 0,
    errors
  };
};
```

### 8.3 Edit Modal State Management

```typescript
interface EditModalState {
  front: string;
  back: string;
  originalFront: string;
  originalBack: string;
  isDirty: boolean; // Has user made changes?
  isSaving: boolean;
  errors: ValidationError[];
}

const hasChanges = (state: EditModalState): boolean => {
  return state.front !== state.originalFront ||
         state.back !== state.originalBack;
};

// Warn before closing if unsaved changes
const handleCancel = (state: EditModalState) => {
  if (hasChanges(state)) {
    if (confirm('You have unsaved changes. Are you sure you want to cancel?')) {
      closeModal();
    }
  } else {
    closeModal();
  }
};
```

### 8.4 Character Counter

```typescript
const CharacterCounter = ({ text, maxLength }: Props) => {
  const remaining = maxLength - text.length;
  const isNearLimit = remaining < 100;

  return (
    <div className={isNearLimit ? 'text-warning' : 'text-muted'}>
      {remaining} characters remaining
    </div>
  );
};
```

## 9. Frequency of Occurrence

- Expected: 1-3 edits per session per user
- Occasional; mainly for correcting typos or improving content
- Per user: 5-20 edits per month
- Total: 50-200 edits/day (MVP phase)

## 10. Open Issues

- **Rich text editing:** Markdown or HTML support (future)
- **Image/media support:** Add images to cards (future)
- **Reset SRS option:** Optional "Reset box to 1 after major edits" (future)
- **Edit history:** Track edit history with diffs (future)
- **Collaborative editing:** Multiple users editing same card (future)
- **Auto-save draft:** Save draft while editing (future)

## 11. Related Use Cases

- [UC-019: Update Card](UC-019-update-card.md) - General card update (outside review)
- [UC-023: Review Cards (SRS)](UC-023-review-cards-srs.md) - Review session context
- [UC-024: Rate Card](UC-024-rate-card.md) - Rate card after editing

## 12. Business Rules References

- **BR-CARD-01:** Front/Back required, <= 5000 characters
- **BR-CARD-04:** Editing does not reset SRS state by default

## 13. UI Mockup Notes

### Edit Modal

```
┌─────────────────────────────────────────┐
│ Edit Card                        [×]    │
├─────────────────────────────────────────┤
│                                         │
│ Front *                                 │
│ ┌─────────────────────────────────────┐ │
│ │ What is the capital of France?      │ │
│ │                                     │ │
│ │                                     │ │
│ └─────────────────────────────────────┘ │
│ 45 / 5000 characters                    │
│                                         │
│ Back *                                  │
│ ┌─────────────────────────────────────┐ │
│ │ Paris is the capital and largest    │ │
│ │ city of France.                     │ │
│ │                                     │ │
│ └─────────────────────────────────────┘ │
│ 85 / 5000 characters                    │
│                                         │
│ ⚠️ Editing will not reset SRS progress  │
│                                         │
│ [Cancel]        [Save and Continue]     │
└─────────────────────────────────────────┘
```

### Conflict Resolution Dialog

```
┌─────────────────────────────────────────┐
│ ⚠️ Conflict Detected                     │
├─────────────────────────────────────────┤
│                                         │
│ This card was modified in another       │
│ session.                                │
│                                         │
│ Your changes:                           │
│ Front: "What is the capitol..."         │
│                                         │
│ Current version:                        │
│ Front: "What is the capital..."         │
│                                         │
│ [Cancel]  [Refresh and Retry]         │
└─────────────────────────────────────────┘
```

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
  "front": "What is the capital of France?",
  "back": "Paris is the capital and largest city of France."
}
```

**Success Response (200 OK):**

```json
{
  "card": {
    "id": "card-uuid-123",
    "front": "What is the capital of France?",
    "back": "Paris is the capital and largest city of France.",
    "current_box": 3,
    "due_date": "2025-01-20",
    "last_reviewed_at": "2025-01-25T10:30:00Z",
    "updated_at": "2025-01-28T14:45:00Z",
    "version": 2
  },
  "message": "Card updated successfully"
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
      "message": "Front cannot be empty"
    },
    {
      "field": "back",
      "message": "Back must be 5000 characters or less"
    }
  ]
}
```

401 Unauthorized:

```json
{
  "error": "Unauthorized",
  "message": "Authentication required"
}
```

403 Forbidden - Card belongs to another user:

```json
{
  "error": "Forbidden",
  "message": "You do not have permission to edit this card"
}
```

404 Not Found - Card not found:

```json
{
  "error": "Card not found",
  "message": "Card has been deleted and cannot be edited"
}
```

409 Conflict - Concurrent modification:

```json
{
  "error": "Concurrent modification",
  "message": "Card was modified in another session. Please refresh and try again.",
  "currentVersion": {
    "front": "Current front text",
    "back": "Current back text"
  }
}
```

500 Internal Server Error:

```json
{
  "error": "Internal server error",
  "message": "Failed to update card. Please try again."
}
```

## 15. Test Cases

### TC-027-001: Edit Card Successfully

- **Given:** User viewing card with front "capitol" (typo)
- **When:** User edits front to "capital" and saves
- **Then:** Card updated, SRS state unchanged, user continues reviewing

### TC-027-002: Empty Front Validation

- **Given:** User clears front field
- **When:** User clicks Save
- **Then:** Validation error "Front cannot be empty", modal remains open

### TC-027-003: Content Too Long

- **Given:** User enters front text with 5001 characters
- **When:** User clicks Save
- **Then:** Validation error "Front must be 5000 characters or less"

### TC-027-004: Cancel Edit

- **Given:** User modifies card content
- **When:** User clicks Cancel
- **Then:** Modal closes, no changes saved, original content shown

### TC-027-005: Concurrent Edit Conflict

- **Given:** User editing card in Tab 1
- **When:** Card updated in Tab 2, then user saves in Tab 1
- **Then:** 409 Conflict, dialog shows current version, option to refresh

### TC-027-006: SRS State Preserved

- **Given:** Card in Box 5, due_date = "2025-01-25"
- **When:** User edits card content
- **Then:** Card remains in Box 5, due_date unchanged, only content updated

### TC-027-007: Session Expired During Edit

- **Given:** User's access token expires while editing
- **When:** User saves card
- **Then:** Token refreshed automatically, save succeeds

### TC-027-008: Card Deleted During Edit

- **Given:** User editing card
- **When:** Card deleted in another tab, then user saves
- **Then:** 404 error "Card not found", session refreshed

### TC-027-009: Keyboard Shortcut Save

- **Given:** User in edit modal
- **When:** User presses Ctrl+S (Cmd+S on Mac)
- **Then:** Save action triggered, card updated

### TC-027-010: No Changes Made

- **Given:** User opens edit modal, makes no changes
- **When:** User clicks Save
- **Then:** Card updated (updated_at refreshed), success message shown

### TC-027-011: Character Counter Updates

- **Given:** User in edit modal with character counter
- **When:** User types text
- **Then:** Counter updates in real-time, shows warning when near limit

### TC-027-012: Draft Preserved on Error

- **Given:** User enters invalid content (empty front)
- **When:** User clicks Save, validation fails
- **Then:** Modal remains open, user's input preserved, error shown

## 16. Database Schema Reference

### cards table

```sql
CREATE TABLE cards (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  deck_id UUID NOT NULL REFERENCES decks(id) ON DELETE CASCADE,
  front TEXT NOT NULL CHECK (LENGTH(front) > 0 AND LENGTH(front) <= 5000),
  back TEXT NOT NULL CHECK (LENGTH(back) > 0 AND LENGTH(back) <= 5000),
  current_box INTEGER NOT NULL DEFAULT 1 CHECK (current_box >= 1 AND current_box <= 7),
  due_date DATE NOT NULL DEFAULT CURRENT_DATE,
  last_reviewed_at TIMESTAMP,
  version INTEGER DEFAULT 1, -- For optimistic locking
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at TIMESTAMP
);

CREATE INDEX idx_cards_user_id ON cards(user_id);
CREATE INDEX idx_cards_deck_id ON cards(deck_id);
CREATE INDEX idx_cards_due_date ON cards(due_date);
```
