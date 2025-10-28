# UC-024: Rate Card

## 1. Brief Description

User rates the currently shown card during a review session (e.g., AGAIN/HARD/GOOD/EASY). The system updates SRS fields and schedules the next review.

## 2. Actors

- Primary Actor: Authenticated User
- Secondary Actor: Review Service

## 3. Preconditions

- User is authenticated
- An active review session exists with a current card

## 4. Postconditions

### Success Postconditions

- Card’s SRS state updated (current_box, due_date, review history)
- Session queue advances to next card

### Failure Postconditions

- Card unchanged; session may remain on current card
- Error displayed

## 5. Main Success Scenario (Basic Flow)

1. User reveals the card Back
2. User selects a rating: AGAIN/HARD/GOOD/EASY
3. System applies SRS rule:
   - AGAIN: move to box 1 (forgotten_card_action = MOVE_TO_BOX_1)
   - HARD: optionally keep same box or decrement based on policy
   - GOOD: increment box by 1 (up to max boxes)
   - EASY: increment by >1 box (e.g., +2) within bounds
4. System computes next due_date based on resulting box and scheduling policy
5. System appends review log entry (card_id, rating, timestamp, time_taken_ms)
6. System returns next card (or session end if none)

## 6. Alternative Flows

### 6a. Session Completed

1. No remaining cards in queue
2. System returns 200 with message: "Session complete"

### 6b. Daily Limit Reached After Rating

1. Updating counters exceeds max_reviews_per_day
2. System returns message: "Daily limit reached. Come back tomorrow!"

### 6c. Concurrency/Double Submit

1. Same card rated twice
2. System deduplicates or returns 409 Conflict

## 7. Special Requirements

- Use server-side time for due_date computation
- Configurable total_boxes (default 7) and increments per rating

## 8. Business Rules / Constraints

- BR-SRS-01: total_boxes = 7 (default)
- BR-SRS-02: forgotten_card_action = MOVE_TO_BOX_1
- BR-SRS-03: due_date grows by box level (longer intervals for higher boxes)
- BR-REV-02: respect daily limit when counting reviews

## 9. Frequency of Occurrence

- High during review session

## 10. Open Issues

- Exact intervals per box configurable in SRS settings; MVP uses simple policy

## 11. Related Use Cases

- UC-023: Review Cards (SRS)
- UC-025: Undo Review (future)
- UC-026: Skip Card (future)

## 12. Business Rules References

- BR-SRS-01..03, BR-REV-02

## 13. UI Mockup Notes

- After showing Back, show four rating buttons with keyboard shortcuts (e.g., 1–4)

## 14. API Endpoint

```
POST /api/review/sessions/{sessionId}/rate
```

Request Body:

```json
{
  "cardId": "<uuid>",
  "rating": "AGAIN|HARD|GOOD|EASY",
  "timeTakenMs": 2300
}
```

Success (200): next card or completion message

Errors:

- 404 session/card not found
- 409 duplicate rating
- 400 invalid rating

## 15. Test Cases

- TC-024-001: Rate GOOD -> box +1, due_date updated
- TC-024-002: Rate AGAIN -> move to box 1
- TC-024-003: Rate EASY near max box -> capped at max
- TC-024-004: Double submit -> 409
