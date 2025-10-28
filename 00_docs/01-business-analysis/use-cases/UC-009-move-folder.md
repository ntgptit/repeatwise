# UC-009: Move Folder

## 1. Brief Description

User moves a folder to a new destination (another folder or root). Depth constraints must be respected.

## 2. Actors

- Primary Actor: Authenticated User
- Secondary Actor: Folder Service

## 3. Preconditions

- User is authenticated
- Source folder exists and belongs to user
- Destination folder exists (or root is selected)

## 4. Postconditions

### Success Postconditions

- Folder parent updated to destination
- Path and depth of folder and all descendants updated consistently

### Failure Postconditions

- No changes applied
- Error shown

## 5. Main Success Scenario (Basic Flow)

1. User selects a folder to move
2. User chooses destination (another folder or root)
3. System validates:
   - Destination is not the folder itself nor its descendant
   - New depth for all descendants will not exceed 10
4. System updates parent_id, path, depth for the moved folder and its subtree in a transaction
5. System returns 200 OK with updated folder info
6. UI re-renders the tree reflecting new location

## 6. Alternative Flows

### 6a. Move Into Itself or Descendant

Trigger: Step 3

1. System detects cycle (self/descendant)
2. Returns 400: "Cannot move a folder into itself or its descendant"

### 6b. Max Depth Exceeded

Trigger: Step 3

1. Computed new depth > 10 for some nodes
2. Returns 400: "Maximum folder depth (10 levels) exceeded"

### 6c. Destination Not Found / Forbidden

Trigger: Step 3

1. Destination folder missing or belongs to another user
2. Returns 404 or 403

### 6d. Concurrency Conflict

Trigger: Step 4

1. Two moves affect the same subtree concurrently
2. Use optimistic locking/version field; on conflict return 409

## 7. Special Requirements

- Operation performed within a database transaction
- Efficient subtree update (CTE or path-based updates)

## 8. Business Rules / Constraints

- BR-FOLD-01: Max depth = 10
- BR-FOLD-04: Cannot move into self/descendant

## 9. Frequency of Occurrence

- Occasional: 0–10/day per active user

## 10. Open Issues

- Drag & drop UI is future; MVP uses dialog/selector

## 11. Related Use Cases

- UC-007: Create Folder
- UC-008: Rename Folder
- UC-010: Copy Folder

## 12. Business Rules References

- BR-FOLD-01, BR-FOLD-04

## 13. UI Mockup Notes

- Destination picker with breadcrumb preview

## 14. API Endpoint

```
POST /api/folders/{folderId}/move
```

Request Body:

```json
{ "destinationFolderId": "<uuid-or-null-for-root>" }
```

Success (200): returns updated folder

Errors:

- 400 invalid destination/depth/cycle
- 404/403 destination not found/forbidden
- 409 conflict (optimistic lock)

## 15. Test Cases

- TC-009-001: Move to root success
- TC-009-002: Move under sibling success
- TC-009-003: Move into itself -> 400
- TC-009-004: Move into descendant -> 400
- TC-009-005: Move causes depth > 10 -> 400
- TC-009-006: Destination not found -> 404
