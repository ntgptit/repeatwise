# UC-023: Review Cards (SRS)

## 1. Brief Description

User starts a review session to study due cards according to the SRS Box System with daily limits and ordering rules.

## 2. Actors

- Primary Actor: Authenticated User
- Secondary Actor: Review Service

## 3. Preconditions

- User is authenticated
- There are due cards in the chosen scope (deck or folder)

## 4. Postconditions

### Success Postconditions

- A review session is created or resumed
- A queue of due cards is presented to the user

### Failure Postconditions

- No session started; user sees message (no due cards/daily limit reached)

## 5. Main Success Scenario (Basic Flow)

1. User opens Review for a scope (Deck or Folder)
2. System checks daily limits from user SRS settings (e.g., max_reviews_per_day)
3. System queries due cards: due_date <= today AND not deleted
4. System orders by due_date ASC, current_box ASC (or user setting)
5. System limits batch size (e.g., up to 200 per request)
6. System creates a session with initial queue metadata
7. System returns first card for display (Front) and sessionId
8. UI shows card Front; upon user action, reveals Back
9. User proceeds to rate the card (see UC-024)

## 6. Alternative Flows

### 6a. No Due Cards

1. System finds zero due cards
2. UI shows: "No cards to review today"

### 6b. Daily Limit Reached

1. System detects user reached max_reviews_per_day
2. UI shows: "Daily limit reached. Come back tomorrow!"

### 6c. Prefetch/Batching

1. System prefetches next N cards for smooth UX
2. When queue depletes, system fetches next batch until session ends

## 7. Special Requirements

- Performance: indexed query (user_id, due_date, current_box); LIMIT + pagination
- Prefetch next batch to avoid latency when navigating cards
- Session timeout/inactivity handling (optional in MVP)

## 8. Business Rules / Constraints

- BR-REV-01: Due card = due_date <= today AND not soft-deleted
- BR-REV-02: Respect daily limits from SRS settings
- BR-REV-03: Order by due_date ASC, current_box ASC (configurable)
- BR-REV-04: Batch size capped (e.g., 200)

## 9. Frequency of Occurrence

- Daily; main user activity

## 10. Open Issues

- Scope by folder recursively vs. single deck (MVP can support both via parameter)

## 11. Related Use Cases

- UC-024: Rate Card
- UC-021: Import Cards
- UC-022: Export Cards

## 12. Business Rules References

- BR-REV-01..04

## 13. UI Mockup Notes

- Front view with “Show Answer”; back view with rating buttons

## 14. API Endpoints

```
POST /api/review/sessions
```

Request Body:

```json
{ "scope": { "type": "DECK", "id": "<uuid>" } }
```

Success (201):

```json
{
  "sessionId": "<uuid>",
  "remaining": 120,
  "card": { "id": "<uuid>", "front": "..." }
}
```

Fetch next card (if needed):

```
GET /api/review/sessions/{sessionId}/next
```

```json
{ "card": { "id": "<uuid>", "front": "..." }, "remaining": 119 }
```

Errors:

- 200 with message if no due cards or daily limit reached
- 404 session not found (expired)

## 15. Test Cases

- TC-023-001: Start session with due cards -> returns first card
- TC-023-002: No due cards -> message shown
- TC-023-003: Daily limit reached -> message shown
- TC-023-004: Pagination fetches next batch as queue depletes
