# UC-024: Rate Card

## 1. Brief Description

User rates the currently shown card during a review session using SRS ratings (AGAIN/HARD/GOOD/EASY). The system applies SRS Box algorithm to update card scheduling, computes next review date, logs the review event, and presents the next card in queue.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** Review Service, SRS Engine

## 3. Preconditions

- User is authenticated with valid access token
- An active review session exists with sessionId
- A current card is displayed (Back side revealed)
- User has not exceeded daily review limit

## 4. Postconditions

### Success Postconditions

- Card's SRS state updated in database:
  - current_box adjusted based on rating
  - due_date computed based on new box level
  - last_reviewed_at set to current timestamp
- Review log entry created with:
  - rating value
  - time_taken_ms
  - timestamp
- Session queue advances to next card
- UI displays next card or completion message
- Daily review counter incremented

### Failure Postconditions

- Card SRS state unchanged
- No review log entry created
- User remains on current card
- Error message displayed

## 5. Main Success Scenario (Basic Flow)

1. User is in active review session viewing card Back side
2. System displays four rating buttons: AGAIN / HARD / GOOD / EASY
3. System tracks time since card Front was revealed (for time_taken_ms)
4. User evaluates their recall performance
5. User clicks "GOOD" button (or presses keyboard shortcut "3")
6. Client captures rating metadata:
   - cardId (from current card)
   - rating: "GOOD"
   - timeTakenMs: elapsed time since Front revealed
7. Client sends POST request to rate endpoint
8. Backend validates request:
   - Session exists and is active
   - Card belongs to current session
   - Rating value is valid (AGAIN/HARD/GOOD/EASY)
   - User hasn't exceeded daily limit
9. System retrieves current card SRS state:
   ```sql
   SELECT id, current_box, due_date, last_reviewed_at
   FROM cards
   WHERE id = ? AND user_id = ? AND deleted_at IS NULL
   ```
10. System retrieves user's SRS settings (total_boxes, forgotten_card_action, box intervals)
11. System applies SRS Box algorithm based on rating:
    - **GOOD rating:** current_box = MIN(current_box + 1, total_boxes)
    - New box = 4 (incremented from 3)
12. System computes next due_date based on new box level:
    - Box 4 interval: 7 days
    - due_date = today + 7 days
13. System starts database transaction
14. System updates card record:
    ```sql
    UPDATE cards
    SET current_box = ?,
        due_date = ?,
        last_reviewed_at = CURRENT_TIMESTAMP,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = ? AND user_id = ?
    ```
15. System creates review log entry:
    ```sql
    INSERT INTO review_logs (id, user_id, card_id, session_id, rating, time_taken_ms, created_at)
    VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
    ```
16. System increments daily review counter for user
17. System commits transaction
18. System retrieves next card from session queue:
    - Checks for remaining cards in queue
    - Applies ordering: due_date ASC, current_box ASC
19. System returns 200 OK with next card data:
    ```json
    {
      "card": {
        "id": "card-uuid-456",
        "front": "What is the capital of France?",
        "back": "Paris"
      },
      "remaining": 119,
      "progress": {
        "completed": 1,
        "total": 120
      }
    }
    ```
20. Client receives response
21. Client updates review UI:
    - Displays next card (Front view)
    - Updates progress indicator (1/120 completed)
    - Resets timer for time_taken tracking
22. User continues reviewing

## 6. Alternative Flows

### 6a. Rating AGAIN - Forgotten Card Action

**Trigger:** Step 11 - User selects AGAIN rating

1. User clicks "AGAIN" button (forgot the answer)
2. System retrieves forgotten_card_action from SRS settings
3. System applies policy based on forgotten_card_action:
   
   **Case A: MOVE_TO_BOX_1 (default)**
   - System sets current_box = 1 (regardless of previous box)
   - System computes due_date based on Box 1 interval: 1 minute
   - due_date = today (card becomes due immediately)
   
   **Case B: MOVE_DOWN_N_BOXES**
   - System retrieves move_down_boxes value from SRS settings (1-3)
   - System calculates new box: current_box = MAX(1, current_box - move_down_boxes)
   - Example: Box 5 with move_down_boxes=2 → Box 3
   - Example: Box 2 with move_down_boxes=3 → Box 1 (capped at minimum)
   - System computes due_date based on new box interval
   - due_date = today + interval_days (for new box)
   
   **Case C: REPEAT_IN_SESSION**
   - System keeps current_box unchanged
   - System sets due_date = today (for immediate re-review)
   - Card will appear again later in current session
4. Card will appear again later in current session (if due_date = today)
5. Continue to Step 14 (Main Flow)

**Note:** MOVE_DOWN_N_BOXES provides flexibility - users can choose to move back less aggressively than resetting to Box 1

### 6b. Rating HARD - Keep Same Box or Decrement

**Trigger:** Step 11 - User selects HARD rating

1. User clicks "HARD" button (difficult recall)
2. System applies HARD policy from SRS settings
3. MVP policy: Keep same box (current_box unchanged)
4. Alternative policy (future): Decrement box by 1 (but not below 1)
5. System computes due_date based on current box interval with slight reduction:
   - Current box: 3, interval: 3 days
   - Hard penalty: 70% of normal interval
   - due_date = today + 2 days (3 * 0.7 ≈ 2)
6. Continue to Step 14 (Main Flow)

**Note:** HARD rating indicates difficulty without complete forgetting

### 6c. Rating EASY - Increment by 2 Boxes

**Trigger:** Step 11 - User selects EASY rating

1. User clicks "EASY" button (very easy recall)
2. System applies EASY bonus policy from SRS settings
3. MVP policy: Increment by 2 boxes
4. System calculates new box: current_box = MIN(current_box + 2, total_boxes)
5. Example: Box 3 → Box 5 (skips Box 4)
6. If already at Box 6 (near max 7): Box 6 → Box 7 (capped at max)
7. System computes due_date based on new box interval:
   - Box 5 interval: 14 days
   - due_date = today + 14 days
8. Continue to Step 14 (Main Flow)

**Note:** Rewards very easy cards with longer intervals

### 6d. Session Completed - No More Cards

**Trigger:** Step 18 - No remaining cards in queue

1. System checks session queue after rating current card
2. Queue is empty (all cards reviewed)
3. System marks session as completed
4. System returns 200 OK with completion message:
   ```json
   {
     "message": "Session complete! Great work!",
     "summary": {
       "totalReviewed": 120,
       "again": 15,
       "hard": 25,
       "good": 60,
       "easy": 20,
       "durationSeconds": 1800
     },
     "nextReviewDate": "2025-02-01T00:00:00Z"
   }
   ```
5. Client displays completion summary with statistics
6. Use case ends (success)

### 6e. Daily Limit Reached After Rating

**Trigger:** Step 16 - User exceeds max_reviews_per_day

1. System increments daily review counter
2. Counter exceeds user's max_reviews_per_day setting (e.g., 200)
3. System returns 200 OK with limit message:
   ```json
   {
     "message": "Daily limit reached. Come back tomorrow!",
     "summary": {
       "totalReviewed": 200,
       "limitReached": true
     }
   }
   ```
4. Client displays limit message
5. Session automatically ends
6. Use case ends (success)

**Note:** Prevents over-reviewing and encourages distributed practice

### 6f. Invalid Rating Value

**Trigger:** Step 8 - Rating value not in allowed set

1. Client sends rating: "MEDIUM" (invalid)
2. Backend validates rating against enum: [AGAIN, HARD, GOOD, EASY]
3. Validation fails
4. System returns 400 Bad Request:
   ```json
   {
     "error": "Invalid rating",
     "message": "Rating must be one of: AGAIN, HARD, GOOD, EASY"
   }
   ```
5. Client displays error (should not happen with proper UI)
6. User must select valid rating
7. Use case ends (failure)

### 6g. Card Not Found or Already Rated

**Trigger:** Step 9 - Card doesn't exist or doesn't belong to session

1. System queries for card
2. Card not found or deleted_at IS NOT NULL
3. System returns 404 Not Found:
   ```json
   {
     "error": "Card not found",
     "message": "Card does not exist or has been deleted"
   }
   ```
4. Client displays error and refreshes session
5. Use case ends (failure)

### 6h. Duplicate Rating - Concurrency Issue

**Trigger:** Step 15 - User double-clicks rating button

1. Client sends two rating requests simultaneously
2. First request processes successfully
3. Second request detects duplicate:
   - Same card_id + session_id already has recent review_log entry
   - Within last 5 seconds
4. System returns 409 Conflict:
   ```json
   {
     "error": "Duplicate rating",
     "message": "Card already rated. Showing next card."
   }
   ```
5. Client ignores duplicate response (first response already processed)
6. Use case continues normally

**Solution:** Client should disable rating buttons during API request

### 6i. Session Expired or Not Found

**Trigger:** Step 8 - Session doesn't exist or expired

1. User left session idle for > 2 hours (session timeout)
2. System queries for session
3. Session not found or expired
4. System returns 404 Not Found:
   ```json
   {
     "error": "Session not found",
     "message": "Review session has expired. Please start a new session."
   }
   ```
5. Client redirects to deck/folder view
6. User must start new review session
7. Use case ends (failure)

### 6j. Database Transaction Failure

**Trigger:** Step 17 - Database error during commit

1. System attempts to commit transaction
2. Database error occurs (connection lost, constraint violation)
3. Transaction automatically rolled back
4. No card state changed, no review log created
5. System logs error with details
6. System returns 500 Internal Server Error:
   ```json
   {
     "error": "Internal server error",
     "message": "Failed to save rating. Please try again."
   }
   ```
7. Client displays error message
8. User can retry rating
9. Use case ends (failure)

## 7. Special Requirements

### 7.1 Performance

- Response time < 500ms for rating operation
- Database transaction must be atomic (all or nothing)
- Prefetch next card to reduce perceived latency
- Index on (user_id, session_id, due_date, current_box) for fast queue queries

### 7.2 SRS Box Algorithm

MVP Implementation - Box Intervals (configurable):

| Box | Interval | Due Date Calculation |
|-----|----------|----------------------|
| 1   | 1 minute | today (immediate re-review) |
| 2   | 10 minutes | today (same session) |
| 3   | 3 days | today + 3 days |
| 4   | 7 days | today + 7 days |
| 5   | 14 days | today + 14 days |
| 6   | 30 days | today + 30 days |
| 7   | 60 days | today + 60 days |

Rating Rules:

- **AGAIN:** 
  - If forgotten_card_action = MOVE_TO_BOX_1: current_box = 1, due_date = today
  - If forgotten_card_action = MOVE_DOWN_N_BOXES: current_box = MAX(1, current_box - move_down_boxes), due_date = today + interval_for_new_box
  - If forgotten_card_action = REPEAT_IN_SESSION: current_box unchanged, due_date = today
- **HARD:** current_box unchanged, due_date = today + (interval * 0.7)
- **GOOD:** current_box + 1 (max: total_boxes), due_date = today + interval
- **EASY:** current_box + 2 (max: total_boxes), due_date = today + interval

### 7.3 Review Logging

Each review creates immutable log entry for:

- Analytics and statistics
- Study pattern analysis
- Undo functionality (UC-025)
- Progress tracking
- Spaced repetition optimization

### 7.4 Keyboard Shortcuts

For efficient reviewing:

- **1** or **Z:** AGAIN
- **2** or **X:** HARD
- **3** or **Space:** GOOD
- **4** or **C:** EASY
- **Enter:** Show answer (reveal Back)
- **U:** Undo last rating (UC-025)
- **S:** Skip card (UC-026)

## 8. Technology and Data Variations

### 8.1 SRS Algorithm Implementation

```typescript
interface SRSSettings {
  totalBoxes: number; // Default: 7
  forgottenCardAction: 'MOVE_TO_BOX_1'; // MVP
  boxIntervals: number[]; // Days: [0, 0, 3, 7, 14, 30, 60]
}

interface RatingResult {
  newBox: number;
  dueDate: Date;
}

const applyRating = (
  currentBox: number,
  rating: 'AGAIN' | 'HARD' | 'GOOD' | 'EASY',
  settings: SRSSettings
): RatingResult => {
  let newBox = currentBox;

  switch (rating) {
    case 'AGAIN':
      newBox = 1; // Forgotten
      break;
    case 'HARD':
      newBox = currentBox; // Keep same box
      break;
    case 'GOOD':
      newBox = Math.min(currentBox + 1, settings.totalBoxes);
      break;
    case 'EASY':
      newBox = Math.min(currentBox + 2, settings.totalBoxes);
      break;
  }

  const intervalDays = settings.boxIntervals[newBox];
  const dueDate = addDays(new Date(), intervalDays);

  return { newBox, dueDate };
};
```

### 8.2 Time Taken Tracking

```typescript
// Client-side timer
let reviewStartTime: number;

const showCardFront = () => {
  reviewStartTime = Date.now();
};

const rateCard = (rating: string) => {
  const timeTakenMs = Date.now() - reviewStartTime;

  api.rateCard({
    cardId,
    rating,
    timeTakenMs
  });
};
```

### 8.3 Daily Counter Management

```sql
-- Option 1: Store in user_stats table
UPDATE user_stats
SET reviews_today = reviews_today + 1,
    updated_at = CURRENT_TIMESTAMP
WHERE user_id = ?
  AND stat_date = CURRENT_DATE;

-- Option 2: Count from review_logs
SELECT COUNT(*)
FROM review_logs
WHERE user_id = ?
  AND DATE(created_at) = CURRENT_DATE;
```

## 9. Frequency of Occurrence

- **High frequency:** Core user activity during study sessions
- Per session: 20-200 ratings per user
- Per user per day: 50-500 ratings (depending on daily limit)
- Peak hours: Evening (6-10 PM) and lunch time (12-1 PM)
- Total system: 10,000-50,000 ratings/day (post-MVP)

## 10. Open Issues

- **Advanced SRS algorithms:** SM-2, SM-15, FSRS (future)
- **Adaptive intervals:** Adjust based on user performance history (future)
- **Card difficulty:** Track individual card difficulty score (future)
- **Ease factor:** Per-card ease adjustment like Anki (future)
- **Leech detection:** Flag cards stuck in low boxes (future)
- **Optimal review timing:** Time-of-day optimization (future)

## 11. Related Use Cases

- [UC-023: Review Cards (SRS)](UC-023-review-cards-srs.md) - Start review session
- [UC-025: Undo Review](UC-025-undo-review.md) - Undo last rating
- [UC-026: Skip Card](UC-026-skip-card.md) - Skip without rating
- [UC-027: Edit Card During Review](UC-027-edit-card-during-review.md) - Edit card content
- [UC-028: Configure SRS Settings](UC-028-configure-srs-settings.md) - Adjust SRS parameters

## 12. Business Rules References

- **BR-SRS-01:** Total boxes = 7 (default, configurable)
- **BR-SRS-02:** Forgotten card action = MOVE_TO_BOX_1
- **BR-SRS-03:** Due date grows exponentially by box level
- **BR-REV-02:** Respect daily limit (max_reviews_per_day)
- **BR-REV-03:** Order by due_date ASC, current_box ASC
- **BR-REV-04:** Time tracking for analytics

## 13. UI Mockup Notes

### Card Review Interface

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
│  [Skip]                [Edit]  [Undo]   │
└─────────────────────────────────────────┘
```

### After Revealing Answer

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
│  │ a cause or policy                 │ │
│  └───────────────────────────────────┘ │
│                                         │
│  ⏱ 5.8s                                 │
│                                         │
│  [1] AGAIN    [2] HARD                  │
│  <1m          3d                        │
│                                         │
│  [3] GOOD     [4] EASY                  │
│  7d           30d                       │
│                                         │
│  [Skip]                [Edit]  [Undo]   │
└─────────────────────────────────────────┘
```

### Session Complete

```
┌─────────────────────────────────────────┐
│ 🎉 Session Complete!                    │
├─────────────────────────────────────────┤
│                                         │
│  Great work! You reviewed 120 cards     │
│  in 30 minutes.                         │
│                                         │
│  Summary:                               │
│  ✅ Again:  15 (12%)                    │
│  ⚠️  Hard:   25 (21%)                    │
│  ✓  Good:   60 (50%)                    │
│  ⚡ Easy:   20 (17%)                    │
│                                         │
│  Next review: Tomorrow at 9:00 AM       │
│  (45 cards due)                         │
│                                         │
│  [Close]           [Review Statistics]  │
└─────────────────────────────────────────┘
```

## 14. API Endpoint

```http
POST /api/review/sessions/{sessionId}/rate
```

**Request Headers:**

```http
Authorization: Bearer <access-token>
Content-Type: application/json
```

**Request Body:**

```json
{
  "cardId": "550e8400-e29b-41d4-a716-446655440000",
  "rating": "GOOD",
  "timeTakenMs": 5800
}
```

**Success Response (200 OK) - Next Card:**

```json
{
  "card": {
    "id": "card-uuid-456",
    "front": "What is the capital of France?",
    "back": "Paris",
    "deckName": "Geography Basics"
  },
  "remaining": 119,
  "progress": {
    "completed": 1,
    "total": 120
  }
}
```

**Success Response (200 OK) - Session Complete:**

```json
{
  "message": "Session complete! Great work!",
  "summary": {
    "totalReviewed": 120,
    "again": 15,
    "hard": 25,
    "good": 60,
    "easy": 20,
    "durationSeconds": 1800
  },
  "nextReviewDate": "2025-02-01T00:00:00Z",
  "nextReviewCount": 45
}
```

**Success Response (200 OK) - Daily Limit Reached:**

```json
{
  "message": "Daily limit reached. Come back tomorrow!",
  "summary": {
    "totalReviewed": 200,
    "limitReached": true,
    "nextReviewDate": "2025-02-01T00:00:00Z"
  }
}
```

**Error Responses:**

400 Bad Request - Invalid rating:

```json
{
  "error": "Invalid rating",
  "message": "Rating must be one of: AGAIN, HARD, GOOD, EASY"
}
```

404 Not Found - Card not found:

```json
{
  "error": "Card not found",
  "message": "Card does not exist or has been deleted"
}
```

404 Not Found - Session expired:

```json
{
  "error": "Session not found",
  "message": "Review session has expired. Please start a new session."
}
```

409 Conflict - Duplicate rating:

```json
{
  "error": "Duplicate rating",
  "message": "Card already rated in this session"
}
```

500 Internal Server Error:

```json
{
  "error": "Internal server error",
  "message": "Failed to save rating. Please try again."
}
```

## 15. Test Cases

### TC-024-001: Rate Card GOOD - Box Increment

- **Given:** User viewing card in Box 3 during review session
- **When:** User rates card as GOOD
- **Then:** Card moves to Box 4, due_date = today + 7 days, next card shown

### TC-024-002: Rate Card AGAIN - Move to Box 1

- **Given:** Card in Box 5, user has forgotten_card_action = MOVE_TO_BOX_1
- **When:** User rates card AGAIN
- **Then:** Card moves to Box 1, due_date = today, may appear later in session

### TC-024-002b: Rate Card AGAIN - Move Down N Boxes

- **Given:** Card in Box 5, user has forgotten_card_action = MOVE_DOWN_N_BOXES, move_down_boxes = 2
- **When:** User rates card AGAIN
- **Then:** Card moves to Box 3 (5 - 2), due_date = today + 3 days (Box 3 interval)

### TC-024-002c: Rate Card AGAIN - Move Down N Boxes (Minimum Box)

- **Given:** Card in Box 2, user has forgotten_card_action = MOVE_DOWN_N_BOXES, move_down_boxes = 3
- **When:** User rates card AGAIN
- **Then:** Card moves to Box 1 (MAX(1, 2-3) = 1), due_date = today

### TC-024-002d: Rate Card AGAIN - Repeat in Session

- **Given:** Card in Box 4, user has forgotten_card_action = REPEAT_IN_SESSION
- **When:** User rates card AGAIN
- **Then:** Card stays in Box 4, due_date = today, appears again in session

### TC-024-003: Rate Card EASY - Skip Boxes

- **Given:** User viewing card in Box 3
- **When:** User rates card as EASY
- **Then:** Card moves to Box 5 (+2), due_date = today + 14 days

### TC-024-004: Rate Card EASY Near Max Box - Capped

- **Given:** User viewing card in Box 6 (max is 7)
- **When:** User rates card as EASY (+2 would be Box 8)
- **Then:** Card moves to Box 7 (capped at max), due_date = today + 60 days

### TC-024-005: Rate Card HARD - Same Box

- **Given:** User viewing card in Box 4
- **When:** User rates card as HARD
- **Then:** Card stays in Box 4, due_date = today + 5 days (70% of 7)

### TC-024-006: Session Complete After Last Card

- **Given:** User on last card of 120-card session
- **When:** User rates the last card
- **Then:** Session complete message shown with summary statistics

### TC-024-007: Daily Limit Reached

- **Given:** User has reviewed 199 cards today (limit: 200)
- **When:** User rates one more card
- **Then:** "Daily limit reached" message shown, session ends

### TC-024-008: Keyboard Shortcut - Press 3 for GOOD

- **Given:** User viewing card Back
- **When:** User presses "3" key
- **Then:** Card rated as GOOD, next card shown

### TC-024-009: Time Taken Tracking

- **Given:** User reveals card Front at T=0
- **When:** User rates card at T=5.8s
- **Then:** Review log records timeTakenMs = 5800

### TC-024-010: Duplicate Rating Prevented

- **Given:** User double-clicks GOOD button
- **When:** Second request arrives
- **Then:** 409 error returned, no duplicate review log created

### TC-024-011: Invalid Rating Value

- **Given:** API receives rating "MEDIUM"
- **When:** Backend validates rating
- **Then:** 400 error "Rating must be one of: AGAIN, HARD, GOOD, EASY"

### TC-024-012: Session Expired During Rating

- **Given:** User left session idle for 3 hours
- **When:** User tries to rate card
- **Then:** 404 error "Session expired", redirect to deck view

### TC-024-013: Card Deleted During Session

- **Given:** Card was deleted in another tab
- **When:** User tries to rate the deleted card
- **Then:** 404 error "Card not found", session refreshed

### TC-024-014: Database Transaction Rollback

- **Given:** Database connection fails during commit
- **When:** Rating transaction fails
- **Then:** 500 error, no card state changed, no review log created

### TC-024-015: Review Log Entry Created

- **Given:** User rates card successfully
- **When:** Transaction commits
- **Then:** review_logs table contains entry with card_id, rating, time_taken_ms

## 16. Database Schema Reference

### review_logs table

```sql
CREATE TABLE review_logs (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  card_id UUID NOT NULL REFERENCES cards(id) ON DELETE CASCADE,
  session_id UUID REFERENCES review_sessions(id) ON DELETE SET NULL,
  rating VARCHAR(20) NOT NULL CHECK (rating IN ('AGAIN', 'HARD', 'GOOD', 'EASY')),
  time_taken_ms INTEGER, -- Time from showing Front to rating
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_review_logs_user_id ON review_logs(user_id);
CREATE INDEX idx_review_logs_card_id ON review_logs(card_id);
CREATE INDEX idx_review_logs_session_id ON review_logs(session_id);
CREATE INDEX idx_review_logs_created_at ON review_logs(created_at);
```

### cards table (SRS fields)

```sql
-- SRS-related columns in cards table
current_box INTEGER NOT NULL DEFAULT 1 CHECK (current_box >= 1 AND current_box <= 7),
due_date DATE NOT NULL DEFAULT CURRENT_DATE,
last_reviewed_at TIMESTAMP,
```
