# UC-008: Rename Folder

## 1. Brief Description
User renames an existing folder while keeping its position in the tree unchanged.

## 2. Actors
- Primary Actor: Authenticated User
- Secondary Actor: Folder Service

## 3. Preconditions
- User is authenticated
- Target folder exists and belongs to the user

## 4. Postconditions
### Success Postconditions
- Folder name updated in database
- Tree renders updated label

### Failure Postconditions
- Name unchanged
- Error displayed

## 5. Main Success Scenario (Basic Flow)
1. User selects a folder and chooses “Rename”
2. System shows inline edit or modal with current name
3. User inputs new name (<= 100 chars)
4. User confirms
5. System validates:
   - Name not empty and <= 100 chars
   - Name unique among siblings
6. System updates the folder name (and updated_at)
7. System returns 200 OK with updated folder
8. UI updates the node label

## 6. Alternative Flows
### 6a. Duplicate Name in Parent
Trigger: Step 5
1. System detects name collision under same parent
2. Returns 400 Bad Request
3. UI shows: "Folder name already exists in this location"
4. User retries with different name

### 6b. Empty or Too Long Name
Trigger: Step 5
1. Client/server validation fails
2. UI marks field with error
3. User corrects and resubmits

### 6c. Folder Not Found
Trigger: Step 6
1. Target folder id not found (or not owned by user)
2. Return 404 Not Found (or 403 Forbidden)

## 7. Special Requirements
- Inline editing UX with graceful cancel (Esc)
- Accessibility: focus and aria-live updates on error

## 8. Business Rules / Constraints
- BR-FOLD-02: Name unique in parent
- BR-FOLD-03: Name <= 100 chars, not empty

## 9. Frequency of Occurrence
- Occasional: 0–10/day per active user

## 10. Open Issues
- Consider trim/normalize whitespace when comparing uniqueness

## 11. Related Use Cases
- UC-007: Create Folder
- UC-009: Move Folder

## 12. Business Rules References
- BR-FOLD-02, BR-FOLD-03

## 13. UI Mockup Notes
- Inline rename in tree with confirm/cancel icons

## 14. API Endpoint
```
PATCH /api/folders/{folderId}
```
Request Body:
```json
{ "name": "New Folder Name" }
```
Success (200): returns updated folder

Errors:
- 400 duplicate/invalid name
- 404 not found
- 403 forbidden (ownership)

## 15. Test Cases
- TC-008-001: Rename success
- TC-008-002: Duplicate name -> 400
- TC-008-003: Empty name -> validation error
- TC-008-004: Over length -> validation error
- TC-008-005: Folder not found -> 404
