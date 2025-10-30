# UC-030: Random Mode

## 1. Brief Description

User reviews due cards in a randomized order (different from the default due_date/current_box ordering). Cards are selected based on due_date criteria but presented in random order. SRS updates still apply when rating cards.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** Review Service, SRS Engine

## 3. Preconditions

- User is authenticated with valid access token
- Scope selected (Deck or Folder)
- There are due cards in the chosen scope (due_date <= today)
- User has not exceeded daily review limit

## 4. Postconditions

### Success Postconditions

- A random-ordered review session is created
- Due cards are selected and randomized
- Ratings update SRS (boxes/due_date) as in normal review
- User can review due cards in random order

### Failure Postconditions

- No session created
- Error message displayed
- User remains on deck/folder view

## 5. Main Success Scenario (Basic Flow)

1. User navigates to a deck or folder
2. User clicks "Random Mode" button (or selects from review mode dropdown)
3. System checks for due cards in scope:
   ```sql
   SELECT COUNT(*) 
   FROM cards
   WHERE deck_id = ? 
     AND user_id = ?
     AND due_date <= CURRENT_DATE
     AND deleted_at IS NULL
   ```
4. System finds due cards available
5. Client sends POST request to create random session:
   ```json
   {
     "scope": {
       "type": "DECK",
       "id": "deck-uuid-123"
     }
   }
   ```
6. Backend validates request:
   - User is authenticated
   - Scope exists and belongs to user
   - Due cards exist in scope
7. System queries due cards (due_date <= today):
   ```sql
   SELECT id, front, back, current_box, due_date
   FROM cards
   WHERE deck_id = ?
     AND user_id = ?
     AND due_date <= CURRENT_DATE
     AND deleted_at IS NULL
   ORDER BY RANDOM()
   LIMIT 200
   ```
8. System randomizes card order using RANDOM()
9. System creates random session record:
   ```sql
   INSERT INTO review_sessions (id, user_id, session_type, scope_type, scope_id, 
                                created_at)
   VALUES (?, ?, 'RANDOM', 'DECK', ?, CURRENT_TIMESTAMP)
   ```
10. System returns 201 Created with session info and first card:
    ```json
    {
      "sessionId": "session-uuid-456",
      "sessionType": "RANDOM",
      "totalCards": 120,
      "card": {
        "id": "card-uuid-789",
        "front": "What is the capital of France?",
        "back": "Paris",
        "current_box": 3,
        "due_date": "2025-01-20"
      },
      "progress": {
        "current": 1,
        "total": 120
      }
    }
    ```
11. Client receives response
12. Client displays random session interface:
    - Shows card (Front view)
    - Shows "Random Mode" banner
    - Shows progress indicator
13. User reveals Back side and reviews answer
14. User rates card (AGAIN/HARD/GOOD/EASY)
15. System updates SRS state as normal (UC-024):
    - current_box adjusted based on rating
    - due_date computed based on new box level
    - Review log entry created
16. System returns next randomized card
17. User continues reviewing until:
    - All cards reviewed
    - Daily limit reached
    - User exits random mode

## 6. Alternative Flows

### 6a. No Due Cards Available

**Trigger:** Step 3 or 7 - No due cards found

1. System queries for due cards
2. No cards found (due_date > today)
3. System returns 200 OK with message:
   ```json
   {
     "message": "No cards to review today",
     "reason": "NO_DUE_CARDS",
     "nextReviewDate": "2025-01-30"
   }
   ```
4. Client displays message: "No cards to review today. Next review: 2025-01-30"
5. User can wait or use Cram Mode instead
6. Use case ends (failure)

### 6b. Daily Limit Reached

**Trigger:** Step 5 - User exceeded daily review limit

1. System checks daily review counter
2. Counter >= max_reviews_per_day
3. System returns 200 OK with message:
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
5. User must wait until tomorrow
6. Use case ends (failure)

### 6c. Scope Not Found

**Trigger:** Step 6 - Scope doesn't exist or doesn't belong to user

1. System queries for scope (deck or folder)
2. Scope not found or user_id doesn't match
3. System returns 404 Not Found:
   ```json
   {
     "error": "Scope not found",
     "message": "Deck or folder does not exist"
   }
   ```
4. Client displays error
5. User redirected to deck/folder list
6. Use case ends (failure)

### 6d. Card Limit Exceeded

**Trigger:** Step 7 - More than 200 due cards available

1. System queries for due cards
2. Result set has 350 cards
3. System applies limit: SELECT ... LIMIT 200
4. System includes first 200 cards in session
5. Message shown: "Showing 200 of 350 due cards"
6. Continue to Step 9 (Main Flow)

### 6e. Session Expired During Review

**Trigger:** Step 13 - Session idle for too long

1. User leaves random session idle for > 2 hours
2. System expires session
3. User tries to continue reviewing
4. System returns 404 Not Found:
   ```json
   {
     "error": "Session not found",
     "message": "Review session has expired"
   }
   ```
5. Client redirects to deck/folder view
6. Use case ends (failure)

### 6f. User Exits Random Mode

**Trigger:** Step 17 - User clicks "Exit" button

1. User clicks "Exit" button
2. System marks session as completed
3. System returns session summary:
   ```json
   {
     "message": "Random review session ended",
     "summary": {
       "totalReviewed": 85,
       "durationSeconds": 1200
     }
   }
   ```
4. Client displays summary
5. User redirected to deck/folder view
6. Use case ends (success)

### 6g. All Cards Reviewed

**Trigger:** Step 17 - Last card reviewed

1. User rates the last card in session
2. System updates SRS and checks for remaining cards
3. No more cards in queue
4. System marks session as completed
5. System returns completion message:
   ```json
   {
     "message": "Session complete! Great work!",
     "summary": {
       "totalReviewed": 120,
       "durationSeconds": 1800
     }
   }
   ```
6. Client displays completion summary
7. Use case ends (success)

## 7. Special Requirements

### 7.1 Performance

- Response time < 1 second for creating random session
- Efficient randomization for large card sets
- Batch size limit: 200 cards per session
- Index on (user_id, deck_id, due_date) for fast queries

### 7.2 Random Mode Behavior

- **Due cards only:** Only cards with due_date <= today are included
- **Randomized order:** Cards presented in random order (different from normal)
- **SRS updates:** Ratings affect SRS scheduling exactly like normal review
- **Fair randomization:** Each card has equal chance of appearing
- **Session consistency:** Same randomization seed for session (optional)

### 7.3 Differences from Normal Review

- **Order:** Random vs. due_date ASC / current_box ASC
- **Selection:** Same criteria (due cards)
- **SRS updates:** Identical behavior

### 7.4 Differences from Cram Mode

- **Card selection:** Due cards only vs. all cards
- **SRS updates:** Always enabled vs. optional toggle
- **Purpose:** Study due cards in random order vs. practice without schedule

## 8. Technology and Data Variations

### 8.1 Random Session Creation

```typescript
interface RandomSessionRequest {
  scope: {
    type: 'DECK' | 'FOLDER';
    id: string;
  };
}

const createRandomSession = async (request: RandomSessionRequest) => {
  // Query due cards only
  const query = `
    SELECT id, front, back, current_box, due_date
    FROM cards
    WHERE ${request.scope.type === 'DECK' ? 'deck_id' : 'folder_id'} = ?
      AND user_id = ?
      AND due_date <= CURRENT_DATE
      AND deleted_at IS NULL
    ORDER BY RANDOM()
    LIMIT 200
  `;

  // Execute query and create session
};
```

### 8.2 Randomization Algorithm

```sql
-- PostgreSQL RANDOM() function
SELECT * FROM cards ORDER BY RANDOM() LIMIT 200;

-- Alternative: Use seed for reproducible randomness
SELECT * FROM cards ORDER BY RANDOM(seed) LIMIT 200;

-- Or: Use row_number() with random offset
SELECT * FROM (
  SELECT *, ROW_NUMBER() OVER (ORDER BY RANDOM()) as rn
  FROM cards
) WHERE rn <= 200;
```

### 8.3 Session Type

```sql
-- review_sessions table with session_type
-- session_type = 'RANDOM' for random mode sessions
-- Same as UC-029 (Cram Mode)
```

### 8.4 Rating Behavior

```typescript
// Random mode uses same rating logic as normal review
const rateCardInRandom = async (cardId: string, rating: string) => {
  // Update SRS as normal (UC-024)
  await updateCardSRS(cardId, rating);
  await createReviewLog(cardId, rating);
  await incrementDailyCounter(userId);
};
```

## 9. Frequency of Occurrence

- **Occasional:** When users prefer variety in review order
- **Per user:** 5-10 random sessions per month
- **Total:** 50-200 random sessions/day (MVP phase)
- Preference-based: Some users prefer random over ordered

## 10. Open Issues

- **Allow mixing due and new cards:** Include new cards in random order (future)
- **Custom randomization seed:** Allow user to set seed for reproducibility (future)
- **Randomization algorithm:** More sophisticated algorithms (future)
- **Session resume:** Resume interrupted random session (future)

## 11. Related Use Cases

- [UC-023: Review Cards (SRS)](UC-023-review-cards-srs.md) - Normal review mode
- [UC-024: Rate Card](UC-024-rate-card.md) - Rating behavior (same as random)
- [UC-029: Cram Mode](UC-029-cram-mode.md) - Similar random ordering but different selection

## 12. Business Rules References

- **BR-REV-01:** Due card selection (due_date <= today)
- **BR-RAND-01:** Randomize display order only; SRS rules unchanged
- **BR-RAND-02:** Batch size limit: 200 cards per session

## 13. UI Mockup Notes

### Random Mode Banner

```
┌─────────────────────────────────────────┐
│ 🎲 RANDOM MODE                           │
│                                         │
│ Cards: 120    Progress: 45/120        │
│                                         │
│ Reviewing due cards in random order    │
└─────────────────────────────────────────┘
```

### No Due Cards Message

```
┌─────────────────────────────────────────┐
│ No Cards to Review Today                │
├─────────────────────────────────────────┤
│                                         │
│ All your cards are up to date!         │
│                                         │
│ Next review: January 30, 2025          │
│                                         │
│ [Cram Mode]  [Browse Decks]           │
└─────────────────────────────────────────┘
```

## 14. API Endpoint

```http
POST /api/review/random/sessions
```

**Request Headers:**

```http
Authorization: Bearer <access-token>
Content-Type: application/json
```

**Request Body:**

```json
{
  "scope": {
    "type": "DECK",
    "id": "deck-uuid-123"
  }
}
```

**Success Response (201 Created):**

```json
{
  "sessionId": "session-uuid-456",
  "sessionType": "RANDOM",
  "totalCards": 120,
  "card": {
    "id": "card-uuid-789",
    "front": "What is the capital of France?",
    "back": "Paris",
    "current_box": 3,
    "due_date": "2025-01-20"
  },
  "progress": {
    "current": 1,
    "total": 120
  }
}
```

**Success Response (200 OK) - No Due Cards:**

```json
{
  "message": "No cards to review today",
  "reason": "NO_DUE_CARDS",
  "nextReviewDate": "2025-01-30"
}
```

**Success Response (200 OK) - Daily Limit Reached:**

```json
{
  "message": "Daily limit reached. Come back tomorrow!",
  "summary": {
    "totalReviewed": 200,
    "limitReached": true
  }
}
```

**Error Responses:**

400 Bad Request - Invalid scope:

```json
{
  "error": "Invalid scope",
  "message": "Scope type must be DECK or FOLDER"
}
```

404 Not Found - Scope not found:

```json
{
  "error": "Scope not found",
  "message": "Deck or folder does not exist"
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
  "message": "Failed to create random session. Please try again."
}
```

## 15. Test Cases

### TC-030-001: Start Random Mode Successfully

- **Given:** User selects deck with 150 due cards
- **When:** User starts random session
- **Then:** Session created, first card returned, cards randomized

### TC-030-002: No Due Cards Available

- **Given:** All cards have due_date > today
- **When:** User tries to start random session
- **Then:** Message "No cards to review today", session not created

### TC-030-003: Ratings Update SRS

- **Given:** User in random session
- **When:** User rates card as GOOD
- **Then:** Card SRS updated, review log created, daily counter incremented

### TC-030-004: Card Limit Applied

- **Given:** Deck has 300 due cards
- **When:** User starts random session
- **Then:** Only 200 cards included, message shown

### TC-030-005: Daily Limit Reached

- **Given:** User reviewed 199 cards today
- **When:** User tries to start random session
- **Then:** "Daily limit reached" message, session not created

### TC-030-006: Random Order Different

- **Given:** User starts two random sessions with same cards
- **When:** Compare card order
- **Then:** Different ordering (randomized)

### TC-030-007: All Cards Reviewed

- **Given:** User reviews all 120 cards in session
- **When:** Last card rated
- **Then:** Session complete message shown

### TC-030-008: Scope Not Found

- **Given:** Invalid deck ID
- **When:** User tries to start random session
- **Then:** 404 error "Scope not found"

### TC-030-009: Exit Random Mode

- **Given:** User in active random session
- **When:** User clicks Exit
- **Then:** Session summary shown, redirected to deck view

### TC-030-010: Session Expired

- **Given:** User's random session expired
- **When:** User tries to continue reviewing
- **Then:** 404 error "Session expired", redirect to deck view

## 16. Database Schema Reference

### review_sessions table (random support)

```sql
-- Same as UC-029 (Cram Mode)
-- session_type = 'RANDOM' for random mode sessions
CREATE TABLE review_sessions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  session_type VARCHAR(20) NOT NULL DEFAULT 'NORMAL'
    CHECK (session_type IN ('NORMAL', 'CRAM', 'RANDOM')),
  scope_type VARCHAR(20) CHECK (scope_type IN ('DECK', 'FOLDER')),
  scope_id UUID,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  completed_at TIMESTAMP
);

CREATE INDEX idx_review_sessions_user_id ON review_sessions(user_id);
CREATE INDEX idx_review_sessions_type ON review_sessions(session_type);
CREATE INDEX idx_cards_due_date ON cards(user_id, deck_id, due_date);
```
