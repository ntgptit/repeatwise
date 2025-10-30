# UC-026: Skip Card

## 1. Brief Description

User skips the current card during a review session without changing its SRS state. The card is postponed and moved to the end of the session queue, allowing the user to review it later in the same session.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** Review Service

## 3. Preconditions

- User is authenticated with valid access token
- An active review session exists with sessionId
- A current card is available to display
- User has not exceeded skip limit (if configured)

## 4. Postconditions

### Success Postconditions

- Card SRS state unchanged (current_box, due_date, last_reviewed_at remain the same)
- Card moved to the end of the session queue
- Session queue updated
- Next card in queue displayed
- Skip count incremented for this session
- User continues reviewing with next card

### Failure Postconditions

- No changes to card SRS state
- No changes to session queue
- Error message displayed
- User remains on current card

## 5. Main Success Scenario (Basic Flow)

1. User is in active review session viewing a card
2. System displays card (Front or Back side)
3. User decides to skip this card (e.g., wants to review it later, needs more time)
4. User clicks "Skip" button (or presses keyboard shortcut "S")
5. Client sends POST request to skip endpoint:
   ```json
   {
     "cardId": "card-uuid-123",
     "sessionId": "session-uuid-456"
   }
   ```
6. Backend validates request:
   - Session exists and is active
   - Session belongs to authenticated user
   - Card belongs to current session
   - Skip limit not exceeded (if configured)
7. System retrieves current card from session queue
8. System checks skip limit for session:
   ```sql
   SELECT COUNT(*) as skip_count
   FROM review_logs
   WHERE session_id = ? AND rating = 'SKIP'
   ```
9. System validates skip count < max_skips_per_session (if limit configured)
10. System starts database transaction
11. System creates skip log entry (optional - for analytics):
    ```sql
    INSERT INTO review_logs (id, user_id, card_id, session_id, rating, created_at)
    VALUES (?, ?, ?, ?, 'SKIP', CURRENT_TIMESTAMP)
    ```
    OR simply moves card without logging (MVP choice: no log entry for skip)
12. System removes card from current position in session queue
13. System re-inserts card at the end of session queue:
    - Maintains queue ordering: due_date ASC, current_box ASC
    - Card will appear again later in session
14. System commits transaction
15. System retrieves next card from session queue:
    - Checks for remaining cards in queue
    - Applies ordering: due_date ASC, current_box ASC
16. System returns 200 OK with next card:
    ```json
    {
      "card": {
        "id": "card-uuid-456",
        "front": "What is the capital of Italy?",
        "back": "Rome"
      },
      "remaining": 118,
      "progress": {
        "completed": 1,
        "total": 120
      },
      "message": "Card skipped. Will appear again later in session."
    }
    ```
17. Client receives response
18. Client updates review UI:
    - Displays next card (Front view)
    - Updates progress indicator (remaining cards)
    - Resets timer for time_taken tracking
    - Shows brief notification: "Card skipped"
19. User continues reviewing with next card

## 6. Alternative Flows

### 6a. Skip Limit Reached

**Trigger:** Step 9 - User exceeded skip limit

1. System checks skip count for session
2. Skip count >= max_skips_per_session (e.g., 3 per session)
3. System returns 400 Bad Request:
   ```json
   {
     "error": "Skip limit reached",
     "message": "You have reached the maximum number of skips allowed per session (3). Please rate the card or continue with other cards."
   }
   ```
4. Client displays error message
5. Skip button is disabled/greyed out
6. User must rate the card or continue without skipping
7. Use case ends (failure)

**Note:** Skip limit is configurable (default: 3 per session, or unlimited if not configured)

### 6b. No Next Card Available - Session Complete

**Trigger:** Step 15 - Queue empty after skipping last card

1. System checks session queue after removing skipped card
2. Queue is empty (this was the last card)
3. System marks session as completed
4. System returns 200 OK with completion message:
   ```json
   {
     "message": "Session complete! Great work!",
     "summary": {
       "totalReviewed": 119,
       "skipped": 1,
       "durationSeconds": 1800
     }
   }
   ```
5. Client displays completion summary
6. Use case ends (success)

### 6c. Session Not Found or Expired

**Trigger:** Step 6 - Session doesn't exist or expired

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

**Trigger:** Step 7 - Card doesn't exist or was deleted

1. System queries for card from session
2. Card not found or deleted_at IS NOT NULL
3. System returns 404 Not Found:
   ```json
   {
     "error": "Card not found",
     "message": "Card has been deleted"
   }
   ```
4. Client displays error and refreshes session
5. Use case ends (failure)

### 6e. Card Not in Session Queue

**Trigger:** Step 7 - Card doesn't belong to current session

1. System checks if card belongs to session
2. Card not found in session queue
3. System returns 400 Bad Request:
   ```json
   {
     "error": "Invalid card",
     "message": "Card is not part of this review session"
   }
   ```
4. Client refreshes session
5. Use case ends (failure)

### 6f. Database Transaction Failure

**Trigger:** Step 14 - Database error during commit

1. System attempts to commit transaction
2. Database error occurs (connection lost, constraint violation)
3. Transaction automatically rolled back
4. No card moved, no queue updated
5. System logs error with details
6. System returns 500 Internal Server Error:
   ```json
   {
     "error": "Internal server error",
     "message": "Failed to skip card. Please try again."
   }
   ```
7. Client displays error message
8. User can retry skip
9. Use case ends (failure)

### 6g. Skip Already Skipped Card

**Trigger:** Step 7 - User tries to skip a card that was already skipped

1. System checks if card was already skipped in this session
2. Card already at end of queue (recently skipped)
3. System allows skip (moves to end again) OR returns error
4. MVP choice: Allow skip, card stays at end
5. Continue to Step 16 (Main Flow)

## 7. Special Requirements

### 7.1 Performance

- Response time < 300ms for skip operation
- Efficient queue manipulation (remove and re-insert)
- Database transaction should be atomic

### 7.2 Skip Behavior

- **No SRS State Change:** Skip does not affect card's box, due_date, or last_reviewed_at
- **Queue Position:** Card moved to end of queue, will appear again later in session
- **Re-skipping:** If card appears again and user skips again, it goes to end again
- **Session Persistence:** Skipped cards remain skipped if session is resumed later

### 7.3 Skip Limit Configuration

- **Default:** 3 skips per session (configurable)
- **Rationale:** Prevents abuse, encourages users to engage with cards
- **Unlimited Option:** Can be disabled (set to null or -1)
- **Per-session Counter:** Reset when new session starts

### 7.4 Skip vs Undo

- **Skip:** Forward action, moves card to end without rating
- **Undo:** Backward action, reverts previous rating
- Skip cannot be undone (different from rating undo)
- Skip is logged separately (if logging enabled)

## 8. Technology and Data Variations

### 8.1 Skip Limit Configuration

```typescript
interface SRSSettings {
  maxSkipsPerSession: number | null; // Default: 3, null = unlimited
}

// Check if skip is allowed
const canSkip = (
  currentSkipCount: number,
  maxSkips: number | null
): boolean => {
  if (maxSkips === null) return true; // Unlimited
  return currentSkipCount < maxSkips;
};
```

### 8.2 Queue Management

```typescript
interface SessionQueue {
  cards: Card[];
  currentIndex: number;
}

const skipCard = (
  queue: SessionQueue,
  cardId: string
): SessionQueue => {
  // Remove card from current position
  const card = queue.cards[queue.currentIndex];
  const newCards = queue.cards.filter((c) => c.id !== cardId);

  // Re-insert at end
  newCards.push(card);

  return {
    cards: newCards,
    currentIndex: queue.currentIndex // Stay at same index (next card)
  };
};
```

### 8.3 Skip Logging (Optional)

```sql
-- Option 1: Log skip in review_logs with rating='SKIP'
INSERT INTO review_logs (id, user_id, card_id, session_id, rating, created_at)
VALUES (?, ?, ?, ?, 'SKIP', CURRENT_TIMESTAMP);

-- Option 2: Separate skip_logs table (future)
CREATE TABLE skip_logs (
  id UUID PRIMARY KEY,
  user_id UUID REFERENCES users(id),
  card_id UUID REFERENCES cards(id),
  session_id UUID REFERENCES review_sessions(id),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 8.4 Skip Count Tracking

```sql
-- Count skips in current session
SELECT COUNT(*) as skip_count
FROM review_logs
WHERE session_id = ?
  AND rating = 'SKIP'
  AND created_at >= (
    SELECT created_at FROM review_sessions WHERE id = ?
  );
```

## 9. Frequency of Occurrence

- Expected: 2-5% of cards are skipped per session
- Per session: 2-6 skip operations (with limit of 3)
- Occasional; mainly for temporarily deferring difficult cards
- Peak: When users encounter unfamiliar or challenging cards

## 10. Open Issues

- **Skip logging:** Whether to log skip actions in review_logs (MVP: no logging)
- **Skip reason:** Allow user to specify reason for skipping (future)
- **Snooze until tomorrow:** Skip card and add to tomorrow's queue (future)
- **Skip analytics:** Track which cards are frequently skipped (future)
- **Skip limit per card:** Limit how many times same card can be skipped (future)

## 11. Related Use Cases

- [UC-023: Review Cards (SRS)](UC-023-review-cards-srs.md) - Start review session
- [UC-024: Rate Card](UC-024-rate-card.md) - Rate card instead of skipping
- [UC-025: Undo Review](UC-025-undo-review.md) - Undo previous rating (not applicable to skip)

## 12. Business Rules References

- **BR-REV-07:** Skip does not change SRS state (current_box, due_date unchanged)
- **BR-REV-08:** Skip limit per session (configurable, default: 3)

## 13. UI Mockup Notes

### Skip Button Placement

```
┌─────────────────────────────────────────┐
│ Review: IELTS Academic Words    [120]   │
├─────────────────────────────────────────┤
│                                         │
│  Progress: ████████░░░░░░░░░░  15/120  │
│                                         │
│  ┌───────────────────────────────────┐ │
│  │                                   │ │
│  │  What does "advocate" mean?       │ │
│  │                                   │ │
│  │  [Show Answer]                    │ │
│  │                                   │ │
│  └───────────────────────────────────┘ │
│                                         │
│  ⏱ 3.2s                                 │
│                                         │
│  [Skip]  [Edit]  [Undo]                 │
│   ↑                                      │
│   Skip button (greyed if limit reached)  │
└─────────────────────────────────────────┘
```

### Skip Limit Warning

```
⚠️ Skip Limit Reached

You have skipped 3 cards in this session.
Please rate cards or continue reviewing.

[Continue Reviewing]
```

### Skip Notification

After skipping:
```
⏭ Card skipped
Will appear again later in session
(Skips remaining: 2)
```

## 14. API Endpoint

```http
POST /api/review/sessions/{sessionId}/skip
```

**Request Headers:**

```http
Authorization: Bearer <access-token>
Content-Type: application/json
```

**Request Body:**

```json
{
  "cardId": "card-uuid-123"
}
```

**Success Response (200 OK) - Next Card:**

```json
{
  "card": {
    "id": "card-uuid-456",
    "front": "What is the capital of Italy?",
    "back": "Rome"
  },
  "remaining": 118,
  "progress": {
    "completed": 1,
    "total": 120
  },
  "skipsRemaining": 2,
  "message": "Card skipped. Will appear again later in session."
}
```

**Success Response (200 OK) - Session Complete:**

```json
{
  "message": "Session complete! Great work!",
  "summary": {
    "totalReviewed": 119,
    "skipped": 1,
    "durationSeconds": 1800
  }
}
```

**Error Responses:**

400 Bad Request - Skip limit reached:

```json
{
  "error": "Skip limit reached",
  "message": "You have reached the maximum number of skips allowed per session (3). Please rate the card or continue with other cards."
}
```

400 Bad Request - Card not in session:

```json
{
  "error": "Invalid card",
  "message": "Card is not part of this review session"
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
  "message": "Card has been deleted"
}
```

500 Internal Server Error:

```json
{
  "error": "Internal server error",
  "message": "Failed to skip card. Please try again."
}
```

## 15. Test Cases

### TC-026-001: Skip Card Successfully

- **Given:** User viewing card in Box 3 during review session
- **When:** User clicks Skip button
- **Then:** Card moved to end of queue, next card shown, SRS state unchanged

### TC-026-002: Skip Limit Reached

- **Given:** User has skipped 3 cards (limit: 3)
- **When:** User tries to skip 4th card
- **Then:** 400 error "Skip limit reached", skip button disabled

### TC-026-003: Skip Last Card - Session Complete

- **Given:** User on last card of 120-card session
- **When:** User skips the last card
- **Then:** Session complete message shown with summary

### TC-026-004: Skip Card - No SRS State Change

- **Given:** Card in Box 4, due_date = "2025-01-25"
- **When:** User skips card
- **Then:** Card remains in Box 4, due_date unchanged, only queue position changed

### TC-026-005: Skip Card Appears Again Later

- **Given:** User skips card at position 10 in queue
- **When:** User reviews remaining cards
- **Then:** Skipped card appears again at end of queue (position 120)

### TC-026-006: Skip Multiple Times Same Card

- **Given:** User skips card, card appears again later
- **When:** User skips same card again
- **Then:** Card moved to end again, skip count incremented

### TC-026-007: Session Expired During Skip

- **Given:** User's review session expired
- **When:** User tries to skip card
- **Then:** 404 error "Session not found", redirect to deck view

### TC-026-008: Keyboard Shortcut Skip

- **Given:** User viewing card
- **When:** User presses "S" key
- **Then:** Skip action triggered, card skipped

### TC-026-009: Skip Count Tracking

- **Given:** User skips 2 cards in session
- **When:** User skips 3rd card
- **Then:** Response shows skipsRemaining: 0, warning displayed

### TC-026-010: Skip Unavailable When Limit Reached

- **Given:** User reached skip limit (3 skips)
- **When:** User views next card
- **Then:** Skip button disabled/greyed out, tooltip shows limit message

### TC-026-011: Card Deleted During Session

- **Given:** Card deleted in another tab
- **When:** User tries to skip deleted card
- **Then:** 404 error "Card not found", session refreshed

### TC-026-012: Database Transaction Failure

- **Given:** Database connection fails during skip commit
- **When:** Skip transaction fails
- **Then:** 500 error, no queue changes, card remains in current position

## 16. Database Schema Reference

### review_logs table (skip support)

```sql
-- Add 'SKIP' to rating enum (if logging skips)
ALTER TABLE review_logs 
ALTER COLUMN rating TYPE VARCHAR(20) CHECK (rating IN ('AGAIN', 'HARD', 'GOOD', 'EASY', 'SKIP'));

-- Query skip count per session
SELECT COUNT(*) as skip_count
FROM review_logs
WHERE session_id = ?
  AND rating = 'SKIP';
```

### Session Queue Management

```sql
-- Cards in session queue (virtual/in-memory)
-- No separate table needed - calculated from cards table
-- Filtered by: session_id, due_date, current_box
-- Ordered by: due_date ASC, current_box ASC
```
