# UC-007: Create Folder

## 1. Brief Description

Authenticated user creates a new folder in the hierarchical folder tree to organize their decks. Folders can be nested up to a maximum depth of 10 levels to maintain performance and prevent overly complex structures.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** Folder Service

## 3. Preconditions

- User is authenticated with valid access token
- User can access the folder management interface (sidebar/tree view)
- User is viewing the location where they want to create a folder
- If creating nested folder, parent folder must exist and belong to the user

## 4. Postconditions

### Success Postconditions

- New folder record created in `folders` table with:
  - Unique UUID
  - User-provided name
  - Optional description
  - Correct parent_id (null for root level)
  - Calculated path (materialized path for hierarchy)
  - Calculated depth (level in tree)
  - Timestamps (created_at, updated_at)
- Folder appears in UI tree at correct position
- User can immediately use folder to organize decks
- Success message displayed
- Folder tree updated without page reload

### Failure Postconditions

- No folder created in database
- Error message displayed to user
- User remains on current page
- Folder tree unchanged

## 5. Main Success Scenario (Basic Flow)

1. User is viewing their folder tree in the sidebar or folder management page
2. User identifies desired location for new folder (root or specific parent folder)
3. User clicks "New Folder" button or "Add Folder" icon next to parent folder
4. System displays Create Folder modal/form with fields:
   - Folder Name (text input, required, max 100 chars)
   - Description (textarea, optional, max 500 chars)
   - Parent Folder (read-only, shows selected parent or "Root")
5. User enters folder name "IELTS Preparation"
6. User optionally enters description "Materials for IELTS exam preparation"
7. User clicks "Create" button
8. System performs client-side validation:
   - Name is not empty
   - Name length <= 100 characters
   - Description length <= 500 characters (if provided)
9. Client sends POST request to API with folder data
10. Backend receives request and validates:
    - User is authenticated (valid JWT token)
    - Name is not empty and <= 100 characters
    - Description <= 500 characters (if provided)
    - Parent folder exists (if parent_id provided)
    - Parent folder belongs to user (if parent_id provided)
11. System checks if parent_id is provided:
    - If null: creating root-level folder (depth = 1)
    - If provided: creating nested folder
12. System queries parent folder to get parent depth:

    ```sql
    SELECT id, depth, path
    FROM folders
    WHERE id = ? AND user_id = ? AND deleted_at IS NULL
    ```

13. System calculates new folder depth:
    - If root: depth = 1
    - If nested: depth = parent.depth + 1
14. System validates depth constraint:
    - new_depth <= 10 (business rule BR-FOLD-01)
15. System checks name uniqueness within same parent:

    ```sql
    SELECT COUNT(*)
    FROM folders
    WHERE user_id = ?
      AND parent_id = ? (or IS NULL for root)
      AND name = ?
      AND deleted_at IS NULL
    ```

16. No duplicate found
17. System generates new UUID for folder
18. System constructs materialized path:
    - If root: path = "|{new_id}|"
    - If nested: path = "{parent_path}{new_id}|"
19. System inserts new folder record:

    ```sql
    INSERT INTO folders (id, user_id, name, description, parent_id, path, depth, created_at, updated_at)
    VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ```

20. System returns 201 Created with new folder object:

    ```json
    {
      "id": "uuid-123",
      "name": "IELTS Preparation",
      "description": "Materials for IELTS exam preparation",
      "parentId": null,
      "path": "|uuid-123|",
      "depth": 1,
      "createdAt": "2025-01-31T10:30:00Z",
      "updatedAt": "2025-01-31T10:30:00Z"
    }
    ```

21. Client receives response
22. Client updates folder tree state:
    - Adds new folder node to tree
    - Positions folder at correct location (alphabetically or by creation time)
    - Expands parent folder if collapsed
23. System displays success message: "Folder created successfully"
24. Modal/form closes
25. User sees new folder in tree, ready to use

## 6. Alternative Flows

### 6a. Folder Name Already Exists in Parent

**Trigger:** Step 15-16 - Duplicate name detected in same parent

1. System queries for duplicate name within same parent
2. Duplicate found (COUNT > 0)
3. System returns 400 Bad Request:

   ```json
   {
     "error": "Duplicate folder name",
     "message": "Folder name 'IELTS Preparation' already exists in this location"
   }
   ```

4. Client receives error response
5. UI displays error message below name field
6. User must enter different name
7. Return to Step 5 (Main Flow)

**Note:** Uniqueness only enforced within same parent. Different parents can have folders with same name.

### 6b. Maximum Depth Exceeded

**Trigger:** Step 14 - Calculated depth > 10

1. System calculates new folder depth
2. new_depth = parent.depth + 1 = 11 (exceeds maximum)
3. System validates depth constraint
4. Validation fails (depth > 10)
5. System returns 400 Bad Request:

   ```json
   {
     "error": "Max depth exceeded",
     "message": "Maximum folder depth (10 levels) exceeded. Cannot create folder here."
   }
   ```

6. Client displays error message in modal
7. User must choose shallower parent folder
8. User clicks "Cancel" or selects different parent
9. Return to Step 2 (Main Flow)

**Business Rule:** BR-FOLD-01 - Maximum depth = 10 to maintain performance

### 6c. Invalid Folder Name - Empty

**Trigger:** Step 8 - Name is empty or only whitespace

1. User enters empty name or only spaces
2. Client-side validation detects empty name after trim
3. UI displays inline error: "Folder name is required"
4. "Create" button disabled
5. User must enter valid name
6. Return to Step 5 (Main Flow)

### 6d. Invalid Folder Name - Too Long

**Trigger:** Step 8 - Name length > 100 characters

1. User enters name with 101+ characters
2. Client-side validation detects length violation
3. UI displays character counter: "101/100" (red)
4. UI displays inline error: "Folder name must be 100 characters or less"
5. "Create" button disabled
6. User must shorten name
7. Return to Step 5 (Main Flow)

### 6e. Description Too Long

**Trigger:** Step 8 - Description length > 500 characters

1. User enters description with 501+ characters
2. Client-side validation detects length violation
3. UI displays character counter: "501/500" (red)
4. UI displays inline error: "Description must be 500 characters or less"
5. "Create" button disabled
6. User must shorten description
7. Return to Step 6 (Main Flow)

### 6f. Parent Folder Not Found

**Trigger:** Step 12 - Parent folder doesn't exist or doesn't belong to user

1. User attempts to create folder under specific parent
2. System queries parent folder
3. No matching parent found (deleted, or belongs to another user)
4. System returns 404 Not Found or 403 Forbidden:

   ```json
   {
     "error": "Parent folder not found",
     "message": "Parent folder does not exist or you don't have access"
   }
   ```

5. Client displays error message
6. User must select different parent or refresh page
7. Use case ends (failure)

### 6g. User Cancels Creation

**Trigger:** Step 7 - User clicks "Cancel" button

1. User clicks "Cancel" or closes modal
2. Client discards form data
3. Modal closes
4. No API request sent
5. Folder tree unchanged
6. Use case ends (no changes)

### 6h. Network Error

**Trigger:** Step 9 - Network request fails

1. Client sends POST request
2. Network error occurs (no connection, timeout, etc.)
3. Request fails before reaching server
4. Client catches network error
5. UI displays error: "Network error. Please check your connection and try again."
6. User can retry by clicking "Create" again
7. Return to Step 7 (Main Flow)

### 6i. Database Error

**Trigger:** Step 19 - Database insert fails

1. System attempts to insert folder record
2. Database error occurs (connection lost, constraint violation, etc.)
3. System logs error with details
4. System returns 500 Internal Server Error:

   ```json
   {
     "error": "Internal server error",
     "message": "Failed to create folder. Please try again later."
   }
   ```

5. Client displays error message
6. User can retry creation
7. Use case ends (failure)

### 6j. Session Expired During Creation

**Trigger:** Step 10 - Access token expired

1. User fills form while session expires
2. User clicks "Create"
3. Backend validates token
4. Token expired (> 15 minutes old)
5. Backend returns 401 Unauthorized
6. Client axios interceptor catches 401
7. Client automatically refreshes token (UC-003)
8. Token refresh succeeds
9. Client retries create folder request with new token
10. Request succeeds
11. Continue to Step 20 (Main Flow)

## 7. Special Requirements

### 7.1 Performance

- Response time < 500ms for folder creation
- Tree update should be smooth, no full page reload
- Path calculation should be efficient (O(1) operation)
- UI should support lazy loading for large folder trees (100+ folders)

### 7.2 Validation

- **Name:**
  - Required (not empty after trim)
  - Min length: 1 character (after trim)
  - Max length: 100 characters
  - Trim leading/trailing whitespace
  - Allowed characters: alphanumeric, spaces, hyphens, underscores, parentheses
  - Unique within same parent (case-insensitive check optional)
- **Description:**
  - Optional
  - Max length: 500 characters
  - Trim leading/trailing whitespace
- **Depth:**
  - Must be <= 10
  - Calculated automatically, not user input

### 7.3 Usability

- **Inline validation:** Real-time feedback as user types
- **Character counters:** Show remaining characters for name and description
- **Keyboard shortcuts:**
  - Enter to submit (if validation passes)
  - Esc to cancel
- **Auto-focus:** Focus on name field when modal opens
- **Loading states:** Show spinner on "Create" button during submission
- **Disable double-submit:** Disable button during API request
- **Clear error messages:** Specific, actionable error messages
- **Tree expansion:** Automatically expand parent folder to show new folder

### 7.4 Accessibility

- Proper ARIA labels for form fields
- Error messages announced to screen readers
- Keyboard navigation support (Tab, Enter, Esc)
- Focus management (trap focus in modal)
- Color contrast meets WCAG AA standards

## 8. Technology and Data Variations

### 8.1 Materialized Path Pattern

Example folder structure:

```
Root
├─ IELTS Preparation (depth=1, path="|uuid-A|")
│  ├─ Listening (depth=2, path="|uuid-A|uuid-B|")
│  └─ Reading (depth=2, path="|uuid-A|uuid-C|")
└─ Japanese (depth=1, path="|uuid-D|")
   └─ N5 (depth=2, path="|uuid-D|uuid-E|")
      └─ Grammar (depth=3, path="|uuid-D|uuid-E|uuid-F|")
```

Benefits:

- Fast ancestor queries: `WHERE path LIKE '|uuid-A|%'`
- Fast depth calculation: Count "|" characters
- Efficient move operations

### 8.2 Depth Constraint Rationale

Maximum depth = 10 reasons:

- **Performance:** Deeper trees increase query complexity
- **UX:** Very deep trees are hard to navigate
- **Typical usage:** Most users don't need > 5 levels
- **Database:** Materialized path length manageable

### 8.3 Name Uniqueness Strategy

MVP: Case-sensitive uniqueness within parent

```sql
WHERE parent_id = ? AND name = 'IELTS Preparation'
```

Future: Case-insensitive uniqueness

```sql
WHERE parent_id = ? AND LOWER(name) = LOWER('IELTS Preparation')
```

### 8.4 Concurrent Creation Handling

Scenario: Two users creating folder simultaneously

- Use database unique constraint on (user_id, parent_id, name)
- If race condition occurs, second request fails with 400
- User receives clear error message to retry

## 9. Frequency of Occurrence

- Expected: 10-50 folder creations per day (MVP phase)
- Peak: 100-200 folder creations per day (post-launch)
- Per user: 1-20 folder creations during initial setup
- Ongoing: 0-5 folder creations per month per active user

## 10. Open Issues

- **Folder templates:** Pre-defined folder structures (e.g., "Language Learning Template") - future
- **Folder icons:** Custom icons or emojis for folders - future
- **Folder colors:** Color coding for visual organization - future
- **Drag & drop creation:** Create folders by dragging decks - future
- **Bulk operations:** Create multiple folders at once - future
- **Import folder structure:** From JSON/CSV - future

## 11. Related Use Cases

- [UC-008: Rename Folder](UC-008-rename-folder.md) - Update folder name/description
- [UC-009: Move Folder](UC-009-move-folder.md) - Change folder parent
- [UC-010: Copy Folder](UC-010-copy-folder.md) - Duplicate folder structure
- [UC-011: Delete Folder](UC-011-delete-folder.md) - Remove folder
- [UC-012: View Folder Statistics](UC-012-view-folder-statistics.md) - View folder metrics
- [UC-013: Create Deck](UC-013-create-deck.md) - Create deck in folder

## 12. Business Rules References

- **BR-FOLD-01:** Maximum folder depth = 10 levels
- **BR-FOLD-02:** Folder name unique within same parent
- **BR-FOLD-03:** Folder name max length 100 characters, not empty
- **BR-FOLD-04:** Materialized path pattern for hierarchy

## 13. UI Mockup Notes

### Modal Layout (Create Folder)

```
┌─────────────────────────────────────────┐
│ Create New Folder                    [X]│
├─────────────────────────────────────────┤
│                                         │
│ Parent Folder: Root                     │
│                                         │
│ Folder Name *                           │
│ ┌─────────────────────────────────┐    │
│ │ IELTS Preparation               │    │
│ └─────────────────────────────────┘    │
│ 17/100                                  │
│                                         │
│ Description                             │
│ ┌─────────────────────────────────┐    │
│ │ Materials for IELTS exam        │    │
│ │ preparation                     │    │
│ └─────────────────────────────────┘    │
│ 32/500                                  │
│                                         │
│                [Cancel]  [Create Folder]│
└─────────────────────────────────────────┘
```

### Inline Creation (Alternative)

```
Folders
├─ + IELTS Preparation ← Click to edit
│  ├─ Listening
│  └─ Reading
└─ Japanese
```

## 14. API Endpoint

```http
POST /api/folders
```

**Request Headers:**

```http
Authorization: Bearer <access-token>
Content-Type: application/json
```

**Request Body:**

```json
{
  "name": "IELTS Preparation",
  "description": "Materials for IELTS exam preparation",
  "parentId": null
}
```

**Success Response (201 Created):**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "user-uuid",
  "name": "IELTS Preparation",
  "description": "Materials for IELTS exam preparation",
  "parentId": null,
  "path": "|550e8400-e29b-41d4-a716-446655440000|",
  "depth": 1,
  "createdAt": "2025-01-31T10:30:00Z",
  "updatedAt": "2025-01-31T10:30:00Z",
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
      "message": "Folder name is required"
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

400 Bad Request - Max depth exceeded:

```json
{
  "error": "Max depth exceeded",
  "message": "Maximum folder depth (10 levels) exceeded. Cannot create folder here."
}
```

404 Not Found - Parent folder not found:

```json
{
  "error": "Parent folder not found",
  "message": "Parent folder does not exist"
}
```

403 Forbidden - Parent folder access denied:

```json
{
  "error": "Access denied",
  "message": "You don't have permission to create folders in this location"
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
  "message": "Failed to create folder. Please try again later."
}
```

## 15. Test Cases

### TC-007-001: Create Root-Level Folder Successfully

- **Given:** User is authenticated and viewing folder tree
- **When:** User creates folder with name "IELTS Preparation" at root level
- **Then:** Folder created with depth=1, path="|{id}|", parentId=null, appears in tree

### TC-007-002: Create Nested Folder Successfully

- **Given:** User has existing folder "Languages" (depth=1)
- **When:** User creates folder "Japanese" under "Languages"
- **Then:** Folder created with depth=2, correct path, parentId="Languages", appears under parent

### TC-007-003: Create Folder at Maximum Depth (10)

- **Given:** User has folder structure with 9 levels
- **When:** User creates folder at level 10
- **Then:** Folder created successfully with depth=10

### TC-007-004: Attempt to Exceed Maximum Depth

- **Given:** User has folder structure with 10 levels
- **When:** User attempts to create folder at level 11
- **Then:** 400 error with message "Maximum folder depth (10 levels) exceeded"

### TC-007-005: Duplicate Folder Name in Same Parent

- **Given:** User has folder "IELTS" at root level
- **When:** User attempts to create another folder "IELTS" at root level
- **Then:** 400 error with message "Folder name already exists in this location"

### TC-007-006: Same Folder Name in Different Parents

- **Given:** User has folder "Grammar" under "English" and "French"
- **When:** User creates folder "Grammar" under "Japanese"
- **Then:** Folder created successfully (different parents allow same names)

### TC-007-007: Empty Folder Name

- **Given:** User opens create folder form
- **When:** User leaves name field empty and clicks Create
- **Then:** Inline error "Folder name is required", button disabled

### TC-007-008: Folder Name Too Long (101 characters)

- **Given:** User enters name with 101 characters
- **When:** User tries to submit
- **Then:** Inline error "Folder name must be 100 characters or less", button disabled

### TC-007-009: Description Too Long (501 characters)

- **Given:** User enters description with 501 characters
- **When:** User tries to submit
- **Then:** Inline error "Description must be 500 characters or less", button disabled

### TC-007-010: Create Folder with Valid Description

- **Given:** User fills name and description (< 500 chars)
- **When:** User submits form
- **Then:** Folder created with description saved

### TC-007-011: Create Folder with Empty Description

- **Given:** User fills name, leaves description empty
- **When:** User submits form
- **Then:** Folder created with null description

### TC-007-012: Parent Folder Not Found

- **Given:** User has parentId in request that doesn't exist
- **When:** User submits creation request
- **Then:** 404 error with message "Parent folder does not exist"

### TC-007-013: Session Expired During Creation

- **Given:** User's access token expires after 15 minutes
- **When:** User submits create folder form
- **Then:** Token auto-refreshed, folder created successfully

### TC-007-014: Cancel Folder Creation

- **Given:** User opens create folder modal
- **When:** User clicks "Cancel" button
- **Then:** Modal closes, no folder created, tree unchanged

### TC-007-015: Keyboard Shortcut - Enter to Submit

- **Given:** User fills valid folder name
- **When:** User presses Enter key
- **Then:** Form submitted, folder created

### TC-007-016: Keyboard Shortcut - Esc to Cancel

- **Given:** User has create folder modal open
- **When:** User presses Esc key
- **Then:** Modal closes, no folder created

### TC-007-017: Trim Whitespace from Name

- **Given:** User enters name "  IELTS  " (with leading/trailing spaces)
- **When:** User submits form
- **Then:** Folder created with name "IELTS" (whitespace trimmed)

### TC-007-018: Character Counter Updates Real-Time

- **Given:** User is typing in name field
- **When:** User types each character
- **Then:** Character counter updates: "17/100", "18/100", etc.

### TC-007-019: Auto-Expand Parent After Creation

- **Given:** Parent folder is collapsed in tree
- **When:** User creates child folder under collapsed parent
- **Then:** Parent folder auto-expands to show new child folder

### TC-007-020: Concurrent Creation - Race Condition

- **Given:** Two users create folder with same name under same parent simultaneously
- **When:** Both requests reach server at same time
- **Then:** First succeeds, second gets 400 error "Folder name already exists"

## 16. Database Schema Reference

### folders table

```sql
CREATE TABLE folders (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  parent_id UUID REFERENCES folders(id) ON DELETE CASCADE,
  path TEXT NOT NULL,           -- Materialized path: "|uuid-1|uuid-2|"
  depth INTEGER NOT NULL,        -- Level in tree: 1-10
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at TIMESTAMP,          -- Soft delete

  -- Constraints
  CONSTRAINT folders_name_not_empty CHECK (LENGTH(TRIM(name)) > 0),
  CONSTRAINT folders_depth_range CHECK (depth >= 1 AND depth <= 10),
  CONSTRAINT folders_unique_name_per_parent UNIQUE (user_id, parent_id, name, deleted_at)
);

-- Indexes
CREATE INDEX idx_folders_user_id ON folders(user_id);
CREATE INDEX idx_folders_parent_id ON folders(parent_id);
CREATE INDEX idx_folders_path ON folders(path);
CREATE INDEX idx_folders_deleted_at ON folders(deleted_at);
```

## 17. Future Enhancements

- **Folder templates:** Pre-defined structures for common use cases
- **Folder sharing:** Share folders with other users (collaborative mode)
- **Folder colors:** Visual color coding for organization
- **Folder icons:** Custom icons or emoji for personalization
- **Folder notes:** Rich text notes/documentation for folders
- **Folder permissions:** Fine-grained access control
- **Folder archiving:** Archive inactive folders without deleting
- **Folder tags:** Tag-based organization in addition to hierarchy
- **Folder search:** Full-text search within folder names/descriptions
- **Folder export:** Export folder structure to JSON/CSV
