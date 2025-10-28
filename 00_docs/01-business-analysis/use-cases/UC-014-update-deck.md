# UC-014: Update Deck

## 1. Brief Description

User updates deck properties such as name and description. Cards are unchanged.

## 2. Actors

- Primary Actor: Authenticated User
- Secondary Actor: Deck Service

## 3. Preconditions

- User is authenticated
- Deck exists and belongs to the user

## 4. Postconditions

### Success Postconditions

- Deck fields updated (name, description)
- UI reflects latest values

### Failure Postconditions

- No changes saved
- Error displayed

## 5. Main Success Scenario (Basic Flow)

1. User selects a deck and opens "Edit Deck"
2. System shows current values for name and description
3. User updates fields (name <= 100 chars)
4. User saves changes
5. System validates:
   - Name not empty and <= 100 chars
   - Name unique within the same folder
6. System updates the deck and returns 200 OK
7. UI refreshes with updated info

## 6. Alternative Flows

### 6a. Duplicate Name in Folder

Trigger: Step 5

1. System detects name collision
2. Returns 400 Bad Request
3. UI shows: "Deck name already exists in this location"

### 6b. Empty or Too Long Name

Trigger: Step 5

1. Validation fails (client/server)
2. UI marks the field with error

### 6c. Deck Not Found / Forbidden

Trigger: Step 6

1. Return 404/403

## 7. Special Requirements

- Inline edit support where applicable
- Accessibility labels and focus handling

## 8. Business Rules / Constraints

- BR-DECK-01: Unique name within same folder
- BR-DECK-02: Name <= 100 chars, not empty

## 9. Frequency of Occurrence

- Occasional; typically after initial creation

## 10. Open Issues

- Consider trimming whitespace and normalizing case for uniqueness

## 11. Related Use Cases

- UC-013: Create Deck
- UC-015: Move Deck

## 12. Business Rules References

- BR-DECK-01, BR-DECK-02

## 13. UI Mockup Notes

- Simple form with Save/Cancel; disable save until valid

## 14. API Endpoint

```
PATCH /api/decks/{deckId}
```

Request Body:

```json
{ "name": "New Deck Name", "description": "Optional text" }
```

Success (200): returns updated deck

Errors:

- 400 duplicate/invalid name
- 404 not found
- 403 forbidden

## 15. Test Cases

- TC-014-001: Update name/description -> success
- TC-014-002: Duplicate name -> 400
- TC-014-003: Empty/too long name -> validation error
- TC-014-004: Deck not found -> 404
