# UC-023: Review Cards (SRS)

## 1. Brief Description

Authenticated user starts a review session to study due cards according to the SRS Box System with daily limits and ordering rules. Cards are presented one at a time for review.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** Review Service, SRS System

## 3. Preconditions

- User is authenticated with valid access token
- There are due cards in the chosen scope (deck or folder)
- User has not reached daily review limit
- User has internet connection

## 4. Postconditions

### Success Postconditions

- A review session is created or resumed
- A queue of due cards is presented to the user
- First card displayed with Front side visible
- Session metadata tracked (sessionId, remaining cards count)
- User can proceed to rate cards (see UC-024)

### Failure Postconditions

- No session started
- Error message displayed (no due cards/daily limit reached)
- User remains on deck/folder view

## 5. Main Success Scenario (Basic Flow)

1. User opens Review for a scope (Deck or Folder)
2. System checks daily limits from user SRS settings (max_reviews_per_day)
3. System queries due cards:
   - due_date <= today
   - deleted_at IS NULL
   - Belongs to selected deck/folder
4. System orders cards by:
   - due_date ASC (earliest first)
   - current_box ASC (lower boxes first)
   - Or user's review_order setting (RANDOM if configured)
5. System limits batch size (e.g., up to 200 per request)
6. System creates a session with initial queue metadata:
   - sessionId: UUID
   - totalCards: count of due cards
   - remainingCards: count
   - currentIndex: 0
7. System returns first card for display (Front only) and sessionId
8. UI shows card Front side
9. User clicks "Show Answer" button
10. UI reveals Back side of card
11. User proceeds to rate the card (see UC-024 Rate Card)

## 6. Alternative Flows

### 6a. No Due Cards

**Trigger:** Step 3 - No due cards found

1. System queries due cards
2. Zero due cards found
3. System returns 200 OK with message: "No cards to review today"
4. UI displays message: "No cards to review today. Great job!"
5. Use case ends (no session created)

### 6b. Daily Limit Reached

**Trigger:** Step 2 - User reached daily review limit

1. System checks user's daily review count
2. count >= max_reviews_per_day
3. System returns 200 OK with message: "Daily limit reached"
4. UI displays message: "Daily limit reached. Come back tomorrow!"
5. Use case ends (no session created)

### 6c. Prefetch/Batching

**Trigger:** Step 5 - Queue depletes during session

1. System prefetches next N cards (e.g., 50) for smooth UX
2. When queue depletes, system fetches next batch automatically
3. System continues fetching until all due cards reviewed or session ends
4. Continue to Step 7 (Main Flow)

### 6d. Deck Not Found or Forbidden

**Trigger:** Step 3 - Deck validation fails

1. System validates deck ownership
2. Deck not found or belongs to another user
3. System returns 404/403 error
4. Client displays error message
5. Use case ends (failure)

### 6e. Session Expired

**Trigger:** Step 7 - Access token expired

1. Token expired during session creation
2. Backend returns 401 Unauthorized
3. Client auto-refreshes token (UC-003)
4. Client retries request with new token
5. Continue to Step 7 (Main Flow)

### 6f. Network Error

**Trigger:** Step 7 - Network request fails

1. Network error occurs
2. Client catches network error
3. UI displays error: "Network error. Please check your connection."
4. User can retry
5. Use case ends (failure)

## 7. Special Requirements

### 7.1 Performance

- Indexed query (user_id, due_date, current_box)
- LIMIT + pagination for large result sets
- Prefetch next batch to avoid latency when navigating cards
- Response time < 500ms for session creation

### 7.2 Session Management

- Session timeout/inactivity handling (optional in MVP)
- Session state tracked client-side and server-side
- Resume interrupted session (future feature)

### 7.3 Usability

- Clear indication of remaining cards count
- Progress indicator (e.g., "Card 5 of 20")
- Smooth card transitions
- Keyboard shortcuts: Space to show answer, Arrow keys to rate (future)

## 8. Technology and Data Variations

### 8.1 Review Order

- **Due Date First:** due_date ASC, current_box ASC (default)
- **Random:** Randomized order (if user setting enabled)
- Future: Custom ordering algorithms

### 8.2 Session Creation

- Session can be created per deck or per folder
- Session metadata stored temporarily (client-side or server-side)
- Session expires after inactivity (e.g., 30 minutes)

### 8.3 Card Query

```sql
SELECT * FROM cards
WHERE deck_id = ? 
  AND due_date <= CURRENT_DATE
  AND deleted_at IS NULL
ORDER BY due_date ASC, current_box ASC
LIMIT 200
```

## 9. Frequency of Occurrence

- Daily; main user activity
- Expected: 1-5 review sessions per user per day
- Peak: 10+ review sessions per day for active users

## 10. Open Issues

- Scope by folder recursively vs. single deck (MVP can support both via parameter)
- Custom review filters (by box, by tag) - future
- Review session history/statistics - future
- Adaptive difficulty based on performance - future

## 11. Related Use Cases

- [UC-024: Rate Card](UC-024-rate-card.md) - Rate card after review
- [UC-025: Undo Review](UC-025-undo-review.md) - Undo last rating
- [UC-026: Skip Card](UC-026-skip-card.md) - Skip card during review
- [UC-027: Edit Card During Review](UC-027-edit-card-during-review.md) - Quick edit
- [UC-028: Configure SRS Settings](UC-028-configure-srs-settings.md) - Configure review limits

## 12. Business Rules References

- **BR-REV-01:** Due card = due_date <= today AND not soft-deleted
- **BR-REV-02:** Respect daily limits from SRS settings
- **BR-REV-03:** Order by due_date ASC, current_box ASC (configurable)
- **BR-REV-04:** Batch size capped (e.g., 200)

## 13. UI Mockup Notes

- Review interface with card display area
- Front view with "Show Answer" button
- Back view with rating buttons (Again, Hard, Good, Easy)
- Progress indicator showing current position
- Remaining cards count
- Session statistics (optional)

## 14. API Endpoints

### Start Review Session

```http
POST /api/review/sessions
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

Or for folder:

```json
{
  "scope": {
    "type": "FOLDER",
    "id": "folder-uuid-456"
  }
}
```

**Success Response (201 Created):**

```json
{
  "sessionId": "session-uuid-789",
  "remaining": 120,
  "totalCards": 120,
  "card": {
    "id": "card-uuid-abc",
    "front": "What is a closure in JavaScript?",
    "currentBox": 1,
    "dueDate": "2025-01-31"
  }
}
```

**Success Response - No Due Cards (200 OK):**

```json
{
  "message": "No cards to review today",
  "remaining": 0
}
```

**Success Response - Daily Limit Reached (200 OK):**

```json
{
  "message": "Daily limit reached. Come back tomorrow!",
  "dailyLimit": 200,
  "reviewedToday": 200
}
```

**Error Responses:**

404 Not Found - Deck not found:

```json
{
  "error": "Deck not found",
  "message": "The specified deck does not exist"
}
```

403 Forbidden:

```json
{
  "error": "Access denied",
  "message": "You do not have permission to review cards from this deck"
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
  "message": "Failed to start review session. Please try again later."
}
```

### Fetch Next Card

```http
GET /api/review/sessions/{sessionId}/next
```

**Success Response (200 OK):**

```json
{
  "card": {
    "id": "card-uuid-def",
    "front": "What is a Promise in JavaScript?",
    "currentBox": 2,
    "dueDate": "2025-01-31"
  },
  "remaining": 119,
  "currentIndex": 1
}
```

**Error Responses:**

404 Not Found - Session expired:

```json
{
  "error": "Session not found",
  "message": "Review session expired or does not exist"
}
```

## 15. Test Cases

### TC-023-001: Start Session with Due Cards Successfully

- **Given:** User has deck with 50 due cards, daily limit not reached
- **When:** User starts review session
- **Then:** Session created, first card returned, remaining = 50

### TC-023-002: No Due Cards

- **Given:** User has deck with no due cards
- **When:** User starts review session
- **Then:** 200 OK with message "No cards to review today"

### TC-023-003: Daily Limit Reached

- **Given:** User has reviewed 200 cards today (limit = 200)
- **When:** User tries to start review session
- **Then:** 200 OK with message "Daily limit reached"

### TC-023-004: Pagination Fetches Next Batch

- **Given:** User has 500 due cards
- **When:** User reviews first 200 cards, queue depletes
- **Then:** Next batch of 200 cards fetched automatically

### TC-023-005: Deck Not Found

- **Given:** User has invalid deckId
- **When:** User tries to start review session
- **Then:** 404 error "Deck not found"

### TC-023-006: Folder Scope

- **Given:** User has folder with multiple decks containing due cards
- **When:** User starts review session with folder scope
- **Then:** Cards from all decks in folder included in session

### TC-023-007: Order by Due Date

- **Given:** User has cards with different due dates
- **When:** User starts review session
- **Then:** Cards ordered by due_date ASC (earliest first)

### TC-023-008: Random Order

- **Given:** User has review_order = RANDOM in SRS settings
- **When:** User starts review session
- **Then:** Cards presented in random order

### TC-023-009: Session Expired During Review

- **Given:** User's access token expires
- **When:** User tries to fetch next card
- **Then:** Token auto-refreshed, card fetched successfully

### TC-023-010: Soft-Deleted Cards Excluded

- **Given:** User has deck with due cards, some soft-deleted
- **When:** User starts review session
- **Then:** Only non-deleted cards included in session

### TC-023-011: Batch Size Limit

- **Given:** User has 500 due cards
- **When:** User starts review session
- **Then:** First batch contains 200 cards, subsequent batches fetched as needed

### TC-023-012: Progress Tracking

- **Given:** User reviews cards in session
- **When:** User views progress indicator
- **Then:** Shows "Card 5 of 20" or similar

### TC-023-013: Session Metadata Persisted

- **Given:** User starts review session
- **When:** User refreshes page
- **Then:** Session can be resumed (if session persistence implemented)
