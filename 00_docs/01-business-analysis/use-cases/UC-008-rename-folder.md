# UC-008: Rename Folder

## 1. Brief Description

Authenticated user renames an existing folder by updating its name and/or description while maintaining its position in the folder hierarchy. The folder's children, path, and depth remain unchanged.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** Folder Service

## 3. Preconditions

- User is authenticated with valid access token
- Target folder exists in the system
- Target folder belongs to the user (user_id matches)
- Target folder is not soft-deleted
- User can access the folder management interface

## 4. Postconditions

### Success Postconditions

- Folder name and/or description updated in database
- `updated_at` timestamp refreshed to current time
- Folder tree UI reflects new name immediately
- Success message displayed to user
- Folder remains at same position in hierarchy (parent unchanged)
- All child folders and decks unaffected

### Failure Postconditions

- Folder name and description unchanged
- Error message displayed to user
- User remains on current page/modal
- Folder tree unchanged

## 5. Main Success Scenario (Basic Flow)

1. User is viewing their folder tree
2. User identifies folder they want to rename (e.g., "IELTS" folder)
3. User initiates rename action:
   - Right-clicks folder and selects "Rename" from context menu, OR
   - Clicks three-dot menu icon next to folder and selects "Rename", OR
   - Selects folder and presses F2 key, OR
   - Double-clicks folder name
4. System displays Rename Folder modal/inline editor with fields:
   - Folder Name (text input, pre-filled with current name, max 100 chars)
   - Description (textarea, pre-filled with current description, max 500 chars)
5. System pre-populates form with current folder data
6. User modifies folder name from "IELTS" to "IELTS Preparation"
7. User optionally updates description from "Exam materials" to "IELTS exam preparation materials"
8. User confirms change:
   - Clicks "Save" or "Rename" button, OR
   - Presses Enter key (for inline editor)
9. System performs client-side validation:
   - Name is not empty after trim
   - Name length <= 100 characters
   - Description length <= 500 characters (if provided)
10. Client sends PATCH request to API with updated folder data:

    ```json
    {
      "name": "IELTS Preparation",
      "description": "IELTS exam preparation materials"
    }
    ```

11. Backend receives request and validates:
    - User is authenticated (valid JWT token)
    - Folder exists (folder_id from URL parameter)
    - Folder belongs to user (user_id matches)
    - Folder is not soft-deleted (deleted_at IS NULL)
    - Name is not empty and <= 100 characters
    - Description <= 500 characters (if provided)
12. System trims whitespace from name and description
13. System checks if name has changed
14. If name changed, system queries parent_id of target folder:

    ```sql
    SELECT parent_id FROM folders WHERE id = ? AND user_id = ?
    ```

15. System checks name uniqueness among siblings (folders with same parent):

    ```sql
    SELECT COUNT(*)
    FROM folders
    WHERE user_id = ?
      AND parent_id = ? (or IS NULL if root)
      AND name = ?
      AND id != ? (exclude current folder)
      AND deleted_at IS NULL
    ```

16. No duplicate found (COUNT = 0)
17. System updates folder record:

    ```sql
    UPDATE folders
    SET name = ?,
        description = ?,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = ? AND user_id = ?
    ```

18. Update successful (affected rows = 1)
19. System returns 200 OK with updated folder object:

    ```json
    {
      "id": "uuid-123",
      "name": "IELTS Preparation",
      "description": "IELTS exam preparation materials",
      "parentId": null,
      "path": "|uuid-123|",
      "depth": 1,
      "createdAt": "2025-01-20T10:00:00Z",
      "updatedAt": "2025-01-31T11:15:00Z"
    }
    ```

20. Client receives response
21. Client updates folder tree state:
    - Updates folder node name in tree
    - Updates folder description in state
    - Maintains folder position (no re-sorting unless alphabetical)
22. System displays success message: "Folder renamed successfully"
23. Modal/inline editor closes
24. User sees updated folder name in tree

## 6. Alternative Flows

### 6a. Duplicate Folder Name Among Siblings

**Trigger:** Step 15-16 - Name already exists among sibling folders

1. User attempts to rename folder to name that already exists among siblings
2. System queries for duplicate name among siblings
3. Duplicate found (COUNT > 0)
4. System returns 400 Bad Request:

   ```json
   {
     "error": "Duplicate folder name",
     "message": "Folder name 'IELTS Preparation' already exists in this location"
   }
   ```

5. Client receives error response
6. UI displays error message below name field or in toast
7. User must enter different name
8. Return to Step 6 (Main Flow)

**Note:** Uniqueness enforced only among siblings (same parent_id). Different parents can have folders with identical names.

### 6b. Empty Folder Name

**Trigger:** Step 9 - Name is empty or only whitespace after trim

1. User clears folder name field or enters only spaces
2. Client-side validation detects empty name after trim
3. UI displays inline error: "Folder name cannot be empty"
4. "Save" button disabled
5. User must enter valid name
6. Return to Step 6 (Main Flow)

### 6c. Folder Name Too Long

**Trigger:** Step 9 - Name length > 100 characters

1. User enters name with 101+ characters
2. Client-side validation detects length violation
3. UI displays character counter: "101/100" (red)
4. UI displays inline error: "Folder name must be 100 characters or less"
5. "Save" button disabled
6. User must shorten name
7. Return to Step 6 (Main Flow)

### 6d. Description Too Long

**Trigger:** Step 9 - Description length > 500 characters

1. User enters description with 501+ characters
2. Client-side validation detects length violation
3. UI displays character counter: "501/500" (red)
4. UI displays inline error: "Description must be 500 characters or less"
5. "Save" button disabled
6. User must shorten description
7. Return to Step 7 (Main Flow)

### 6e. No Changes Made

**Trigger:** Step 13 - User clicks Save without modifying any field

1. User opens rename modal but doesn't change name or description
2. User clicks "Save"
3. Client detects no changes (compares with original values)
4. Client can either:
   - **Option A:** Close modal immediately without API call (optimization)
   - **Option B:** Send request anyway (updates updated_at timestamp)
5. If Option A:
   - Modal closes
   - No API request
   - UI displays info message: "No changes made" (optional)
6. If Option B:
   - Proceed with update (Step 10-24 Main Flow)
   - Success message: "Folder updated successfully"
7. Use case ends (success)

### 6f. Folder Not Found

**Trigger:** Step 11 - Folder doesn't exist or was deleted

1. User attempts to rename folder
2. System queries folder by ID
3. No matching folder found (deleted or never existed)
4. System returns 404 Not Found:

   ```json
   {
     "error": "Folder not found",
     "message": "The folder you're trying to rename does not exist"
   }
   ```

5. Client displays error message
6. Client refreshes folder tree to sync state
7. Use case ends (failure)

### 6g. Folder Belongs to Another User

**Trigger:** Step 11 - Folder exists but user_id doesn't match

1. User attempts to rename folder that doesn't belong to them
2. System validates folder ownership
3. Ownership check fails (folder.user_id != request.user_id)
4. System returns 403 Forbidden:

   ```json
   {
     "error": "Access denied",
     "message": "You don't have permission to rename this folder"
   }
   ```

5. Client displays error message
6. Use case ends (failure)

**Note:** This should rarely happen in normal usage (UI should only show user's folders)

### 6h. User Cancels Rename

**Trigger:** Step 8 - User clicks "Cancel" or presses Esc

1. User opens rename modal/editor
2. User makes some changes
3. User clicks "Cancel" button or presses Esc key
4. Client discards changes
5. Modal/inline editor closes
6. Folder name and description remain unchanged
7. Folder tree unchanged
8. Use case ends (no changes)

### 6i. Network Error

**Trigger:** Step 10 - Network request fails

1. Client sends PATCH request
2. Network error occurs (no connection, timeout, etc.)
3. Request fails before reaching server
4. Client catches network error
5. UI displays error: "Network error. Please check your connection and try again."
6. User can retry by clicking "Save" again
7. Return to Step 8 (Main Flow)

### 6j. Database Error

**Trigger:** Step 17 - Database update fails

1. System attempts to update folder record
2. Database error occurs (connection lost, deadlock, etc.)
3. System logs error with details
4. System returns 500 Internal Server Error:

   ```json
   {
     "error": "Internal server error",
     "message": "Failed to rename folder. Please try again later."
   }
   ```

5. Client displays error message
6. User can retry rename operation
7. Use case ends (failure)

### 6k. Session Expired During Rename

**Trigger:** Step 11 - Access token expired

1. User opens rename modal/editor and takes time to edit
2. Access token expires (> 15 minutes)
3. User clicks "Save"
4. Backend validates token
5. Token expired
6. Backend returns 401 Unauthorized
7. Client axios interceptor catches 401
8. Client automatically refreshes token (UC-003)
9. Token refresh succeeds
10. Client retries rename request with new token
11. Request succeeds
12. Continue to Step 19 (Main Flow)

### 6l. Concurrent Rename Conflict

**Trigger:** Step 17 - Two users rename same folder simultaneously

1. User A and User B both open rename modal for same folder
2. User A changes name to "IELTS Prep" and saves first
3. User B changes name to "IELTS Preparation" and saves second
4. System processes both requests
5. First request succeeds (User A)
6. Second request succeeds (User B) - overwrites User A's change
7. Last write wins (no optimistic locking in MVP)
8. Use case ends (potential data loss for User A)

**Note:** Optimistic locking (version field) can be added in future to detect conflicts

## 7. Special Requirements

### 7.1 Performance

- Response time < 300ms for rename operation
- Inline editor should have no input lag
- Tree update should be instant (optimistic update)
- Support for undo/redo (future enhancement)

### 7.2 Validation

- **Name:**
  - Required (not empty after trim)
  - Min length: 1 character (after trim)
  - Max length: 100 characters
  - Trim leading/trailing whitespace
  - Unique among siblings (same parent)
- **Description:**
  - Optional (can be null or empty)
  - Max length: 500 characters
  - Trim leading/trailing whitespace

### 7.3 Usability

- **Inline editing:** Quick rename directly in tree (double-click or F2)
- **Modal editing:** Full form for name + description changes
- **Real-time validation:** Inline feedback as user types
- **Character counters:** Show remaining characters
- **Keyboard shortcuts:**
  - F2: Initiate rename
  - Enter: Save changes
  - Esc: Cancel rename
- **Auto-select text:** Select all text when opening inline editor
- **Loading states:** Show spinner during save
- **Optimistic updates:** Update UI immediately before API response
- **Rollback on error:** Revert UI if API call fails

### 7.4 Accessibility

- Proper ARIA labels for edit controls
- Error messages announced to screen readers
- Keyboard navigation support (Tab, Enter, Esc, F2)
- Focus management (focus on name field when opening)
- Color contrast meets WCAG AA standards

## 8. Technology and Data Variations

### 8.1 Inline Editing vs Modal Editing

**Inline Editing (Quick Rename):**

- Best for name-only changes
- Activated by: Double-click, F2, or click edit icon
- Shows text input directly in tree
- Auto-focus and select all text
- Save on Enter, cancel on Esc

**Modal Editing (Full Edit):**

- Best for name + description changes
- Shows full form in modal/dialog
- More space for description textarea
- Explicit Save/Cancel buttons

### 8.2 Optimistic Updates

Client updates UI immediately before API response:

```typescript
const renameFolder = async (folderId: string, newName: string) => {
  // Optimistic update
  const previousName = updateFolderInTree(folderId, newName);

  try {
    await api.patch(`/api/folders/${folderId}`, { name: newName });
    toast.success('Folder renamed successfully');
  } catch (error) {
    // Rollback on error
    updateFolderInTree(folderId, previousName);
    toast.error('Failed to rename folder');
  }
};
```

### 8.3 Name Uniqueness Check

Server-side validation:

```sql
-- Check for duplicate name among siblings
SELECT COUNT(*)
FROM folders
WHERE user_id = ?
  AND parent_id = ? (or IS NULL for root folders)
  AND name = ?
  AND id != ? (exclude current folder)
  AND deleted_at IS NULL
```

If COUNT > 0, return 400 Bad Request.

### 8.4 Debouncing Validation

For real-time validation, debounce API calls:

```typescript
const debouncedCheckUniqueness = debounce(async (name: string) => {
  const exists = await api.get(`/api/folders/check-name?name=${name}&parentId=${parentId}`);
  setNameError(exists ? 'Name already exists' : null);
}, 500);
```

## 9. Frequency of Occurrence

- Expected: 5-20 folder renames per day (MVP phase)
- Peak: 50-100 folder renames per day (post-launch)
- Per user: 0-5 folder renames per month (occasional reorganization)
- Common during initial setup and content reorganization

## 10. Open Issues

- **Batch rename:** Rename multiple folders at once - future
- **Rename history:** Track rename history for audit - future
- **Undo/redo:** Allow undo after rename - future
- **Optimistic locking:** Detect concurrent edits - future
- **Name suggestions:** Auto-suggest names based on content - future

## 11. Related Use Cases

- [UC-007: Create Folder](UC-007-create-folder.md) - Initial folder creation with name
- [UC-009: Move Folder](UC-009-move-folder.md) - Change folder parent
- [UC-010: Copy Folder](UC-010-copy-folder.md) - Copy creates new folder with similar name
- [UC-011: Delete Folder](UC-011-delete-folder.md) - Remove folder
- [UC-013: Create Deck](UC-013-create-deck.md) - Similar rename operation for decks

## 12. Business Rules References

- **BR-FOLD-02:** Folder name unique among siblings (same parent)
- **BR-FOLD-03:** Folder name max length 100 characters, not empty
- **BR-FOLD-05:** Rename operation does not affect hierarchy (parent, depth, path unchanged)

## 13. UI Mockup Notes

### Inline Editor (Quick Rename)

```
Folders
├─ [IELTS Preparation____] ✓ ✗  ← Inline editing
│  ├─ Listening
│  └─ Reading
└─ Japanese
   └─ N5
```

### Modal Editor (Full Edit)

```
┌─────────────────────────────────────────┐
│ Rename Folder                        [X]│
├─────────────────────────────────────────┤
│                                         │
│ Folder Name *                           │
│ ┌─────────────────────────────────┐    │
│ │ IELTS Preparation               │    │
│ └─────────────────────────────────┘    │
│ 17/100                                  │
│                                         │
│ Description                             │
│ ┌─────────────────────────────────┐    │
│ │ IELTS exam preparation          │    │
│ │ materials                       │    │
│ └─────────────────────────────────┘    │
│ 32/500                                  │
│                                         │
│                  [Cancel]  [Save Changes]│
└─────────────────────────────────────────┘
```

### Context Menu

```
Folders
├─ IELTS ← Right-click
│  ├─ Listening       ┌──────────────────┐
│  └─ Reading         │ 📝 Rename     F2 │
└─ Japanese           │ 📂 Move          │
                      │ 📋 Copy          │
                      │ 🗑️  Delete       │
                      │ ──────────────── │
                      │ 📊 View Stats    │
                      └──────────────────┘
```

## 14. API Endpoint

```http
PATCH /api/folders/{folderId}
```

**Request Headers:**

```http
Authorization: Bearer <access-token>
Content-Type: application/json
```

**Request Body (Partial Update):**

```json
{
  "name": "IELTS Preparation",
  "description": "IELTS exam preparation materials"
}
```

Or name only:

```json
{
  "name": "IELTS Preparation"
}
```

**Success Response (200 OK):**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "user-uuid",
  "name": "IELTS Preparation",
  "description": "IELTS exam preparation materials",
  "parentId": null,
  "path": "|550e8400-e29b-41d4-a716-446655440000|",
  "depth": 1,
  "createdAt": "2025-01-20T10:00:00Z",
  "updatedAt": "2025-01-31T11:15:00Z",
  "deletedAt": null
}
```

**Error Responses:**

400 Bad Request - Validation error:

```json
{
  "error": "Validation failed",
  "details": [
    {
      "field": "name",
      "message": "Folder name cannot be empty"
    }
  ]
}
```

400 Bad Request - Duplicate name:

```json
{
  "error": "Duplicate folder name",
  "message": "Folder name 'IELTS Preparation' already exists in this location"
}
```

404 Not Found - Folder not found:

```json
{
  "error": "Folder not found",
  "message": "The folder you're trying to rename does not exist"
}
```

403 Forbidden - Access denied:

```json
{
  "error": "Access denied",
  "message": "You don't have permission to rename this folder"
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
  "message": "Failed to rename folder. Please try again later."
}
```

## 15. Test Cases

### TC-008-001: Rename Folder Name Successfully

- **Given:** User has folder "IELTS" at root level
- **When:** User renames folder to "IELTS Preparation"
- **Then:** Folder name updated, tree shows new name, success message displayed

### TC-008-002: Rename Folder Description Successfully

- **Given:** User has folder "IELTS" with description "Exam"
- **When:** User updates description to "IELTS exam preparation materials"
- **Then:** Description updated in database, success message displayed

### TC-008-003: Rename Both Name and Description

- **Given:** User has folder "IELTS" with description "Exam"
- **When:** User changes name to "IELTS Prep" and description to "Materials"
- **Then:** Both fields updated successfully

### TC-008-004: Duplicate Name Among Siblings

- **Given:** User has two root folders: "IELTS" and "TOEFL"
- **When:** User tries to rename "TOEFL" to "IELTS"
- **Then:** 400 error with message "Folder name already exists in this location"

### TC-008-005: Same Name in Different Parents (Allowed)

- **Given:** User has folder "Grammar" under "English" and wants to rename "Vocabulary" under "French"
- **When:** User renames "Vocabulary" under "French" to "Grammar"
- **Then:** Rename succeeds (different parents allow same name)

### TC-008-006: Empty Folder Name

- **Given:** User opens rename modal for folder "IELTS"
- **When:** User clears name field and clicks Save
- **Then:** Inline error "Folder name cannot be empty", button disabled

### TC-008-007: Folder Name Too Long (101 characters)

- **Given:** User opens rename modal
- **When:** User enters name with 101 characters
- **Then:** Inline error "Folder name must be 100 characters or less", button disabled

### TC-008-008: Description Too Long (501 characters)

- **Given:** User opens rename modal
- **When:** User enters description with 501 characters
- **Then:** Inline error "Description must be 500 characters or less", button disabled

### TC-008-009: No Changes Made

- **Given:** User opens rename modal
- **When:** User clicks Save without making changes
- **Then:** Modal closes, no API call (or updates updated_at if sent)

### TC-008-010: Trim Whitespace from Name

- **Given:** User has folder "IELTS"
- **When:** User renames to "  IELTS Prep  " (with spaces)
- **Then:** Folder name saved as "IELTS Prep" (whitespace trimmed)

### TC-008-011: Cancel Rename Operation

- **Given:** User opens rename modal and changes name
- **When:** User clicks "Cancel" button
- **Then:** Modal closes, folder name unchanged

### TC-008-012: Keyboard Shortcut - F2 to Rename

- **Given:** User selects a folder in tree
- **When:** User presses F2 key
- **Then:** Inline editor opens with folder name selected

### TC-008-013: Keyboard Shortcut - Enter to Save

- **Given:** User is editing folder name inline
- **When:** User presses Enter key
- **Then:** Changes saved, inline editor closes

### TC-008-014: Keyboard Shortcut - Esc to Cancel

- **Given:** User is editing folder name inline
- **When:** User presses Esc key
- **Then:** Changes discarded, inline editor closes

### TC-008-015: Session Expired During Rename

- **Given:** User's access token expires after 15 minutes
- **When:** User renames folder
- **Then:** Token auto-refreshed, rename succeeds

### TC-008-016: Folder Not Found

- **Given:** User opens rename modal for folder that was deleted by another session
- **When:** User clicks Save
- **Then:** 404 error with message "Folder does not exist"

### TC-008-017: Network Error During Rename

- **Given:** User has no internet connection
- **When:** User tries to rename folder
- **Then:** Error "Network error. Please check your connection"

### TC-008-018: Optimistic Update - Success

- **Given:** User renames folder
- **When:** User clicks Save
- **Then:** UI updates immediately, then API confirms, success message shown

### TC-008-019: Optimistic Update - Rollback on Error

- **Given:** User renames folder to duplicate name
- **When:** User clicks Save
- **Then:** UI updates optimistically, API returns 400, UI reverts to original name, error shown

### TC-008-020: Character Counter Updates Real-Time

- **Given:** User is typing in name field
- **When:** User types each character
- **Then:** Character counter updates: "17/100", "18/100", etc.

### TC-008-021: Rename Nested Folder

- **Given:** User has folder "Grammar" under "English" under "Languages"
- **When:** User renames "Grammar" to "Grammar Basics"
- **Then:** Rename succeeds, folder remains in same position in hierarchy

### TC-008-022: Concurrent Rename Conflict

- **Given:** Two users open rename modal for same folder
- **When:** Both save different names simultaneously
- **Then:** Last write wins (both succeed, second overwrites first)

### TC-008-023: Double-Click to Rename

- **Given:** User views folder tree
- **When:** User double-clicks folder name
- **Then:** Inline editor opens with name selected

### TC-008-024: Auto-Select Text in Inline Editor

- **Given:** User opens inline editor with F2 or double-click
- **When:** Editor opens
- **Then:** All text in name field is automatically selected

### TC-008-025: Rename Does Not Affect Children

- **Given:** User has folder "IELTS" with children "Listening" and "Reading"
- **When:** User renames "IELTS" to "IELTS Prep"
- **Then:** Parent renamed, children unaffected and remain under parent

## 16. Database Operations

### Update Query

```sql
-- Update folder name and description
UPDATE folders
SET name = 'IELTS Preparation',
    description = 'IELTS exam preparation materials',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '550e8400-e29b-41d4-a716-446655440000'
  AND user_id = 'user-uuid'
  AND deleted_at IS NULL;
```

### Uniqueness Check Query

```sql
-- Check for duplicate name among siblings
SELECT COUNT(*)
FROM folders
WHERE user_id = 'user-uuid'
  AND parent_id IS NULL  -- or = 'parent-uuid' for nested folders
  AND name = 'IELTS Preparation'
  AND id != '550e8400-e29b-41d4-a716-446655440000'
  AND deleted_at IS NULL;
```

If COUNT > 0, name is duplicate.

## 17. Future Enhancements

- **Batch rename:** Rename multiple folders at once with pattern
- **Rename templates:** Quick rename patterns (e.g., add prefix/suffix)
- **Rename history:** Track all rename operations for audit trail
- **Undo/redo:** Allow undo within session
- **Optimistic locking:** Use version field to detect concurrent edits
- **Name suggestions:** AI-powered name suggestions based on content
- **Auto-rename on move:** Optionally rename when moving to avoid conflicts
- **Search and replace:** Bulk find/replace in folder names
- **Validation rules:** Custom regex patterns for folder naming conventions
- **Rich text descriptions:** Support markdown in descriptions
