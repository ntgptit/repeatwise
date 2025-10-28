# UC-026: Skip Card

## 1. Brief Description

User skips the current card during a review session without changing its SRS state. The card is postponed or temporarily removed from the queue according to policy.

## 2. Actors

- Primary Actor: Authenticated User
- Secondary Actor: Review Service

## 3. Preconditions

- User is authenticated
- An active review session exists
- A current card is available to display

## 4. Postconditions

### Success Postconditions

- Card SRS state unchanged
- Session queue updated (card moved to the tail or temporarily hidden)

### Failure Postconditions

- No change to queue
- Error displayed

## 5. Main Success Scenario (Basic Flow)

1. User clicks “Skip” while viewing a card
2. System applies skip policy:
   - Default MVP: move card to the end of the current session queue
3. System returns next card
4. UI displays the next card

## 6. Alternative Flows

### 6a. Too Many Skips (Optional Limit)

1. User exceeded configurable skip limit (e.g., 3 per session)
2. System returns 400 with message: "Skip limit reached"

### 6b. No Next Card Available

1. Queue empty after skipping
2. System shows message: "No more cards in this session"

## 7. Special Requirements

- Skip should not update due_date or box
- Optional setting for per-session skip limit

## 8. Business Rules / Constraints

- BR-REV-07: Skip does not change SRS state
- BR-REV-08: Skip limit per session (optional)

## 9. Frequency of Occurrence

- Occasional; useful for temporarily deferring hard cards

## 10. Open Issues

- Add “snooze until tomorrow” as future enhancement

## 11. Related Use Cases

- UC-023: Review Cards (SRS)
- UC-024: Rate Card
- UC-025: Undo Review

## 12. Business Rules References

- BR-REV-07..08

## 13. UI Mockup Notes

- "Skip" button next to rating controls; greyed out when limit reached

## 14. API Endpoint

```
POST /api/review/sessions/{sessionId}/skip
```

Success (200): returns next card

Errors:

- 400 skip limit reached
- 404 session not found

## 15. Test Cases

- TC-026-001: Skip card -> next card shown, SRS unchanged
- TC-026-002: Skip limit reached -> 400
- TC-026-003: Skip on last card -> session ends message
