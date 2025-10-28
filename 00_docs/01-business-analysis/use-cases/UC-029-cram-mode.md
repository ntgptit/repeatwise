# UC-029: Cram Mode

## 1. Brief Description

User practices cards in a fast “cram” session ignoring due dates. By default, cram does not update SRS scheduling (no changes to boxes or due dates).

## 2. Actors

- Primary Actor: Authenticated User
- Secondary Actor: Review Service

## 3. Preconditions

- User is authenticated
- Scope selected (Deck or Folder)

## 4. Postconditions

### Success Postconditions

- A cram session is created with a queue of cards from the scope
- By default, ratings do not affect SRS fields (configurable option)

### Failure Postconditions

- No session created; error shown

## 5. Main Success Scenario (Basic Flow)

1. User chooses Cram Mode for a scope
2. System loads cards from the scope (ignoring due_date); optional cap (e.g., 500)
3. System shuffles order for variety
4. System creates a cram session and returns the first card
5. User reveals Back and optionally rates or marks known/unknown
6. System advances to next card; repeats until user exits or queue ends

## 6. Alternative Flows

### 6a. Include/Exclude Already Learned

1. User toggles filters (e.g., only box 1–3)
2. System rebuilds queue accordingly

### 6b. Affect SRS (Optional)

1. User enables “Apply ratings to SRS” (off by default in MVP)
2. Ratings then update boxes/due_date similar to normal review

## 7. Special Requirements

- Efficient loading and shuffling for large sets; pagination if needed
- Clear indicator that cram is separate from normal SRS scheduling

## 8. Business Rules / Constraints

- BR-CRAM-01: Ignore due_date when selecting cards
- BR-CRAM-02: Default does not update SRS; optional toggle exists

## 9. Frequency of Occurrence

- Occasional, typically before exams or quick revision

## 10. Open Issues

- Persist cram session state across reloads (future)

## 11. Related Use Cases

- UC-030: Random Mode
- UC-023: Review Cards (SRS)

## 12. Business Rules References

- BR-CRAM-01..02

## 13. UI Mockup Notes

- Mode banner (Cram) and a toggle “Apply to SRS”

## 14. API Endpoints

```
POST /api/review/cram/sessions
```

Request Body:

```json
{ "scope": { "type": "DECK", "id": "<uuid>" }, "applyToSrs": false }
```

Success (201): returns sessionId and first card

Errors:

- 400 invalid scope
- 404 scope not found

## 15. Test Cases

- TC-029-001: Start cram -> first card returned, shuffled
- TC-029-002: Toggle applyToSrs=false -> SRS unchanged after rating
- TC-029-003: Toggle applyToSrs=true -> SRS updated after rating
