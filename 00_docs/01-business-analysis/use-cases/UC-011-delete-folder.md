# UC-011: Delete Folder

## 1. Brief Description

User deletes a folder and its entire subtree (all sub-folders and decks). Deletion uses soft delete mechanism to allow recovery within a grace period of 30 days before permanent removal.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** Folder Service, Cleanup Job (background)

## 3. Preconditions

- User is authenticated with valid access token
- Target folder exists and belongs to user
- Target folder is not already soft-deleted
- User can access folder management interface

## 4. Postconditions

### Success Postconditions

- Folder and all descendants marked as soft-deleted (`deleted_at` timestamp set)
- Folder and descendants no longer visible in active folder tree
- Folder metadata preserved for recovery (30-day grace period)
- UI updates to remove folder from tree
- Success message displayed
- Undo option available (optional)

### Failure Postconditions

- No deletion performed
- Folder and descendants remain active
- Error message displayed
- User remains on current page

## 5. Main Success Scenario (Basic Flow)

1. User views folder tree
2. User identifies folder to delete (e.g., "Old Materials" folder)
3. User initiates delete action:
   - Right-clicks folder and selects "Delete"
   - Clicks three-dot menu and selects "Delete"
   - Selects folder and presses Delete key
4. System displays confirmation dialog:
   - "Delete this folder and all contents?"
   - Shows folder name and item count
   - Warning: "This will delete X subfolders and Y decks"
   - Options: [Cancel] [Delete]
5. User confirms deletion by clicking "Delete" button
6. System performs client-side validation:
   - Folder ID is valid
   - User owns the folder
7. Client sends DELETE request to API
8. Backend receives request and validates:
   - User is authenticated (valid JWT token)
   - Folder exists and belongs to user
   - Folder is not already soft-deleted
9. System starts database transaction
10. System soft-deletes folder by setting `deleted_at`:

    ```sql
    UPDATE folders
    SET deleted_at = CURRENT_TIMESTAMP
    WHERE id = ? AND user_id = ? AND deleted_at IS NULL
    ```

11. System soft-deletes all descendants (folders and decks):

    ```sql
    UPDATE folders
    SET deleted_at = CURRENT_TIMESTAMP
    WHERE path LIKE ? AND user_id = ? AND deleted_at IS NULL
    -- path LIKE '|folder-id|%'
    ```

    ```sql
    UPDATE decks
    SET deleted_at = CURRENT_TIMESTAMP
    WHERE folder_id IN (SELECT id FROM folders WHERE path LIKE ?)
      AND user_id = ? AND deleted_at IS NULL
    ```

12. System commits transaction
13. System returns 200 OK with deletion summary:

    ```json
    {
      "message": "Folder deleted successfully",
      "deletedFolders": 5,
      "deletedDecks": 15,
      "recoverableUntil": "2025-03-02T10:00:00Z"
    }
    ```

14. Client receives response
15. Client updates folder tree state:
    - Removes folder node from tree
    - Removes all descendant nodes
16. System displays success message: "Folder deleted. Recoverable for 30 days."
17. Optional: System shows "Undo" button for immediate recovery
18. Use case ends (success)

## 6. Alternative Flows

### 6a. User Cancels Deletion

**Trigger:** Step 5 - User clicks "Cancel" in confirmation dialog

1. User opens confirmation dialog
2. User reads warning and decides not to delete
3. User clicks "Cancel" button or presses Esc key
4. Dialog closes
5. No API request sent
6. Folder remains active
7. Use case ends (no changes)

### 6b. Folder Not Found

**Trigger:** Step 8 - Folder doesn't exist or was already deleted

1. User attempts to delete folder
2. System queries folder by ID
3. No matching active folder found (already deleted or never existed)
4. System returns 404 Not Found:

   ```json
   {
     "error": "Folder not found",
     "message": "The folder you're trying to delete does not exist or has already been deleted"
   }
   ```

5. UI displays error message
6. UI refreshes folder tree to sync state
7. Use case ends (failure)

### 6c. Folder Belongs to Another User

**Trigger:** Step 8 - Folder exists but doesn't belong to user

1. User attempts to delete folder
2. System validates ownership
3. Ownership check fails (folder.user_id != request.user_id)
4. System returns 403 Forbidden:

   ```json
   {
     "error": "Access denied",
     "message": "You don't have permission to delete this folder"
   }
   ```

5. UI displays error message
6. Use case ends (failure)

### 6d. Network Error

**Trigger:** Step 7 - Network request fails

1. Client sends DELETE request
2. Network error occurs (no connection, timeout)
3. Request fails before reaching server
4. Client catches network error
5. UI displays error: "Network error. Please try again."
6. User can retry deletion
7. Use case ends (failure)

### 6e. Database Transaction Failure

**Trigger:** Step 9-12 - Database error during deletion

1. System starts transaction
2. Partially marks some folders as deleted
3. Database error occurs (connection lost, deadlock)
4. System rolls back entire transaction
5. No folders marked as deleted (atomic operation)
6. System logs error with details
7. System returns 500 Internal Server Error:

   ```json
   {
     "error": "Internal server error",
     "message": "Failed to delete folder. Please try again later."
   }
   ```

8. UI displays error message
9. Folder remains active
10. Use case ends (failure)

### 6f. Session Expired During Deletion

**Trigger:** Step 8 - Access token expired

1. User confirms deletion
2. Token expired (> 15 minutes)
3. Backend returns 401 Unauthorized
4. Client axios interceptor catches 401
5. Client automatically refreshes token (UC-003)
6. Token refresh succeeds
7. Client retries delete request with new token
8. Deletion proceeds successfully
9. Continue to step 13 (Main Flow)

### 6g. Immediate Undo After Deletion

**Trigger:** Step 17 - User clicks "Undo" button

1. User deletes folder
2. Success message appears with "Undo" button
3. User clicks "Undo" within 10 seconds
4. System sends restore request:

   ```http
   POST /api/folders/{folderId}/restore
   ```

5. System clears `deleted_at` for folder and descendants
6. Folder restored to tree
7. UI displays: "Folder restored"
8. Use case ends (deletion undone)

## 7. Special Requirements

### 7.1 Performance

- Response time < 2 seconds for deleting < 100 items
- Response time < 5 seconds for deleting 100-1000 items
- Use efficient SQL UPDATE with path pattern for descendants
- Transaction should be short-lived to minimize locking

### 7.2 Data Integrity

- **Atomic operation:** All deletions in single transaction (all or nothing)
- **Soft delete:** Use `deleted_at` timestamp (not physical deletion)
- **Cascade soft delete:** Children automatically soft-deleted with parent
- **Referential integrity:** Maintain foreign key relationships during soft delete

### 7.3 Recovery and Cleanup

- **Grace period:** 30 days before permanent deletion
- **Background job:** Daily cleanup job permanently deletes folders where `deleted_at < NOW() - INTERVAL '30 days'`
- **Restore capability:** User can restore deleted folders within grace period (future UC)
- **Trash/Recycle Bin:** View and manage deleted folders (future)

### 7.4 Confirmation and Safety

- **Confirmation dialog:** Always confirm before deletion
- **Item count display:** Show number of folders and decks to be deleted
- **Warning for large deletions:** Extra confirmation for > 50 items
- **Undo option:** Provide immediate undo (10-second window)
- **No accidental deletion:** Require explicit user action

## 8. Technology and Data Variations

### 8.1 Soft Delete Pattern

```typescript
// Soft delete folder and descendants
async function softDeleteFolder(folderId: string, userId: string): Promise<void> {
  await db.transaction(async (trx) => {
    const folder = await trx('folders')
      .where({ id: folderId, user_id: userId })
      .whereNull('deleted_at')
      .first();

    if (!folder) {
      throw new NotFoundError('Folder not found');
    }

    const now = new Date();

    // Soft delete folder
    await trx('folders')
      .where({ id: folderId, user_id: userId })
      .update({ deleted_at: now });

    // Soft delete all descendants
    await trx('folders')
      .where('path', 'like', `${folder.path}%`)
      .where({ user_id: userId })
      .whereNull('deleted_at')
      .update({ deleted_at: now });

    // Soft delete all decks in folder tree
    const folderIds = await trx('folders')
      .where('path', 'like', `${folder.path}%`)
      .orWhere({ id: folderId })
      .select('id');

    await trx('decks')
      .whereIn('folder_id', folderIds.map(f => f.id))
      .whereNull('deleted_at')
      .update({ deleted_at: now });
  });
}
```

### 8.2 Cleanup Job (Background)

```typescript
// Daily cleanup job - permanently delete after 30 days
async function cleanupDeletedFolders(): Promise<void> {
  const thirtyDaysAgo = new Date(Date.now() - 30 * 24 * 60 * 60 * 1000);

  // Permanently delete folders
  await db('folders')
    .where('deleted_at', '<', thirtyDaysAgo)
    .delete();

  // Permanently delete associated decks
  await db('decks')
    .where('deleted_at', '<', thirtyDaysAgo)
    .delete();

  // Permanently delete associated cards
  await db('cards')
    .where('deleted_at', '<', thirtyDaysAgo)
    .delete();
}

// Schedule: Run daily at 2 AM
cron.schedule('0 2 * * *', cleanupDeletedFolders);
```

### 8.3 Restore Functionality (Future)

```typescript
// Restore soft-deleted folder
async function restoreFolder(folderId: string, userId: string): Promise<void> {
  await db.transaction(async (trx) => {
    // Clear deleted_at for folder and descendants
    await trx('folders')
      .where({ id: folderId, user_id: userId })
      .whereNotNull('deleted_at')
      .update({ deleted_at: null });

    const folder = await trx('folders').where({ id: folderId }).first();

    await trx('folders')
      .where('path', 'like', `${folder.path}%`)
      .where({ user_id: userId })
      .update({ deleted_at: null });

    // Restore decks
    const folderIds = await trx('folders')
      .where('path', 'like', `${folder.path}%`)
      .orWhere({ id: folderId })
      .select('id');

    await trx('decks')
      .whereIn('folder_id', folderIds.map(f => f.id))
      .update({ deleted_at: null });
  });
}
```

## 9. Frequency of Occurrence

- Expected: 3-10 folder deletions per day (MVP phase)
- Peak: 20-40 folder deletions per day (post-launch)
- Per user: 0-3 folder deletions per month (cleanup/reorganization)

## 10. Open Issues

- **Trash/Recycle Bin UI:** View and manage deleted folders - future
- **Restore functionality:** UI for restoring deleted folders - future
- **Selective restore:** Restore specific items from deleted folder - future
- **Permanent delete option:** Bypass soft delete for immediate permanent deletion - future
- **Bulk delete:** Delete multiple folders at once - future

## 11. Related Use Cases

- [UC-007: Create Folder](UC-007-create-folder.md) - Create folders
- [UC-009: Move Folder](UC-009-move-folder.md) - Move before deleting
- [UC-010: Copy Folder](UC-010-copy-folder.md) - Backup before deleting
- Restore Folder (future use case) - Restore deleted folders

## 12. Business Rules References

- **BR-DEL-01:** Soft delete using `deleted_at` timestamp
- **BR-DEL-02:** 30-day grace period before permanent deletion
- **BR-DEL-03:** Cascade soft delete to all descendants
- **BR-DEL-04:** Require confirmation for all deletions

## 13. UI Mockup Notes

### Confirmation Dialog

```
┌─────────────────────────────────────────┐
│ ⚠️  Delete Folder?                   [X]│
├─────────────────────────────────────────┤
│                                         │
│ Are you sure you want to delete:       │
│                                         │
│ "Old Materials"                         │
│                                         │
│ This folder contains:                   │
│ • 5 subfolders                          │
│ • 15 decks                              │
│ • 234 cards                             │
│                                         │
│ ⓘ You can restore this folder within   │
│   30 days from the Trash.               │
│                                         │
│                    [Cancel]  [Delete]   │
└─────────────────────────────────────────┘
```

### Success with Undo

```
┌─────────────────────────────────────────┐
│ ✓ Folder deleted successfully           │
│   Recoverable for 30 days        [Undo] │
└─────────────────────────────────────────┘
```

### Context Menu

```
Folders
├─ IELTS Preparation
└─ Old Materials ← Right-click
                    ┌──────────────────┐
                    │ 📝 Rename     F2 │
                    │ 📂 Move          │
                    │ 📋 Copy          │
                    │ 🗑️  Delete    Del│ ← Select
                    │ ──────────────── │
                    │ 📊 View Stats    │
                    └──────────────────┘
```

## 14. API Endpoint

```http
DELETE /api/folders/{folderId}
```

**Request Headers:**

```http
Authorization: Bearer <access-token>
```

**Success Response (200 OK):**

```json
{
  "message": "Folder deleted successfully",
  "deletedFolders": 5,
  "deletedDecks": 15,
  "deletedCards": 234,
  "recoverableUntil": "2025-03-02T10:00:00Z"
}
```

**Error Responses:**

404 Not Found:

```json
{
  "error": "Folder not found",
  "message": "The folder you're trying to delete does not exist or has already been deleted"
}
```

403 Forbidden:

```json
{
  "error": "Access denied",
  "message": "You don't have permission to delete this folder"
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
  "message": "Failed to delete folder. Please try again later."
}
```

## 15. Test Cases

### TC-011-001: Delete Empty Folder Successfully

- **Given:** User has empty folder "Empty Folder"
- **When:** User deletes folder and confirms
- **Then:** Folder soft-deleted, removed from tree, success message shown

### TC-011-002: Delete Folder With Children

- **Given:** User has folder "Parent" with 3 subfolders and 10 decks
- **When:** User deletes "Parent" and confirms
- **Then:** Parent and all children soft-deleted, removed from tree

### TC-011-003: Cancel Deletion

- **Given:** User initiates delete on folder
- **When:** User clicks "Cancel" in confirmation dialog
- **Then:** Dialog closes, folder remains active, no changes

### TC-011-004: Delete Already Deleted Folder

- **Given:** User has folder that was already soft-deleted
- **When:** User attempts to delete again
- **Then:** 404 error with message "Folder has already been deleted"

### TC-011-005: Folder Not Found

- **Given:** User attempts to delete non-existent folder
- **When:** Delete request is sent
- **Then:** 404 error with message "Folder does not exist"

### TC-011-006: Session Expired During Deletion

- **Given:** User's token expires
- **When:** User confirms deletion
- **Then:** Token auto-refreshed, deletion succeeds

### TC-011-007: Network Error During Deletion

- **Given:** User has no internet connection
- **When:** User confirms deletion
- **Then:** Error "Network error. Please try again."

### TC-011-008: Transaction Rollback on Error

- **Given:** Database error occurs during deletion
- **When:** Deletion is processing
- **Then:** Transaction rolls back, folder remains active

### TC-011-009: Delete Updates Descendants

- **Given:** User has folder "A" > "B" > "C"
- **When:** User deletes "A"
- **Then:** All folders A, B, C marked as deleted

### TC-011-010: Immediate Undo After Deletion

- **Given:** User deletes folder
- **When:** User clicks "Undo" within 10 seconds
- **Then:** Folder restored, appears back in tree

### TC-011-011: Permanent Cleanup After 30 Days

- **Given:** Folder soft-deleted 31 days ago
- **When:** Daily cleanup job runs
- **Then:** Folder permanently deleted from database

### TC-011-012: Confirmation Shows Item Count

- **Given:** User deletes folder with 5 subfolders and 15 decks
- **When:** Confirmation dialog appears
- **Then:** Dialog shows "5 subfolders" and "15 decks"

## 16. Database Operations

### Soft Delete Transaction

```sql
BEGIN TRANSACTION;

-- Get folder path
SELECT path FROM folders WHERE id = ? AND user_id = ?;

-- Soft delete folder
UPDATE folders
SET deleted_at = CURRENT_TIMESTAMP
WHERE id = ? AND user_id = ? AND deleted_at IS NULL;

-- Soft delete all descendants
UPDATE folders
SET deleted_at = CURRENT_TIMESTAMP
WHERE path LIKE '|folder-id|%'
  AND user_id = ?
  AND deleted_at IS NULL;

-- Soft delete all decks in folder tree
UPDATE decks
SET deleted_at = CURRENT_TIMESTAMP
WHERE folder_id IN (
  SELECT id FROM folders
  WHERE (path LIKE '|folder-id|%' OR id = ?)
    AND user_id = ?
)
AND deleted_at IS NULL;

COMMIT;
```

### Permanent Cleanup Query

```sql
-- Run daily: Delete folders older than 30 days
DELETE FROM folders
WHERE deleted_at < NOW() - INTERVAL '30 days';

-- Cascade delete handled by foreign key constraints
-- or explicit deletion of decks/cards
```

## 17. Future Enhancements

- **Trash/Recycle Bin UI:** View all deleted folders in dedicated section
- **Restore functionality:** One-click restore from trash
- **Selective restore:** Choose specific items to restore from deleted folder
- **Permanent delete option:** Bypass 30-day grace period (with extra confirmation)
- **Bulk delete:** Delete multiple folders at once
- **Delete with export:** Export before deleting (backup)
- **Scheduled deletion:** Schedule folder deletion for future date
- **Delete analytics:** Track what users delete to improve UX
- **Recovery notifications:** Email notification before permanent deletion
