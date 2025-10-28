# UC-030: Random Mode

## 1. Brief Description

User reviews due cards in a randomized order (different from the default due_date/current_box ordering). SRS updates still apply when rating.

## 2. Actors

- Primary Actor: Authenticated User
- Secondary Actor: Review Service

## 3. Preconditions

- User is authenticated
- There are due cards in the chosen scope

## 4. Postconditions

### Success Postconditions

- A random-ordered review session is created
- Ratings update SRS (boxes/due_date) as in normal review

### Failure Postconditions

- No session started; message shown (no due cards/daily limit reached)

## 5. Main Success Scenario (Basic Flow)

1. User selects Random Mode for a scope
2. System queries due cards (due_date <= today)
3. System randomizes order and caps batch size (e.g., 200)
4. System creates a session and returns the first card (randomized)
5. User reveals Back and rates (AGAIN/HARD/GOOD/EASY)
6. System updates SRS and returns next randomized card

## 6. Alternative Flows

### 6a. No Due Cards

1. System shows: "No cards to review today"

### 6b. Daily Limit Reached

1. System shows: "Daily limit reached. Come back tomorrow!"

## 7. Special Requirements

- Randomization should be fair; consider seeding for repeatability (optional)
- Maintain performance with indexes and limited batch sizes

## 8. Business Rules / Constraints

- BR-REV-01: Due card selection (due_date <= today)
- BR-RAND-01: Randomize display order only; SRS rules unchanged

## 9. Frequency of Occurrence

- Occasional; preference-based study

## 10. Open Issues

- Allow mixing due and new cards (future)

## 11. Related Use Cases

- UC-023: Review Cards (SRS)
- UC-024: Rate Card

## 12. Business Rules References

- BR-REV-01, BR-RAND-01

## 13. UI Mockup Notes

- Mode banner (Random); show remaining count

## 14. API Endpoints

```
POST /api/review/random/sessions
```

Request Body:

```json
{ "scope": { "type": "FOLDER", "id": "<uuid>" } }
```

Success (201): returns sessionId and first randomized card

Errors:

- 200 with message when no due cards
- 404 scope not found

## 15. Test Cases

- TC-030-001: Start random mode with due cards -> randomized first card
- TC-030-002: No due cards -> message
- TC-030-003: Ratings update SRS as normal
