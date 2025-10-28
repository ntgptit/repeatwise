# UC-013: Create Deck

## 1. Brief Description

User creates a new deck either under a selected folder or at the root level.

## 2. Actors

- Primary Actor: Authenticated User
- Secondary Actor: Deck Service

## 3. Preconditions

- User is authenticated
- Destination folder (if provided) exists and belongs to the user

## 4. Postconditions

### Success Postconditions

- New deck record created with correct parent folder reference (nullable for root)
- Deck metadata initialized (name, description, created_at/updated_at)
- UI updates to show the new deck in the correct location

### Failure Postconditions

- No deck created
- Error message displayed

## 5. Main Success Scenario (Basic Flow)

1. User clicks “New Deck” at current location (folder or root)
2. System displays form with fields:
   - Name (required, max 100 chars)
   - Description (optional)
3. User enters deck name (e.g., "Academic Words")
4. User confirms creation
5. System validates:
   - Name not empty and <= 100 chars
   - Name unique within the same folder (or root)
6. System creates deck in database:
   - id (UUID), name, description, folder_id (nullable), created_at, updated_at
7. System returns 201 Created with new deck info
8. UI renders the deck in the list/tree

## 6. Alternative Flows

### 6a. Duplicate Name Under Same Folder

Trigger: Step 5

1. System detects duplicate name in the same parent folder
2. Returns 400 Bad Request
3. UI shows: "Deck name already exists in this location"
4. User revises name and retries (Step 3)

### 6b. Invalid Name

Trigger: Step 5

1. Client/server validation fails (empty or >100 chars)
2. UI marks field with error
3. User corrects and resubmits

### 6c. Destination Folder Not Found / Forbidden

Trigger: Step 6

1. Folder id invalid or belongs to another user
2. Return 404 or 403

### 6d. Network/Server Error

Trigger: Step 6-7

1. System returns 500 Internal Server Error
2. UI shows: "Unable to create deck. Please try again later."

## 7. Special Requirements

- Inline validation and keyboard support (Enter submit, Esc cancel)
- Accessibility: labels, focus management

## 8. Business Rules / Constraints

- BR-DECK-01: Name unique within the same folder
- BR-DECK-02: Name max length 100; not empty
- BR-DECK-03: Deck can exist at root (no folder)

## 9. Frequency of Occurrence

- Common during initial organization; 1–20/day per active user

## 10. Open Issues

- None for MVP

## 11. Related Use Cases

- UC-014: Update Deck
- UC-015: Move Deck
- UC-016: Copy Deck
- UC-017: Delete Deck
- UC-018: Create Card

## 12. Business Rules References

- BR-DECK-01..03

## 13. UI Mockup Notes

- Modal with parent context shown; disable submit until valid

## 14. API Endpoint

```
POST /api/decks
```

Request Body:

```json
{
  "name": "Academic Words",
  "description": "Vocabulary for IELTS",
  "folderId": "<uuid-or-null>"
}
```

Success (201):

```json
{
  "id": "<uuid>",
  "name": "Academic Words",
  "description": "Vocabulary for IELTS",
  "folderId": null,
  "createdAt": "2025-01-01T10:00:00Z"
}
```

Errors:

- 400 duplicate/invalid name
- 404/403 folder not found/forbidden
- 500 internal error

## 15. Test Cases

- TC-013-001: Create deck at root -> success
- TC-013-002: Create deck under folder -> success
- TC-013-003: Duplicate name in same folder -> 400
- TC-013-004: Empty/too long name -> validation error
- TC-013-005: Folder not found -> 404
