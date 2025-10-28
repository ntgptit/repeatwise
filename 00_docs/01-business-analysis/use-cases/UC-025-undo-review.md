# UC-025: Undo Review

## 1. Brief Description

User undoes the most recent rating applied in the current review session, restoring the card’s SRS state and the session queue.

## 2. Actors

- Primary Actor: Authenticated User
- Secondary Actor: Review Service

## 3. Preconditions

- User is authenticated
- An active review session exists
- There is at least one recent rating event in this session that is eligible for undo

## 4. Postconditions

### Success Postconditions

- The last rating is reverted
- Card’s SRS fields (current_box, due_date, etc.) are restored to the previous values
- Review log adjusted accordingly (last entry removed/marked undone)
- Session queue returns to the state before the rating

### Failure Postconditions

- No changes applied
- Error displayed (e.g., nothing to undo)

## 5. Main Success Scenario (Basic Flow)

1. User clicks “Undo” after rating a card
2. System verifies that the last action is undoable (within allowed window, belongs to this session)
3. System loads the prior snapshot for the card’s SRS state
4. System rolls back SRS fields and removes/marks the last review log entry
5. System re-inserts the card at the appropriate position in the session queue (typically as current)
6. System returns 200 OK with the restored card
7. UI shows the restored card (Front view)

## 6. Alternative Flows

### 6a. No Action to Undo

Trigger: Step 2

1. No eligible last action found (e.g., user left the session or time window expired)
2. System returns 400 with message: "Nothing to undo"

### 6b. Concurrency/Conflict

Trigger: Step 4

1. Card was edited or rated in a parallel tab
2. System returns 409 Conflict; UI suggests refreshing the session

## 7. Special Requirements

- Store minimal, consistent snapshots needed to restore (idempotent undo)
- Optional time window for undo (e.g., 2 minutes)

## 8. Business Rules / Constraints

- BR-REV-05: Only the most recent rating in the current session can be undone
- BR-REV-06: Undo window may be time-limited (configurable)

## 9. Frequency of Occurrence

- Occasional; mainly for correcting mistakes

## 10. Open Issues

- Multi-step undo (history) is out of MVP scope

## 11. Related Use Cases

- UC-023: Review Cards (SRS)
- UC-024: Rate Card
- UC-026: Skip Card

## 12. Business Rules References

- BR-REV-05..06

## 13. UI Mockup Notes

- Show an “Undo” button and a short-lived toast with countdown (e.g., “Undo in 10s”)

## 14. API Endpoint

```
POST /api/review/sessions/{sessionId}/undo
```

Success (200): returns restored card as current

Errors:

- 400 nothing to undo / window expired
- 404 session not found
- 409 conflict (concurrent modification)

## 15. Test Cases

- TC-025-001: Undo immediately -> success, card restored
- TC-025-002: Undo after window expired -> 400
- TC-025-003: Undo when session restarted -> 400
- TC-025-004: Concurrent edit -> 409
