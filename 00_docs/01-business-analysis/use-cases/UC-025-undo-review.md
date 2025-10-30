# UC-025: Undo Review

## 1. Brief Description

User undoes the most recent rating applied in the current review session, restoring the card's SRS state (current_box, due_date) and the session queue to the state before the rating was applied.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** Review Service, SRS Engine

## 3. Preconditions

- User is authenticated with valid access token
- An active review session exists with sessionId
- There is at least one recent rating event in this session that is eligible for undo
- Last rating was applied within the undo time window (default: 2 minutes)

## 4. Postconditions

### Success Postconditions

- The last rating is reverted
- Card's SRS fields are restored to previous values:
  - current_box restored to previous value
  - due_date restored to previous value
  - last_reviewed_at restored to previous value (or set to null)
- Review log entry removed or marked as undone
- Session queue returns to the state before the rating
- Card is re-inserted at appropriate position (typically as current card)
- UI displays the restored card (Front view)
- Undo countdown timer reset

### Failure Postconditions

- No changes applied to card SRS state
- No changes to review log
- No changes to session queue
- Error message displayed
- User remains on current card

## 5. Main Success Scenario (Basic Flow)

1. User is in active review session
2. User rates a card (e.g., clicks "GOOD" button)
3. System processes rating and shows next card
4. User realizes they made a mistake (e.g., clicked wrong button)
5. User clicks "Undo" button (or presses keyboard shortcut "U")
6. Client sends POST request to undo endpoint with sessionId
7. Backend validates request:
   - Session exists and is active
   - Session belongs to authenticated user
   - Last action in session is a rating (not skip or other action)
   - Last rating was applied within undo time window (default: 2 minutes)
8. System retrieves last review log entry for this session:
   ```sql
   SELECT id, card_id, rating, time_taken_ms, created_at
   FROM review_logs
   WHERE session_id = ? AND user_id = ?
   ORDER BY created_at DESC
   LIMIT 1
   ```
9. System retrieves card's current SRS state:
   ```sql
   SELECT id, current_box, due_date, last_reviewed_at
   FROM cards
   WHERE id = ? AND user_id = ? AND deleted_at IS NULL
   ```
10. System retrieves previous SRS state from review log snapshot or calculates from rating:
    - If snapshot stored: Load previous state
    - Otherwise: Reverse-engineer from rating and current state
11. System starts database transaction
12. System restores card's SRS state:
    ```sql
    UPDATE cards
    SET current_box = ?,
        due_date = ?,
        last_reviewed_at = ?,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = ? AND user_id = ?
    ```
13. System marks review log entry as undone:
    ```sql
    UPDATE review_logs
    SET undone_at = CURRENT_TIMESTAMP
    WHERE id = ?
    ```
    OR deletes it:
    ```sql
    DELETE FROM review_logs WHERE id = ?
    ```
14. System decrements daily review counter for user
15. System re-inserts card into session queue at appropriate position:
    - Typically as current card (head of queue)
    - Or based on restored due_date
16. System commits transaction
17. System returns 200 OK with restored card:
    ```json
    {
      "card": {
        "id": "card-uuid-123",
        "front": "What is the capital of France?",
        "back": "Paris",
        "current_box": 3,
        "due_date": "2025-01-20"
      },
      "message": "Rating undone successfully"
    }
    ```
18. Client receives response
19. Client updates UI:
    - Displays restored card (Front view)
    - Hides/removes undo button if no more actions to undo
    - Updates progress indicator (decrements completed count)
    - Resets timer for time_taken tracking
20. User sees the card they just rated, ready to rate again

## 6. Alternative Flows

### 6a. No Action to Undo

**Trigger:** Step 7 - No eligible last action found

1. System checks for last review log entry in session
2. No review log entry found (session just started or all ratings undone)
3. System returns 400 Bad Request:
   ```json
   {
     "error": "Nothing to undo",
     "message": "No recent rating found to undo in this session"
   }
   ```
4. Client displays error message
5. User remains on current card
6. Use case ends (failure)

### 6b. Undo Time Window Expired

**Trigger:** Step 7 - Last rating was applied outside undo time window

1. System checks timestamp of last review log entry
2. Time elapsed > undo time window (default: 2 minutes)
3. System returns 400 Bad Request:
   ```json
   {
     "error": "Undo window expired",
     "message": "Undo is only available for ratings within the last 2 minutes"
   }
   ```
4. Client displays error message with countdown timer (if UI was showing it)
5. Undo button is disabled/greyed out
6. Use case ends (failure)

**Note:** Time window is configurable in SRS settings (default: 2 minutes)

### 6c. Session Not Found or Expired

**Trigger:** Step 7 - Session doesn't exist or expired

1. System queries for session
2. Session not found or expired (timeout > 2 hours)
3. System returns 404 Not Found:
   ```json
   {
     "error": "Session not found",
     "message": "Review session has expired. Please start a new session."
   }
   ```
4. Client redirects to deck/folder view
5. User must start new review session
6. Use case ends (failure)

### 6d. Card Not Found or Deleted

**Trigger:** Step 9 - Card doesn't exist or was deleted

1. System queries for card from review log
2. Card not found or deleted_at IS NOT NULL
3. System returns 404 Not Found:
   ```json
   {
     "error": "Card not found",
     "message": "Card has been deleted and cannot be restored"
   }
   ```
4. Client displays error and refreshes session
5. Use case ends (failure)

### 6e. Concurrency Conflict - Card Modified

**Trigger:** Step 12 - Card was edited or rated in parallel tab

1. System attempts to update card
2. Card was modified by another request (optimistic locking detects conflict)
3. System detects version mismatch or concurrent modification
4. System rolls back transaction
5. System returns 409 Conflict:
   ```json
   {
     "error": "Concurrent modification",
     "message": "Card was modified in another session. Please refresh and try again."
   }
   ```
6. Client displays error message
7. Client offers option to refresh session
8. Use case ends (failure)

### 6f. Database Transaction Failure

**Trigger:** Step 16 - Database error during commit

1. System attempts to commit transaction
2. Database error occurs (connection lost, constraint violation)
3. Transaction automatically rolled back
4. No card state changed, no review log updated
5. System logs error with details
6. System returns 500 Internal Server Error:
   ```json
   {
     "error": "Internal server error",
     "message": "Failed to undo rating. Please try again."
   }
   ```
7. Client displays error message
8. User can retry undo
9. Use case ends (failure)

### 6g. Undo After Multiple Ratings

**Trigger:** Step 7 - User wants to undo but there are multiple ratings in session

1. System checks review log history
2. Multiple ratings exist in session
3. System allows undo of only the most recent rating (per BR-REV-05)
4. Last rating is undone successfully
5. If user wants to undo more, they must click Undo again (if within time window)
6. Continue to Step 17 (Main Flow)

**Note:** Multi-step undo history is out of MVP scope

## 7. Special Requirements

### 7.1 Performance

- Response time < 500ms for undo operation
- Database transaction must be atomic (all or nothing)
- Efficient lookup of last review log entry (index on session_id, created_at)

### 7.2 Undo Time Window

- Default: 2 minutes from rating time
- Configurable per user in SRS settings
- Countdown timer shown in UI (optional)
- Window expires based on review_log.created_at timestamp
- Rationale: Prevents abuse, ensures undo is for genuine mistakes

### 7.3 State Restoration

- **Snapshot Approach:** Store previous state before each rating
  - Pros: Accurate restoration, handles complex state changes
  - Cons: Additional storage overhead
- **Reverse Calculation:** Calculate previous state from rating and current state
  - Pros: No extra storage
  - Cons: May not handle all edge cases perfectly
- **MVP Choice:** Reverse calculation for simplicity

### 7.4 Review Log Handling

Two approaches for handling undone review logs:

- **Soft Delete:** Mark as undone (undone_at timestamp)
  - Pros: Preserves audit trail, allows analytics
  - Cons: Need to filter undone entries in queries
- **Hard Delete:** Remove entry completely
  - Pros: Clean data, simpler queries
  - Cons: Lose audit trail

**MVP Choice:** Soft delete (mark undone_at) for audit trail

### 7.5 Session Queue Management

- Card must be re-inserted at correct position based on restored due_date
- If card was at head of queue before rating, restore to head
- Maintain queue ordering: due_date ASC, current_box ASC

## 8. Technology and Data Variations

### 8.1 Undo Window Configuration

```typescript
interface SRSSettings {
  undoWindowSeconds: number; // Default: 120 (2 minutes)
}

// Check if undo is allowed
const canUndo = (lastRatingTime: Date, undoWindowSeconds: number): boolean => {
  const elapsedSeconds = (Date.now() - lastRatingTime.getTime()) / 1000;
  return elapsedSeconds <= undoWindowSeconds;
};
```

### 8.2 State Restoration Logic

```typescript
const restoreCardState = (
  currentBox: number,
  currentDueDate: Date,
  rating: 'AGAIN' | 'HARD' | 'GOOD' | 'EASY',
  settings: SRSSettings
): { previousBox: number; previousDueDate: Date } => {
  // Reverse-engineer previous state
  let previousBox = currentBox;
  let previousDueDate = currentDueDate;

  switch (rating) {
    case 'AGAIN':
      // Card was moved to box 1, previous box unknown
      // Use box 1 as fallback or restore from snapshot
      previousBox = 1; // Simplified
      break;
    case 'HARD':
      // Box unchanged, due_date was reduced
      previousDueDate = addDays(currentDueDate, 1 / 0.7); // Approximate
      break;
    case 'GOOD':
      previousBox = Math.max(1, currentBox - 1);
      const interval = settings.boxIntervals[previousBox];
      previousDueDate = subtractDays(currentDueDate, interval);
      break;
    case 'EASY':
      previousBox = Math.max(1, currentBox - 2);
      const interval = settings.boxIntervals[previousBox];
      previousDueDate = subtractDays(currentDueDate, interval);
      break;
  }

  return { previousBox, previousDueDate };
};
```

### 8.3 Review Log Snapshot (Future Enhancement)

```sql
CREATE TABLE review_log_snapshots (
  id UUID PRIMARY KEY,
  review_log_id UUID REFERENCES review_logs(id),
  card_id UUID NOT NULL,
  previous_box INTEGER,
  previous_due_date DATE,
  previous_last_reviewed_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 8.4 Daily Counter Adjustment

```sql
-- Decrement daily review counter
UPDATE user_stats
SET reviews_today = GREATEST(0, reviews_today - 1),
    updated_at = CURRENT_TIMESTAMP
WHERE user_id = ?
  AND stat_date = CURRENT_DATE;
```

## 9. Frequency of Occurrence

- Expected: 5-10% of ratings are undone
- Per session: 1-5 undo operations
- Occasional; mainly for correcting mistakes or accidental clicks
- Peak: During learning phase when users are still learning the interface

## 10. Open Issues

- **Multi-step undo:** History of undo operations (future)
- **Undo across sessions:** Allow undo from previous session (future, complex)
- **Undo after skip:** Handle undo of skip action (future)
- **Undo countdown UI:** Show countdown timer in UI (optional enhancement)
- **Snapshot storage:** Store full state snapshots for accurate restoration (future)

## 11. Related Use Cases

- [UC-023: Review Cards (SRS)](UC-023-review-cards-srs.md) - Start review session
- [UC-024: Rate Card](UC-024-rate-card.md) - Apply rating that can be undone
- [UC-026: Skip Card](UC-026-skip-card.md) - Skip action (undo not applicable)
- [UC-027: Edit Card During Review](UC-027-edit-card-during-review.md) - Edit card content

## 12. Business Rules References

- **BR-REV-05:** Only the most recent rating in the current session can be undone
- **BR-REV-06:** Undo window may be time-limited (configurable, default: 2 minutes)

## 13. UI Mockup Notes

### Undo Button with Countdown

```
┌─────────────────────────────────────────┐
│ Review: IELTS Academic Words    [120]   │
├─────────────────────────────────────────┤
│                                         │
│  Progress: ████████░░░░░░░░░░  15/120  │
│                                         │
│  ┌───────────────────────────────────┐ │
│  │ Front:                            │ │
│  │ What does "advocate" mean?        │ │
│  │                                   │ │
│  │ Back:                             │ │
│  │ To publicly support or recommend  │ │
│  └───────────────────────────────────┘ │
│                                         │
│  [1] AGAIN    [2] HARD                  │
│  [3] GOOD     [4] EASY                  │
│                                         │
│  [Skip]  [Edit]  [Undo] ⏱ 1:45         │
│                    ↑                    │
│              Countdown timer             │
└─────────────────────────────────────────┘
```

### Toast Notification

After undo:
```
✓ Rating undone successfully
Card restored to previous state
```

## 14. API Endpoint

```http
POST /api/review/sessions/{sessionId}/undo
```

**Request Headers:**

```http
Authorization: Bearer <access-token>
Content-Type: application/json
```

**Request Body:**

```json
{}
```

(No body required - sessionId from URL path)

**Success Response (200 OK):**

```json
{
  "card": {
    "id": "card-uuid-123",
    "front": "What is the capital of France?",
    "back": "Paris",
    "current_box": 3,
    "due_date": "2025-01-20"
  },
  "message": "Rating undone successfully",
  "progress": {
    "completed": 14,
    "total": 120
  }
}
```

**Error Responses:**

400 Bad Request - Nothing to undo:

```json
{
  "error": "Nothing to undo",
  "message": "No recent rating found to undo in this session"
}
```

400 Bad Request - Undo window expired:

```json
{
  "error": "Undo window expired",
  "message": "Undo is only available for ratings within the last 2 minutes"
}
```

404 Not Found - Session not found:

```json
{
  "error": "Session not found",
  "message": "Review session has expired. Please start a new session."
}
```

404 Not Found - Card not found:

```json
{
  "error": "Card not found",
  "message": "Card has been deleted and cannot be restored"
}
```

409 Conflict - Concurrent modification:

```json
{
  "error": "Concurrent modification",
  "message": "Card was modified in another session. Please refresh and try again."
}
```

500 Internal Server Error:

```json
{
  "error": "Internal server error",
  "message": "Failed to undo rating. Please try again."
}
```

## 15. Test Cases

### TC-025-001: Undo Immediately After Rating

- **Given:** User rates card as GOOD, card moves from Box 3 to Box 4
- **When:** User clicks Undo within 10 seconds
- **Then:** Card restored to Box 3, review log marked undone, card shown as current

### TC-025-002: Undo After Time Window Expired

- **Given:** User rated card 3 minutes ago (window: 2 minutes)
- **When:** User clicks Undo
- **Then:** 400 error "Undo window expired", no changes applied

### TC-025-003: Undo When Session Expired

- **Given:** User's review session expired (> 2 hours idle)
- **When:** User tries to undo
- **Then:** 404 error "Session not found", redirect to deck view

### TC-025-004: Concurrent Edit Conflict

- **Given:** User rates card in Tab 1
- **When:** Card edited in Tab 2, then user tries to undo in Tab 1
- **Then:** 409 Conflict error, suggest refreshing session

### TC-025-005: Undo AGAIN Rating

- **Given:** User rates card as AGAIN (moved to Box 1)
- **When:** User clicks Undo
- **Then:** Card restored to previous box (or Box 1 if unknown), restored due_date

### TC-025-006: Undo EASY Rating (Box Jump)

- **Given:** User rates card as EASY, moved from Box 3 to Box 5
- **When:** User clicks Undo
- **Then:** Card restored to Box 3, due_date restored accordingly

### TC-025-007: Undo Multiple Times

- **Given:** User rates Card A, then rates Card B
- **When:** User clicks Undo twice (undo Card B, then undo Card A)
- **Then:** Both ratings undone, cards restored in reverse order

### TC-025-008: Undo When No Ratings Yet

- **Given:** User starts new session, no ratings applied
- **When:** User clicks Undo
- **Then:** 400 error "Nothing to undo"

### TC-025-009: Keyboard Shortcut Undo

- **Given:** User just rated card
- **When:** User presses "U" key
- **Then:** Undo action triggered, card restored

### TC-025-010: Daily Counter Decremented

- **Given:** User has reviewed 50 cards today
- **When:** User undoes a rating
- **Then:** Daily counter decremented to 49, review log marked undone

### TC-025-011: Card Deleted After Rating

- **Given:** User rates card, then card deleted in another tab
- **When:** User tries to undo rating
- **Then:** 404 error "Card not found", session refreshed

### TC-025-012: Database Transaction Rollback

- **Given:** Database connection fails during undo commit
- **When:** Undo transaction fails
- **Then:** 500 error, no changes applied, card state unchanged

## 16. Database Schema Reference

### review_logs table (undo support)

```sql
CREATE TABLE review_logs (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  card_id UUID NOT NULL REFERENCES cards(id) ON DELETE CASCADE,
  session_id UUID REFERENCES review_sessions(id) ON DELETE SET NULL,
  rating VARCHAR(20) NOT NULL CHECK (rating IN ('AGAIN', 'HARD', 'GOOD', 'EASY')),
  time_taken_ms INTEGER,
  undone_at TIMESTAMP, -- Timestamp when undone, NULL if not undone
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_review_logs_session_created ON review_logs(session_id, created_at DESC);
CREATE INDEX idx_review_logs_undone ON review_logs(undone_at) WHERE undone_at IS NULL;
```

### Cards table (SRS fields)

```sql
-- SRS-related columns in cards table
current_box INTEGER NOT NULL DEFAULT 1 CHECK (current_box >= 1 AND current_box <= 7),
due_date DATE NOT NULL DEFAULT CURRENT_DATE,
last_reviewed_at TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
```
