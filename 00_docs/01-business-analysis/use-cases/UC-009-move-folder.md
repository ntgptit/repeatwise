# UC-009: Move Folder

## 1. Brief Description

Authenticated user moves a folder to a different location in the hierarchy by changing its parent folder. The system ensures depth constraints are maintained, prevents circular references, and updates the materialized path for the moved folder and all its descendants.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** Folder Service

## 3. Preconditions

- User is authenticated with valid access token
- Source folder exists and belongs to the user
- Destination parent folder exists and belongs to the user (or root is selected)
- Source folder is not soft-deleted
- Destination folder is not soft-deleted (if applicable)
- User can access the folder management interface

## 4. Postconditions

### Success Postconditions

- Source folder's `parent_id` updated to new parent (or NULL for root)
- Source folder's `path` recalculated based on new parent path
- Source folder's `depth` recalculated based on new parent depth
- All descendant folders' `path` and `depth` updated recursively
- All updates performed in single database transaction (atomic operation)
- `updated_at` timestamp refreshed for moved folder
- Folder tree UI reflects new location immediately
- Success message displayed to user
- Source folder and all descendants appear under new parent

### Failure Postconditions

- No changes to folder hierarchy
- Source folder remains at original location
- Error message displayed to user
- User remains on current page
- Folder tree unchanged

## 5. Main Success Scenario (Basic Flow)

1. User is viewing their folder tree
2. User identifies folder they want to move (e.g., "Grammar" folder under "English")
3. User initiates move action:
   - Drags folder to new location (drag & drop), OR
   - Right-clicks folder and selects "Move" from context menu, OR
   - Clicks three-dot menu and selects "Move"
4. System displays Move Folder modal/destination picker with:
   - Current location breadcrumb (e.g., "English > Grammar")
   - Destination folder tree/picker (all folders except source and its descendants)
   - "Root" option to move to top level
5. User selects destination folder (e.g., "Japanese" folder)
6. User confirms move by clicking "Move Here" button
7. System performs client-side validation:
   - Destination is not the source folder itself
   - Destination is not a descendant of source folder (prevents circular reference)
8. Client sends POST request to API:

   ```json
   {
     "destinationFolderId": "uuid-japanese"
   }
   ```

9. Backend receives request and validates:
   - User is authenticated (valid JWT token)
   - Source folder exists and belongs to user
   - Destination folder exists and belongs to user (if not null)
   - Source folder is not soft-deleted
   - Destination folder is not soft-deleted (if provided)
10. System queries source folder details:

    ```sql
    SELECT id, parent_id, path, depth
    FROM folders
    WHERE id = ? AND user_id = ? AND deleted_at IS NULL
    ```

11. System checks if destination is NULL (root move) or specific folder
12. If moving to specific folder, system queries destination folder:

    ```sql
    SELECT id, path, depth
    FROM folders
    WHERE id = ? AND user_id = ? AND deleted_at IS NULL
    ```

13. System validates destination is not source itself:
    - destination_id != source_id
14. System validates destination is not descendant of source (prevents cycle):

    ```sql
    -- Check if destination path starts with source path
    -- Example: source.path = "|a|b|", destination.path = "|a|b|c|"
    -- This means destination is descendant of source
    ```

    - destination.path DOES NOT START WITH source.path
15. System calculates new depth for source folder:
    - If moving to root: new_depth = 1
    - If moving to folder: new_depth = destination.depth + 1
16. System calculates depth increase for source and descendants:
    - depth_delta = new_depth - source.current_depth
17. System queries all descendants of source folder:

    ```sql
    SELECT id, depth
    FROM folders
    WHERE path LIKE ? AND user_id = ? AND deleted_at IS NULL
    -- path LIKE '|source-id|%'
    ```

18. System validates max depth constraint for all affected folders:
    - For each descendant: new_depth = current_depth + depth_delta
    - Ensure all new depths <= 10 (BR-FOLD-01)
19. All depth validations pass
20. System starts database transaction
21. System updates source folder's parent_id:

    ```sql
    UPDATE folders
    SET parent_id = ?,  -- new parent id or NULL for root
        updated_at = CURRENT_TIMESTAMP
    WHERE id = ? AND user_id = ?
    ```

22. System recalculates and updates path and depth for source folder:
    - If root: new_path = "|source-id|", new_depth = 1
    - If folder: new_path = "|dest-path|source-id|", new_depth = dest.depth + 1

    ```sql
    UPDATE folders
    SET path = ?,
        depth = ?
    WHERE id = ? AND user_id = ?
    ```

23. System recursively updates all descendants' path and depth:

    ```sql
    UPDATE folders
    SET path = REPLACE(path, old_source_path, new_source_path),
        depth = depth + depth_delta
    WHERE path LIKE ? AND user_id = ? AND deleted_at IS NULL
    -- path LIKE 'old_source_path%'
    ```

24. System commits transaction
25. System returns 200 OK with updated folder structure:

    ```json
    {
      "id": "uuid-grammar",
      "name": "Grammar",
      "parentId": "uuid-japanese",
      "path": "|uuid-japanese|uuid-grammar|",
      "depth": 2,
      "updatedAt": "2025-01-31T12:00:00Z"
    }
    ```

26. Client receives response
27. Client updates folder tree state:
    - Removes source folder from old location
    - Adds source folder to new location under destination
    - Updates all descendant positions recursively
28. System displays success message: "Folder moved successfully"
29. Modal closes
30. User sees folder at new location in tree

## 6. Alternative Flows

### 6a. Cannot Move Into Itself

**Trigger:** Step 13 - Destination is the source folder itself

1. User selects source folder as destination
2. System validates destination != source
3. Validation fails (destination_id == source_id)
4. System returns 400 Bad Request:

   ```json
   {
     "error": "Invalid destination",
     "message": "Cannot move a folder into itself"
   }
   ```

5. Client displays error message
6. User must select different destination
7. Return to Step 5 (Main Flow)

### 6b. Cannot Move Into Descendant (Circular Reference)

**Trigger:** Step 14 - Destination is a descendant of source folder

1. User attempts to move "English" folder into "English > Grammar"
2. System checks if destination path starts with source path
3. destination.path = "|english|grammar|" starts with source.path = "|english|"
4. Validation fails (circular reference detected)
5. System returns 400 Bad Request:

   ```json
   {
     "error": "Invalid destination",
     "message": "Cannot move a folder into its own descendant"
   }
   ```

6. Client displays error message in modal
7. User must select different destination (not a child of source)
8. Return to Step 5 (Main Flow)

**Business Rule:** BR-FOLD-04 - Prevent circular references in folder hierarchy

### 6c. Maximum Depth Exceeded After Move

**Trigger:** Step 18 - Moving folder would exceed depth limit of 10

1. User attempts to move folder with deep subtree to location that would exceed max depth
2. System calculates new depths for all affected folders
3. Some descendant would have depth > 10
4. Validation fails
5. System returns 400 Bad Request:

   ```json
   {
     "error": "Max depth exceeded",
     "message": "Moving this folder would exceed maximum depth (10 levels). Current structure has 8 levels, destination is at level 3."
   }
   ```

6. Client displays detailed error message
7. User must choose shallower destination
8. Return to Step 5 (Main Flow)

**Example:**

- Source folder at depth 3 with subtree extending to depth 8 (5 levels deep)
- Destination folder at depth 6
- After move: subtree would extend to depth 6 + 5 = 11 (exceeds limit)

### 6d. Source Folder Not Found

**Trigger:** Step 10 - Source folder doesn't exist or was deleted

1. User attempts to move folder that was deleted by another session
2. System queries source folder
3. No matching folder found
4. System returns 404 Not Found:

   ```json
   {
     "error": "Folder not found",
     "message": "The folder you're trying to move does not exist"
   }
   ```

5. Client displays error message
6. Client refreshes folder tree to sync state
7. Use case ends (failure)

### 6e. Destination Folder Not Found

**Trigger:** Step 12 - Destination folder doesn't exist or was deleted

1. User selects destination folder
2. Before API call completes, another session deletes destination folder
3. System queries destination folder
4. No matching folder found
5. System returns 404 Not Found:

   ```json
   {
     "error": "Destination not found",
     "message": "The destination folder no longer exists"
   }
   ```

6. Client displays error message
7. Client refreshes folder tree
8. Use case ends (failure)

### 6f. Folder Belongs to Another User

**Trigger:** Step 9 - Source or destination folder doesn't belong to user

1. User attempts to move folder
2. System validates ownership
3. Ownership check fails (folder.user_id != request.user_id)
4. System returns 403 Forbidden:

   ```json
   {
     "error": "Access denied",
     "message": "You don't have permission to move this folder"
   }
   ```

5. Client displays error message
6. Use case ends (failure)

### 6g. User Cancels Move

**Trigger:** Step 6 - User clicks "Cancel" button

1. User opens move modal/picker
2. User selects destination but changes mind
3. User clicks "Cancel" button or closes modal
4. Client discards selection
5. Modal closes
6. No API request sent
7. Folder remains at original location
8. Use case ends (no changes)

### 6h. Network Error

**Trigger:** Step 8 - Network request fails

1. Client sends POST request
2. Network error occurs (no connection, timeout, etc.)
3. Request fails before reaching server
4. Client catches network error
5. UI displays error: "Network error. Please check your connection and try again."
6. User can retry by selecting destination and clicking "Move Here" again
7. Return to Step 6 (Main Flow)

### 6i. Database Transaction Failure

**Trigger:** Step 20-24 - Database error during transaction

1. System starts transaction
2. Partially updates folder hierarchy
3. Database error occurs (connection lost, deadlock, constraint violation)
4. System rolls back entire transaction
5. No changes applied to any folders
6. System logs error with details
7. System returns 500 Internal Server Error:

   ```json
   {
     "error": "Internal server error",
     "message": "Failed to move folder. Please try again later."
   }
   ```

8. Client displays error message
9. Folder remains at original location
10. Use case ends (failure)

**Critical:** Transaction ensures atomicity - either all folders updated or none

### 6j. Session Expired During Move

**Trigger:** Step 9 - Access token expired

1. User opens move modal and takes time to select destination
2. Access token expires (> 15 minutes)
3. User clicks "Move Here"
4. Backend validates token
5. Token expired
6. Backend returns 401 Unauthorized
7. Client axios interceptor catches 401
8. Client automatically refreshes token (UC-003)
9. Token refresh succeeds
10. Client retries move request with new token
11. Request succeeds
12. Continue to Step 25 (Main Flow)

### 6k. Move to Same Parent (No-Op)

**Trigger:** Step 11 - User selects current parent as destination

1. User selects folder "Grammar" under "English"
2. User opens move modal
3. User selects "English" as destination (current parent)
4. System detects destination == current parent
5. System can either:
   - **Option A:** Return success immediately (no database update needed)
   - **Option B:** Return 400 with message "Folder is already in this location"
6. If Option A:
   - Return 200 OK with current folder data
   - Display info: "Folder is already in this location"
7. If Option B:
   - Return 400 with error message
   - User must select different destination
8. Use case ends

### 6l. Concurrent Move Conflict

**Trigger:** Step 20-24 - Two users move same folder simultaneously

1. User A and User B both move same folder to different destinations
2. Both API requests start at nearly same time
3. System processes both requests
4. Database uses row-level locking
5. First transaction (User A) acquires lock, completes successfully
6. Second transaction (User B) waits for lock
7. When lock released, second transaction proceeds
8. Second move succeeds (overwrites first move)
9. Folder ends up at User B's destination
10. User A sees folder at unexpected location
11. Use case ends (last write wins)

**Note:** Optimistic locking can be added to detect conflicts

## 7. Special Requirements

### 7.1 Performance

- Response time < 1 second for moving folder with < 100 descendants
- Response time < 5 seconds for moving folder with 100-1000 descendants
- Use efficient SQL UPDATE with path pattern matching for descendants
- Transaction should be short-lived to minimize locking
- UI should show loading indicator for long operations

### 7.2 Data Integrity

- **Atomic operation:** All updates in single transaction (all or nothing)
- **Referential integrity:** Foreign key constraints on parent_id
- **Path consistency:** Materialized path must reflect actual hierarchy
- **Depth accuracy:** Depth field must match path-based depth calculation
- **Cascade rules:** Moving parent moves all descendants automatically

### 7.3 Validation

- **Destination != Source:** Cannot move folder into itself
- **Destination not descendant:** Prevents circular references
- **Max depth constraint:** All folders after move must have depth <= 10
- **Ownership check:** Source and destination must belong to user

### 7.4 Usability

- **Drag & drop:** Intuitive drag-and-drop in tree UI (future)
- **Destination picker:** Clear visual picker showing valid destinations
- **Invalid destinations:** Disable/hide source and its descendants in picker
- **Breadcrumb preview:** Show where folder will be moved
- **Confirmation dialog:** For moves with many descendants (optional)
- **Loading states:** Show progress for large moves
- **Undo support:** Allow undo after move (future)

### 7.5 Accessibility

- Keyboard navigation for destination selection
- Clear ARIA labels for destination picker
- Error messages announced to screen readers
- Focus management after move completes

## 8. Technology and Data Variations

### 8.1 Materialized Path Update Strategy

**Strategy:** Use SQL REPLACE to update paths efficiently

```sql
-- Example: Move "Grammar" from "English" to "Japanese"
-- Old path: |uuid-english|uuid-grammar|
-- New path: |uuid-japanese|uuid-grammar|

-- Update source folder
UPDATE folders
SET parent_id = 'uuid-japanese',
    path = '|uuid-japanese|uuid-grammar|',
    depth = 2
WHERE id = 'uuid-grammar';

-- Update all descendants
UPDATE folders
SET path = REPLACE(path, '|uuid-english|uuid-grammar|', '|uuid-japanese|uuid-grammar|'),
    depth = depth + 1  -- depth_delta = new_depth - old_depth
WHERE path LIKE '|uuid-english|uuid-grammar|%'
  AND id != 'uuid-grammar';
```

### 8.2 Depth Calculation

```typescript
// Calculate new depth after move
const calculateNewDepth = (
  sourceCurrentDepth: number,
  destDepth: number | null  // null for root
): number => {
  return destDepth === null ? 1 : destDepth + 1;
};

// Calculate depth delta for descendants
const depthDelta = newDepth - sourceCurrentDepth;

// Validate all descendants will be within limit
const maxDescendantDepth = sourceSubtreeMaxDepth + depthDelta;
if (maxDescendantDepth > 10) {
  throw new Error('Max depth exceeded');
}
```

### 8.3 Circular Reference Detection

```typescript
const isCircularReference = (
  sourcePath: string,
  destPath: string
): boolean => {
  // Destination is descendant if its path starts with source path
  return destPath.startsWith(sourcePath);
};

// Example:
// source.path = "|a|b|"
// dest.path = "|a|b|c|" → startsWith → CIRCULAR (invalid)
// dest.path = "|a|d|" → not startsWith → OK
```

### 8.4 Transaction Isolation Level

```sql
BEGIN TRANSACTION ISOLATION LEVEL READ COMMITTED;

-- Update parent_id, path, depth for source
UPDATE folders ... WHERE id = ?;

-- Update path, depth for all descendants
UPDATE folders ... WHERE path LIKE ?;

COMMIT;
```

Use READ COMMITTED to balance consistency and performance.

## 9. Frequency of Occurrence

- Expected: 10-30 folder moves per day (MVP phase)
- Peak: 50-100 folder moves per day (post-launch)
- Per user: 1-10 folder moves per month (reorganization)
- Common during initial setup and periodic reorganization

## 10. Open Issues

- **Drag & drop UI:** Implement intuitive drag-and-drop interface - future
- **Batch move:** Move multiple folders at once - future
- **Move history:** Track move operations for audit trail - future
- **Undo move:** Allow undo within session - future
- **Move with rename:** Optionally rename when moving to avoid conflicts - future
- **Move confirmation:** Confirm before moving folders with many descendants - future

## 11. Related Use Cases

- [UC-007: Create Folder](UC-007-create-folder.md) - Create folder at specific location
- [UC-008: Rename Folder](UC-008-rename-folder.md) - Rename without moving
- [UC-010: Copy Folder](UC-010-copy-folder.md) - Duplicate folder at new location
- [UC-011: Delete Folder](UC-011-delete-folder.md) - Remove folder from hierarchy
- [UC-015: Move Deck](UC-015-move-deck.md) - Similar move operation for decks

## 12. Business Rules References

- **BR-FOLD-01:** Maximum folder depth = 10 levels
- **BR-FOLD-04:** Cannot move folder into itself or its descendant (prevent cycles)
- **BR-FOLD-06:** Move operation updates path and depth for entire subtree
- **BR-FOLD-07:** Move must be atomic (transaction-based)

## 13. UI Mockup Notes

### Destination Picker Modal

```
┌─────────────────────────────────────────┐
│ Move Folder                          [X]│
├─────────────────────────────────────────┤
│                                         │
│ Moving: English > Grammar               │
│                                         │
│ Select destination:                     │
│ ┌─────────────────────────────────┐    │
│ │ ○ Root                          │    │
│ │ ○ IELTS Preparation             │    │
│ │   ○ Listening                   │    │
│ │   ○ Reading                     │    │
│ │ ● Japanese                      │    │ ← Selected
│ │   ○ N5                          │    │
│ │ ✗ English (current)             │    │ ← Disabled
│ │   ✗ Grammar (source)            │    │ ← Disabled
│ │   ✗ Vocabulary                  │    │ ← Disabled (child)
│ └─────────────────────────────────┘    │
│                                         │
│ Preview: Japanese > Grammar             │
│                                         │
│                    [Cancel]  [Move Here]│
└─────────────────────────────────────────┘
```

### Drag & Drop (Future)

```
Folders
├─ IELTS Preparation
│  ├─ Listening
│  └─ Reading
├─ Japanese ← Drop here
│  └─ N5
└─ English
   ├─ [Grammar] ← Dragging
   └─ Vocabulary
```

### Breadcrumb Preview

```
┌─────────────────────────────────────────┐
│ Moving "Grammar" to:                    │
│                                         │
│ From: English > Grammar                 │
│ To:   Japanese > Grammar                │
│                                         │
│ This will affect 15 subfolders and     │
│ 43 decks.                               │
│                                         │
│                    [Cancel]  [Move Here]│
└─────────────────────────────────────────┘
```

## 14. API Endpoint

```http
POST /api/folders/{folderId}/move
```

**Request Headers:**

```http
Authorization: Bearer <access-token>
Content-Type: application/json
```

**Request Body:**

```json
{
  "destinationFolderId": "uuid-japanese"
}
```

Or move to root:

```json
{
  "destinationFolderId": null
}
```

**Success Response (200 OK):**

```json
{
  "id": "uuid-grammar",
  "userId": "user-uuid",
  "name": "Grammar",
  "description": "Grammar lessons",
  "parentId": "uuid-japanese",
  "path": "|uuid-japanese|uuid-grammar|",
  "depth": 2,
  "createdAt": "2025-01-20T10:00:00Z",
  "updatedAt": "2025-01-31T12:00:00Z",
  "deletedAt": null,
  "affectedDescendants": 5
}
```

**Error Responses:**

400 Bad Request - Circular reference:

```json
{
  "error": "Invalid destination",
  "message": "Cannot move a folder into itself or its own descendant"
}
```

400 Bad Request - Max depth exceeded:

```json
{
  "error": "Max depth exceeded",
  "message": "Moving this folder would exceed maximum depth (10 levels). Current structure has 8 levels, destination is at level 3."
}
```

400 Bad Request - Same destination:

```json
{
  "error": "Invalid move",
  "message": "Folder is already in this location"
}
```

404 Not Found - Source folder not found:

```json
{
  "error": "Folder not found",
  "message": "The folder you're trying to move does not exist"
}
```

404 Not Found - Destination not found:

```json
{
  "error": "Destination not found",
  "message": "The destination folder does not exist"
}
```

403 Forbidden - Access denied:

```json
{
  "error": "Access denied",
  "message": "You don't have permission to move this folder"
}
```

401 Unauthorized:

```json
{
  "error": "Unauthorized",
  "message": "Authentication required"
}
```

500 Internal Server Error:

```json
{
  "error": "Internal server error",
  "message": "Failed to move folder. Please try again later."
}
```

## 15. Test Cases

### TC-009-001: Move Folder to Root Successfully

- **Given:** User has folder "Grammar" under "English"
- **When:** User moves "Grammar" to root level
- **Then:** Folder moved to root, parentId=null, depth=1, path updated

### TC-009-002: Move Folder to Another Folder Successfully

- **Given:** User has folder "Grammar" under "English"
- **When:** User moves "Grammar" to "Japanese" folder
- **Then:** Folder moved, parentId="Japanese", depth=2, path updated

### TC-009-003: Move Folder With Descendants

- **Given:** User has "Grammar" with children "Nouns" and "Verbs"
- **When:** User moves "Grammar" to "Japanese"
- **Then:** Grammar and all children moved, paths and depths updated

### TC-009-004: Cannot Move Into Itself

- **Given:** User has folder "English"
- **When:** User tries to move "English" into "English"
- **Then:** 400 error with message "Cannot move a folder into itself"

### TC-009-005: Cannot Move Into Descendant

- **Given:** User has "English" > "Grammar" > "Nouns"
- **When:** User tries to move "English" into "Nouns"
- **Then:** 400 error with message "Cannot move folder into its own descendant"

### TC-009-006: Max Depth Exceeded After Move

- **Given:** User has deep folder structure (8 levels)
- **When:** User tries to move to destination at depth 3
- **Then:** 400 error with message "Maximum depth exceeded"

### TC-009-007: Move to Same Parent (No-Op)

- **Given:** User has folder "Grammar" under "English"
- **When:** User moves "Grammar" to "English" (current parent)
- **Then:** Success with info "Folder is already in this location"

### TC-009-008: Source Folder Not Found

- **Given:** User opens move modal for folder that was deleted
- **When:** User selects destination and clicks Move
- **Then:** 404 error with message "Folder does not exist"

### TC-009-009: Destination Folder Not Found

- **Given:** User selects destination that gets deleted before move completes
- **When:** Move request is sent
- **Then:** 404 error with message "Destination folder does not exist"

### TC-009-010: Session Expired During Move

- **Given:** User's access token expires after 15 minutes
- **When:** User moves folder
- **Then:** Token auto-refreshed, move succeeds

### TC-009-011: Network Error During Move

- **Given:** User has no internet connection
- **When:** User tries to move folder
- **Then:** Error "Network error. Please check your connection"

### TC-009-012: Transaction Rollback on Error

- **Given:** Database error occurs during descendant update
- **When:** Move operation is processing
- **Then:** Transaction rolls back, folder remains at original location

### TC-009-013: Move Updates Descendant Paths

- **Given:** User has "English" > "Grammar" > "Nouns"
- **When:** User moves "Grammar" from "English" to "Japanese"
- **Then:** "Nouns" path updated from "|english|grammar|nouns|" to "|japanese|grammar|nouns|"

### TC-009-014: Move Updates Descendant Depths

- **Given:** User has "Grammar" at depth 2 with "Nouns" at depth 3
- **When:** User moves "Grammar" to root (depth 1)
- **Then:** "Nouns" depth updated to 2 (decreased by 1)

### TC-009-015: Move Multiple Levels Deep

- **Given:** User has "A" > "B" > "C" > "D"
- **When:** User moves "B" to "X" > "Y"
- **Then:** All descendants updated, "D" now at "X" > "Y" > "B" > "C" > "D"

### TC-009-016: Concurrent Move Conflict

- **Given:** Two users move same folder to different destinations
- **When:** Both moves process simultaneously
- **Then:** Last write wins (folder ends at second user's destination)

### TC-009-017: Move Empty Folder

- **Given:** User has folder "Empty" with no children
- **When:** User moves "Empty" to new location
- **Then:** Move succeeds, only source folder updated

### TC-009-018: Move Folder With 100 Descendants

- **Given:** User has large folder structure with 100 subfolders
- **When:** User moves parent folder
- **Then:** All 100 descendants updated in single transaction, completes within 5 seconds

### TC-009-019: Depth Calculation After Move to Root

- **Given:** User has folder at depth 5
- **When:** User moves folder to root
- **Then:** New depth = 1, depth_delta = -4, all descendants decreased by 4

### TC-009-020: Depth Calculation After Move to Deep Folder

- **Given:** User has folder at depth 2
- **When:** User moves folder to destination at depth 7
- **Then:** New depth = 8, depth_delta = +6, all descendants increased by 6

## 16. Database Operations

### Move Transaction Example

```sql
BEGIN TRANSACTION;

-- Step 1: Update source folder's parent_id
UPDATE folders
SET parent_id = 'uuid-japanese',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 'uuid-grammar' AND user_id = 'user-uuid';

-- Step 2: Update source folder's path and depth
UPDATE folders
SET path = '|uuid-japanese|uuid-grammar|',
    depth = 2
WHERE id = 'uuid-grammar' AND user_id = 'user-uuid';

-- Step 3: Update all descendants' paths and depths
UPDATE folders
SET path = REPLACE(path, '|uuid-english|uuid-grammar|', '|uuid-japanese|uuid-grammar|'),
    depth = depth + 1  -- depth_delta
WHERE path LIKE '|uuid-english|uuid-grammar|%'
  AND id != 'uuid-grammar'
  AND user_id = 'user-uuid'
  AND deleted_at IS NULL;

COMMIT;
```

### Circular Reference Detection Query

```sql
-- Check if destination is descendant of source
SELECT COUNT(*)
FROM folders
WHERE id = ? -- destination_id
  AND path LIKE CONCAT(?, '%')  -- source_path
  AND user_id = ?;

-- If COUNT > 0, destination is descendant (invalid)
```

### Max Depth Validation Query

```sql
-- Get maximum depth in source subtree
SELECT MAX(depth) AS max_depth
FROM folders
WHERE path LIKE ? -- source_path + '%'
  AND user_id = ?
  AND deleted_at IS NULL;

-- Calculate: new_max_depth = max_depth + depth_delta
-- Validate: new_max_depth <= 10
```

## 17. Future Enhancements

- **Drag & drop UI:** Intuitive visual drag-and-drop in tree
- **Batch move:** Move multiple folders at once
- **Smart destination suggestions:** Suggest likely destinations based on content
- **Move with rename:** Automatically rename on conflict
- **Move history:** Track all move operations for audit
- **Undo move:** Allow undo within session
- **Move preview:** Show before/after tree structure
- **Progress indicator:** Show progress for large moves (100+ descendants)
- **Move validation API:** Pre-validate move before confirming
- **Keyboard shortcuts:** Ctrl+X to cut, Ctrl+V to paste folder
