# UC-029: Cram Mode

## 1. Brief Description

User practices cards in a fast "cram" session ignoring due dates. Cards are selected from a scope (deck or folder) regardless of due_date, shuffled for variety, and presented for review. By default, cram does not update SRS scheduling (no changes to boxes or due dates), but this can be optionally enabled.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** Review Service, SRS Engine

## 3. Preconditions

- User is authenticated with valid access token
- Scope selected (Deck or Folder)
- Cards exist in the selected scope
- User has not exceeded daily review limit (if applyToSrs is enabled)

## 4. Postconditions

### Success Postconditions

- A cram session is created with a queue of cards from the scope
- Cards are shuffled for variety
- By default, ratings do not affect SRS fields (configurable option)
- If applyToSrs is enabled, ratings update SRS as in normal review
- User can review cards without affecting normal schedule

### Failure Postconditions

- No session created
- Error message displayed
- User remains on deck/folder view

## 5. Main Success Scenario (Basic Flow)

1. User navigates to a deck or folder
2. User clicks "Cram Mode" button
3. System displays cram mode options:
   - Scope selection (already selected: current deck/folder)
   - Filter options (optional): Box range, Include/Exclude learned cards
   - Toggle: "Apply ratings to SRS" (default: OFF)
4. User reviews options and clicks "Start Cram Session"
5. Client sends POST request to create cram session:
   ```json
   {
     "scope": {
       "type": "DECK",
       "id": "deck-uuid-123"
     },
     "applyToSrs": false,
     "filters": {
       "boxRange": null,
       "includeLearned": true
     }
   }
   ```
6. Backend validates request:
   - User is authenticated
   - Scope exists and belongs to user
   - Scope has cards available
7. System queries cards from scope (ignoring due_date):
   ```sql
   SELECT id, front, back, current_box, due_date
   FROM cards
   WHERE deck_id = ? 
     AND user_id = ?
     AND deleted_at IS NULL
   ORDER BY RANDOM()
   LIMIT 500
   ```
8. System applies optional filters:
   - If boxRange specified: Filter by current_box within range
   - If includeLearned = false: Exclude cards in high boxes (e.g., Box 5+)
9. System shuffles card order using random seed
10. System creates cram session record:
    ```sql
    INSERT INTO review_sessions (id, user_id, session_type, scope_type, scope_id, 
                                 apply_to_srs, created_at)
    VALUES (?, ?, 'CRAM', 'DECK', ?, false, CURRENT_TIMESTAMP)
    ```
11. System returns 201 Created with session info and first card:
    ```json
    {
      "sessionId": "session-uuid-456",
      "sessionType": "CRAM",
      "totalCards": 250,
      "applyToSrs": false,
      "card": {
        "id": "card-uuid-789",
        "front": "What is the capital of France?",
        "back": "Paris",
        "current_box": 3
      },
      "progress": {
        "current": 1,
        "total": 250
      }
    }
    ```
12. Client receives response
13. Client displays cram session interface:
    - Shows card (Front view)
    - Shows "Cram Mode" banner
    - Shows "Apply to SRS" toggle (if disabled, shows indicator)
    - Shows progress indicator
14. User reveals Back side and reviews answer
15. User optionally rates card (AGAIN/HARD/GOOD/EASY) or marks known/unknown
16. If applyToSrs is false:
    - Rating does not update SRS state
    - No review log entry created (or marked as cram-only)
17. If applyToSrs is true:
    - Rating updates SRS state as normal (UC-024)
    - Review log entry created
18. System advances to next card in shuffled queue
19. User continues reviewing until:
    - All cards reviewed
    - User exits cram mode
    - Daily limit reached (if applyToSrs enabled)

## 6. Alternative Flows

### 6a. No Cards Available

**Trigger:** Step 7 - No cards found in scope

1. System queries for cards
2. No cards found (scope is empty or all filtered out)
3. System returns 200 OK with message:
   ```json
   {
     "message": "No cards available for cram session",
     "reason": "EMPTY_SCOPE"
   }
   ```
4. Client displays message: "No cards available in this scope"
5. User must select different scope or add cards
6. Use case ends (failure)

### 6b. Scope Not Found

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

### 6c. Filter by Box Range

**Trigger:** Step 4 - User selects box range filter

1. User selects filter: "Only Box 1-3"
2. System applies filter to query:
   ```sql
   WHERE current_box BETWEEN 1 AND 3
   ```
3. Only cards in specified boxes included in session
4. Continue to Step 7 (Main Flow)

### 6d. Exclude Already Learned Cards

**Trigger:** Step 4 - User unchecks "Include Learned Cards"

1. User sets includeLearned = false
2. System filters out cards in high boxes (e.g., Box 5+):
   ```sql
   WHERE current_box < 5
   ```
3. Only cards in lower boxes included
4. Continue to Step 7 (Main Flow)

### 6e. Apply Ratings to SRS Enabled

**Trigger:** Step 4 - User enables "Apply ratings to SRS"

1. User toggles applyToSrs to true
2. System checks daily review limit
3. If limit not exceeded, continue with applyToSrs = true
4. When user rates cards:
   - SRS state updated as normal
   - Review log entries created
   - Daily counter incremented
5. Continue to Step 16 (Main Flow)

### 6f. Daily Limit Reached (with applyToSrs)

**Trigger:** Step 19 - User exceeds daily limit while cramming with applyToSrs enabled

1. User reviews cards and rates them
2. Daily review counter incremented
3. Counter exceeds max_reviews_per_day
4. System returns message:
   ```json
   {
     "message": "Daily limit reached. Come back tomorrow!",
     "summary": {
       "totalReviewed": 200,
       "limitReached": true
     }
   }
   ```
5. Session ends automatically
6. Use case ends (success)

### 6g. Card Limit Exceeded

**Trigger:** Step 7 - More than 500 cards available

1. System queries for cards
2. Result set has 800 cards
3. System applies limit: SELECT ... LIMIT 500
4. System includes first 500 cards in session
5. Message shown: "Showing 500 of 800 cards"
6. Continue to Step 10 (Main Flow)

### 6h. User Exits Cram Mode

**Trigger:** Step 19 - User clicks "Exit" button

1. User clicks "Exit Cram Mode" button
2. System marks session as completed
3. System returns session summary:
   ```json
   {
     "message": "Cram session ended",
     "summary": {
       "totalReviewed": 45,
       "durationSeconds": 600
     }
   }
   ```
4. Client displays summary
5. User redirected to deck/folder view
6. Use case ends (success)

### 6i. Session Expired During Cram

**Trigger:** Step 14 - Session idle for too long

1. User leaves cram session idle for > 2 hours
2. System expires session
3. User tries to continue reviewing
4. System returns 404 Not Found:
   ```json
   {
     "error": "Session not found",
     "message": "Cram session has expired"
   }
   ```
5. Client redirects to deck/folder view
6. Use case ends (failure)

## 7. Special Requirements

### 7.1 Performance

- Response time < 2 seconds for creating cram session
- Efficient shuffling algorithm for large card sets
- Pagination if card set exceeds 500 (optional)
- Random seed for consistent shuffling (optional)

### 7.2 Cram Mode Behavior

- **Ignore due_date:** All cards eligible regardless of due date
- **Shuffled order:** Cards presented in random order for variety
- **No SRS impact:** By default, ratings don't affect scheduling
- **Flexible scope:** Can cram entire folder (recursive) or single deck
- **Card limit:** Cap at 500 cards per session to prevent overwhelming

### 7.3 Apply to SRS Toggle

- **Default: OFF** - Ratings don't affect SRS
- **When ON:** Ratings work exactly like normal review (UC-024)
- **Clear indicator:** UI clearly shows whether SRS updates are enabled
- **Warning:** Show warning when enabling: "Ratings will affect your SRS schedule"

### 7.4 Filter Options

- **Box range:** Filter cards by current_box (e.g., only Box 1-3)
- **Include learned:** Toggle to include/exclude high-box cards
- **Scope selection:** Choose deck or folder

## 8. Technology and Data Variations

### 8.1 Cram Session Creation

```typescript
interface CramSessionRequest {
  scope: {
    type: 'DECK' | 'FOLDER';
    id: string;
  };
  applyToSrs: boolean;
  filters?: {
    boxRange?: { min: number; max: number };
    includeLearned: boolean;
  };
}

const createCramSession = async (request: CramSessionRequest) => {
  // Query cards ignoring due_date
  let query = `
    SELECT id, front, back, current_box, due_date
    FROM cards
    WHERE ${request.scope.type === 'DECK' ? 'deck_id' : 'folder_id'} = ?
      AND user_id = ?
      AND deleted_at IS NULL
  `;

  // Apply filters
  if (request.filters?.boxRange) {
    query += ` AND current_box BETWEEN ${request.filters.boxRange.min} AND ${request.filters.boxRange.max}`;
  }

  if (!request.filters?.includeLearned) {
    query += ` AND current_box < 5`; // Exclude high boxes
  }

  // Shuffle and limit
  query += ` ORDER BY RANDOM() LIMIT 500`;

  // Execute query and create session
};
```

### 8.2 Random Shuffling

```sql
-- PostgreSQL random ordering
SELECT * FROM cards ORDER BY RANDOM() LIMIT 500;

-- Or use seed for reproducible shuffling
SELECT * FROM cards ORDER BY RANDOM(seed) LIMIT 500;
```

### 8.3 Session Type

```sql
-- Add session_type to review_sessions table
ALTER TABLE review_sessions 
ADD COLUMN session_type VARCHAR(20) DEFAULT 'NORMAL'
  CHECK (session_type IN ('NORMAL', 'CRAM', 'RANDOM'));

ALTER TABLE review_sessions 
ADD COLUMN apply_to_srs BOOLEAN DEFAULT true;
```

### 8.4 Rating Without SRS Update

```typescript
const rateCardInCram = async (
  cardId: string,
  rating: string,
  applyToSrs: boolean
) => {
  if (applyToSrs) {
    // Update SRS as normal
    await updateCardSRS(cardId, rating);
    await createReviewLog(cardId, rating);
  } else {
    // Only log for analytics (optional)
    await createCramLog(cardId, rating);
    // No SRS update
  }
};
```

## 9. Frequency of Occurrence

- **Occasional:** Before exams or quick revision sessions
- **Per user:** 2-5 cram sessions per month
- **Total:** 20-100 cram sessions/day (MVP phase)
- Peak: During exam periods

## 10. Open Issues

- **Persist cram session state:** Save progress across reloads (future)
- **Custom card limits:** Allow user to set custom limit (future)
- **Cram statistics:** Track cram session performance (future)
- **Resume cram session:** Resume interrupted cram session (future)
- **Cram mode presets:** Save favorite filter combinations (future)

## 11. Related Use Cases

- [UC-023: Review Cards (SRS)](UC-023-review-cards-srs.md) - Normal review mode
- [UC-024: Rate Card](UC-024-rate-card.md) - Rating behavior when applyToSrs enabled
- [UC-030: Random Mode](UC-030-random-mode.md) - Similar random ordering

## 12. Business Rules References

- **BR-CRAM-01:** Ignore due_date when selecting cards
- **BR-CRAM-02:** Default does not update SRS; optional toggle exists
- **BR-CRAM-03:** Card limit per session: 500 cards

## 13. UI Mockup Notes

### Cram Mode Banner

```
┌─────────────────────────────────────────┐
│ 🔥 CRAM MODE                             │
│                                         │
│ Cards: 250    Progress: 45/250         │
│                                         │
│ [✓] Apply ratings to SRS               │
│                                         │
│ ⚠️ Ratings will not affect your         │
│    normal schedule                      │
└─────────────────────────────────────────┘
```

### Start Cram Dialog

```
┌─────────────────────────────────────────┐
│ Start Cram Session                      │
├─────────────────────────────────────────┤
│                                         │
│ Scope: IELTS Academic Words            │
│                                         │
│ Filters:                                │
│ [ ] Only Box 1-3                        │
│ [✓] Include learned cards               │
│                                         │
│ [ ] Apply ratings to SRS                │
│                                         │
│ [Cancel]  [Start Cram Session]         │
└─────────────────────────────────────────┘
```

## 14. API Endpoint

```http
POST /api/review/cram/sessions
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
  },
  "applyToSrs": false,
  "filters": {
    "boxRange": {
      "min": 1,
      "max": 3
    },
    "includeLearned": true
  }
}
```

**Success Response (201 Created):**

```json
{
  "sessionId": "session-uuid-456",
  "sessionType": "CRAM",
  "totalCards": 250,
  "applyToSrs": false,
  "card": {
    "id": "card-uuid-789",
    "front": "What is the capital of France?",
    "back": "Paris",
    "current_box": 3
  },
  "progress": {
    "current": 1,
    "total": 250
  }
}
```

**Error Responses:**

200 OK - No cards available:

```json
{
  "message": "No cards available for cram session",
  "reason": "EMPTY_SCOPE"
}
```

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
  "message": "Failed to create cram session. Please try again."
}
```

## 15. Test Cases

### TC-029-001: Start Cram Session Successfully

- **Given:** User selects deck with 200 cards
- **When:** User starts cram session
- **Then:** Session created, first card returned, cards shuffled

### TC-029-002: Cram Without SRS Update

- **Given:** applyToSrs = false
- **When:** User rates card as GOOD
- **Then:** Card SRS state unchanged, no review log created

### TC-029-003: Cram With SRS Update

- **Given:** applyToSrs = true
- **When:** User rates card as GOOD
- **Then:** Card SRS updated, review log created, daily counter incremented

### TC-029-004: Filter by Box Range

- **Given:** User selects "Only Box 1-3"
- **When:** User starts cram session
- **Then:** Only cards in Box 1-3 included in session

### TC-029-005: Exclude Learned Cards

- **Given:** User unchecks "Include learned cards"
- **When:** User starts cram session
- **Then:** Only cards in Box 1-4 included (high boxes excluded)

### TC-029-006: No Cards Available

- **Given:** Empty deck or all cards filtered out
- **When:** User tries to start cram session
- **Then:** Message "No cards available", session not created

### TC-029-007: Card Limit Applied

- **Given:** Deck has 800 cards
- **When:** User starts cram session
- **Then:** Only 500 cards included, message shown

### TC-029-008: Daily Limit Reached (with applyToSrs)

- **Given:** applyToSrs = true, user reviewed 199 cards today
- **When:** User rates one more card in cram mode
- **Then:** Daily limit reached message, session ends

### TC-029-009: Scope Not Found

- **Given:** Invalid deck ID
- **When:** User tries to start cram session
- **Then:** 404 error "Scope not found"

### TC-029-010: Exit Cram Mode

- **Given:** User in active cram session
- **When:** User clicks Exit
- **Then:** Session summary shown, redirected to deck view

## 16. Database Schema Reference

### review_sessions table (cram support)

```sql
CREATE TABLE review_sessions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  session_type VARCHAR(20) NOT NULL DEFAULT 'NORMAL'
    CHECK (session_type IN ('NORMAL', 'CRAM', 'RANDOM')),
  scope_type VARCHAR(20) CHECK (scope_type IN ('DECK', 'FOLDER')),
  scope_id UUID,
  apply_to_srs BOOLEAN DEFAULT true,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  completed_at TIMESTAMP
);

CREATE INDEX idx_review_sessions_user_id ON review_sessions(user_id);
CREATE INDEX idx_review_sessions_type ON review_sessions(session_type);
```
