# UC-007: Create Folder

## 1. Brief Description

User creates a new folder in the folder tree at any allowed level (up to max depth = 10).

## 2. Actors

- Primary Actor: Authenticated User
- Secondary Actor: Folder Service

## 3. Preconditions

- User is authenticated
- User can access the folder management UI
- Destination parent folder exists (or root selected)

## 4. Postconditions

### Success Postconditions

- New folder record created with correct parent_id and path/depth
- Depth constraint (<= 10) enforced
- Folder metadata initialized (name, description optional, timestamps)
- UI tree updates to show the new folder

### Failure Postconditions

- No folder created
- Error message displayed

## 5. Main Success Scenario (Basic Flow)

1. User clicks “New Folder” at current location (parent can be root or any folder)
2. System shows Create Folder form with fields:
   - Name (required, max 100 chars)
   - Description (optional)
3. User enters folder name (e.g., "IELTS Preparation")
4. User confirms creation
5. System validates:
   - Name not empty and <= 100 chars
   - Name unique within the same parent
   - Resulting depth <= 10
6. System creates folder in database with:
   - id (UUID), name, description, parent_id (nullable for root)
   - path/materialized-path, depth
   - created_at/updated_at
7. System returns 201 Created with new folder info
8. UI inserts the new node into the tree at the correct position

## 6. Alternative Flows

### 6a. Name Already Exists in Parent

Trigger: Step 5 - uniqueness violation

1. System detects duplicate name within the same parent
2. Returns 400 Bad Request
3. UI shows: "Folder name already exists in this location"
4. User revises name and retries (go to Step 3)

### 6b. Max Depth Exceeded

Trigger: Step 5 - depth > 10

1. System calculates resulting depth > 10
2. Returns 400 Bad Request: "Maximum folder depth (10 levels) exceeded"
3. User must choose a shallower parent (go to Step 1)

### 6c. Invalid Name

Trigger: Step 5 - name empty or too long

1. Client-side validation fails or server returns 400 with field errors
2. UI highlights "Name" with message
3. User corrects and retries (Step 3)

### 6d. Network/Server Error

Trigger: Step 6-7 fails

1. System returns 500 Internal Server Error
2. UI shows: "Unable to create folder. Please try again later."
3. Use case ends (failure)

## 7. Special Requirements

- Inline validation for name and uniqueness (optimistic check when possible)
- Keyboard support: Enter to submit, Esc to cancel
- Accessibility: labels, focus management

## 8. Business Rules / Constraints

- BR-FOLD-01: Max depth = 10
- BR-FOLD-02: Name unique within the same parent
- BR-FOLD-03: Name max length 100; not empty

## 9. Frequency of Occurrence

- Common while organizing decks; 1–20/day per active user

## 10. Open Issues

- None for MVP

## 11. Related Use Cases

- UC-008: Rename Folder
- UC-009: Move Folder
- UC-010: Copy Folder
- UC-011: Delete Folder
- UC-012: View Folder Statistics

## 12. Business Rules References

- See BR-FOLD-01..03 above

## 13. UI Mockup Notes

- Simple modal with name/description and parent shown in header
- Disable submit until validation passes

## 14. API Endpoint

```
POST /api/folders
```

Request Body:

```json
{
  "name": "IELTS Preparation",
  "description": "Exam preparation",
  "parentId": "<uuid-or-null>"
}
```

Success (201):

```json
{
  "id": "<uuid>",
  "name": "IELTS Preparation",
  "description": "Exam preparation",
  "parentId": null,
  "depth": 1,
  "path": "|<uuid>|",
  "createdAt": "2025-01-01T10:00:00Z"
}
```

Errors:

- 400 Name exists / Invalid field / Depth exceeded
- 500 Internal server error

## 15. Test Cases

- TC-007-001: Create root-level folder success
- TC-007-002: Create child folder at depth 10 success
- TC-007-003: Create child folder leading to depth 11 -> 400
- TC-007-004: Duplicate name under same parent -> 400
- TC-007-005: Empty name -> validation error
- TC-007-006: Long name (>100) -> validation error
