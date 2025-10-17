# UC-009: Delete Folder

## 1. Use Case Information

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-009 |
| **Use Case Name** | Delete Folder |
| **Primary Actor** | Student (Learner) |
| **Secondary Actors** | Background Job Service (for large folders) |
| **Priority** | High (P0) |
| **Complexity** | High |
| **Status** | MVP |

## 2. Brief Description

User deletes a folder and all its contents (sub-folders, decks, and cards). The system uses soft-delete to allow recovery within 30 days. Large folders are deleted asynchronously to prevent blocking the UI.

## 3. Preconditions

- User is logged in
- User owns the folder
- Folder exists and is not already deleted

## 4. Postconditions

**Success**:
- Folder soft-deleted (deleted_at timestamp set)
- All descendants soft-deleted (cascade)
- All decks in folder and descendants soft-deleted
- All cards in those decks soft-deleted
- Folder removed from tree view
- folder_stats updated for parent chain
- Operation logged

**Failure**:
- No changes to folder or contents
- Error message displayed
- User remains on current view

## 5. Main Success Scenario (Synchronous - Small Folder)

### Step 1: Initiate Delete Operation
**Actor**: User right-clicks folder "IELTS Preparation" and selects "Delete"

**System**:
- Shows confirmation dialog
- Displays folder information:
  - Name: "IELTS Preparation"
  - Contains: 3 sub-folders, 5 decks, 120 cards
  - Warning: "This will delete all contents"
- Shows recovery info: "Can be recovered from Trash within 30 days"

### Step 2: Confirm Deletion
**Actor**: User types folder name "IELTS Preparation" to confirm (safety measure)

**OR** (for small folders):

**Actor**: User clicks "Delete" button

**System**:
- Validates folder name matches (if required)
- Shows final warning: "Are you sure? This will delete 120 cards."

### Step 3: Execute Soft Delete
**Actor**: User clicks "Yes, Delete"

**System**:
1. Starts database transaction
2. Soft-deletes folder and all descendants:

```sql
-- Set deleted_at for folder and all descendants
UPDATE folders SET
  deleted_at = NOW(),
  updated_at = NOW()
WHERE (id = :folder_id OR path LIKE :folder_path || '%')
  AND user_id = :user_id
  AND deleted_at IS NULL;

-- Soft-delete all decks in folder and descendants
UPDATE decks SET
  deleted_at = NOW(),
  updated_at = NOW()
WHERE folder_id IN (
    SELECT id FROM folders
    WHERE (id = :folder_id OR path LIKE :folder_path || '%')
      AND user_id = :user_id
  )
  AND deleted_at IS NULL;

-- Soft-delete all cards in those decks
UPDATE cards SET
  deleted_at = NOW(),
  updated_at = NOW()
WHERE deck_id IN (
    SELECT id FROM decks
    WHERE folder_id IN (
        SELECT id FROM folders
        WHERE (id = :folder_id OR path LIKE :folder_path || '%')
          AND user_id = :user_id
    )
  )
  AND deleted_at IS NULL;
```

3. Updates folder_stats for parent chain:
   - Decrement total_cards_count by deleted cards count
   - Decrement due_cards_count by deleted due cards count
   - Propagate upward to all ancestors

4. Commits transaction
5. Logs event: "Folder deleted: 'IELTS Preparation' (120 cards)"

### Step 4: Update Tree View
**System**:
- Removes folder from tree view with animation (fade out)
- Selects parent folder or next sibling
- Shows success toast: "Folder 'IELTS Preparation' moved to Trash"
- Shows undo option: "Undo" button (5 seconds)

**Actor**: User sees folder removed and can undo if needed

### Step 5: Undo Option (Optional)
**Actor**: User clicks "Undo" within 5 seconds

**System**:
1. Restores folder:
```sql
UPDATE folders SET deleted_at = NULL, updated_at = NOW()
WHERE (id = :folder_id OR path LIKE :folder_path || '%')
  AND user_id = :user_id;

UPDATE decks SET deleted_at = NULL, updated_at = NOW()
WHERE folder_id IN (...);

UPDATE cards SET deleted_at = NULL, updated_at = NOW()
WHERE deck_id IN (...);
```
2. Recalculates folder_stats for parent chain
3. Restores folder in tree view
4. Shows toast: "Folder restored"

**End Use Case**

## 6. Alternative Flows

### A1: Large Folder - Asynchronous Delete
**Trigger**: Folder has > 1000 cards (Step 3)

**Flow**:
1. System detects large folder: 5000 cards
2. System shows warning: "This folder is large and will be deleted in the background"
3. User confirms deletion
4. System creates background job:
```java
DeleteJob job = new DeleteJob();
job.id = UUID.randomUUID();
job.user_id = currentUser.id;
job.folder_id = folderId;
job.status = JobStatus.PENDING;
job.total_items = 5000;
jobRepository.save(job);

// Execute async
@Async
deleteFolderAsync(job);
```
5. System immediately hides folder from tree view (optimistic UI)
6. System shows toast: "Deleting folder... This may take a few minutes."
7. Background job processes deletion:
   - Batch soft-delete: 1000 cards at a time
   - Update progress every batch
8. On completion:
   - System shows notification: "✓ Folder deleted successfully"
9. On error:
   - System restores folder in tree view
   - System shows notification: "✗ Delete failed. Please try again."

**End Use Case**

---

### A2: Delete Root Folder
**Trigger**: User deletes root-level folder (Step 1)

**Flow**:
1. User deletes "English Learning" (root folder, depth 0)
2. System shows all descendants in warning:
   - "English Learning" (root)
     - IELTS Preparation
       - Vocabulary
       - Grammar
     - Business English
   - Total: 10 decks, 500 cards
3. User confirms with extra safety measure (type folder name)
4. System proceeds with soft-delete (same as nested folder)
5. Folder removed from tree view

**Continue to**: Step 3

---

### A3: Empty Folder
**Trigger**: Folder has no decks or cards (Step 1)

**Flow**:
1. User deletes "New Folder" (empty, 0 cards)
2. System shows simple confirmation: "Delete 'New Folder'?"
3. No name typing required (safety not needed for empty)
4. User clicks "Delete"
5. System soft-deletes folder only:
```sql
UPDATE folders SET deleted_at = NOW()
WHERE id = :folder_id AND user_id = :user_id;
```
6. Operation completes immediately (< 50ms)

**Continue to**: Step 4

---

### A4: Delete from Trash (Permanent Delete)
**Trigger**: User deletes folder that is already soft-deleted (Step 1)

**Flow**:
1. User navigates to Trash view
2. User selects "IELTS Preparation" (deleted 5 days ago)
3. User clicks "Delete Permanently"
4. System shows warning: "This action cannot be undone. All data will be lost forever."
5. User types "DELETE" to confirm
6. User clicks "Permanently Delete"
7. System hard-deletes folder and all contents:
```sql
-- Delete cards (cascades to card_box_position, review_logs)
DELETE FROM cards
WHERE deck_id IN (
    SELECT id FROM decks WHERE folder_id IN (...)
);

-- Delete decks
DELETE FROM decks
WHERE folder_id IN (...);

-- Delete folder_stats
DELETE FROM folder_stats
WHERE folder_id IN (...);

-- Delete folders
DELETE FROM folders
WHERE (id = :folder_id OR path LIKE :folder_path || '%')
  AND user_id = :user_id;
```
8. System shows toast: "Folder permanently deleted"
9. No undo option

**End Use Case**

---

### A5: Auto-Purge After 30 Days (Future)
**Trigger**: Cron job runs daily (System-initiated)

**Flow**:
1. Cron job runs at 2:00 AM
2. System queries soft-deleted folders older than 30 days:
```sql
SELECT id FROM folders
WHERE deleted_at < NOW() - INTERVAL '30 days'
  AND deleted_at IS NOT NULL;
```
3. For each folder:
   - Hard-delete folder and all contents (same as A4)
4. System sends email: "X items permanently deleted from Trash"
5. Logs event: "Auto-purged X folders"

**End Use Case**

---

### A6: Delete Folder with Shared Decks (Future)
**Trigger**: Folder contains decks shared with other users (Step 1)

**Flow**:
1. User deletes folder with shared deck
2. System detects sharing: "IELTS Vocabulary" shared with 5 users
3. System shows warning: "This deck is shared. Others will lose access."
4. User can:
   - Cancel deletion
   - Remove sharing first, then delete
   - Delete anyway (others lose access)
5. If user deletes:
   - System removes sharing links
   - Other users see: "Deck no longer available (deleted by owner)"

**Continue to**: Step 3

---

### A7: Concurrent Deletion Conflict
**Trigger**: Two sessions delete same folder simultaneously (Step 3)

**Flow**:
1. Session A: User deletes folder
2. Session B: User deletes same folder (concurrent)
3. Session A commits first → Sets deleted_at
4. Session B commits:
```sql
UPDATE folders SET deleted_at = NOW()
WHERE id = :folder_id AND deleted_at IS NULL;
-- 0 rows affected (already deleted)
```
5. System shows message: "Folder already deleted"
6. Tree view refreshes

**End Use Case**

## 7. Special Requirements

### Performance
- Small folder (< 100 cards): < 200ms (synchronous)
- Medium folder (100-1000 cards): < 1s (synchronous)
- Large folder (> 1000 cards): Async job (< 5 min)
- Tree view update: Immediate (optimistic UI)

### Usability
- Soft-delete (recoverable within 30 days)
- Undo option (5 seconds)
- Safety confirmation for large folders (type name)
- Show descendants count before delete
- Clear warning about cascading delete

### Data Safety
- Soft-delete by default (deleted_at timestamp)
- Hard-delete only from Trash with extra confirmation
- Auto-purge after 30 days (configurable)
- Transaction ensures atomicity

### Cascade Rules
- Deleting folder → Cascade to all descendants
- Deleting folder → Cascade to all decks in folder tree
- Deleting deck → Cascade to all cards
- Deleting card → Cascade to card_box_position and review_logs (via foreign key)

## 8. Business Rules

### BR-026: Soft Delete
- All deletes set deleted_at timestamp (not hard delete)
- Soft-deleted items excluded from queries: WHERE deleted_at IS NULL
- Soft-deleted items visible in Trash view
- Recovery possible within 30 days

### BR-027: Cascade Delete
- Deleting folder cascades to all descendants (sub-folders)
- Deleting folder cascades to all decks in folder tree
- Deleting deck cascades to all cards
- Foreign key ON DELETE CASCADE for card → box_position, review_logs

### BR-028: Permanent Delete
- Hard-delete only from Trash view
- Requires typing "DELETE" or folder name
- No recovery after permanent delete
- Auto-purge after 30 days (configurable)

### BR-029: Stats Update
- Decrement folder_stats for parent chain
- Update total_cards_count and due_cards_count
- Recalculation can be async (5-min TTL acceptable)

### BR-030: Undo Window
- Undo available for 5 seconds after delete
- Restores folder and all descendants
- Recalculates stats

## 9. Data Requirements

### Input
- folder_id: UUID, required

### Output
- Delete summary: { folders_deleted, decks_deleted, cards_deleted }
- Job ID (for async deletes)

### Database Changes (Soft Delete)
```sql
BEGIN TRANSACTION;

-- 1. Soft-delete folder and descendants
UPDATE folders SET deleted_at = NOW(), updated_at = NOW()
WHERE (id = :folder_id OR path LIKE :folder_path || '%')
  AND user_id = :user_id
  AND deleted_at IS NULL;

-- 2. Soft-delete decks
UPDATE decks SET deleted_at = NOW(), updated_at = NOW()
WHERE folder_id IN (
    SELECT id FROM folders
    WHERE (id = :folder_id OR path LIKE :folder_path || '%')
      AND user_id = :user_id
  )
  AND deleted_at IS NULL;

-- 3. Soft-delete cards
UPDATE cards SET deleted_at = NOW(), updated_at = NOW()
WHERE deck_id IN (
    SELECT id FROM decks
    WHERE folder_id IN (
        SELECT id FROM folders
        WHERE (id = :folder_id OR path LIKE :folder_path || '%')
          AND user_id = :user_id
    )
  )
  AND deleted_at IS NULL;

-- 4. Update folder_stats (can be async)
UPDATE folder_stats SET
  total_cards_count = total_cards_count - :deleted_cards,
  due_cards_count = due_cards_count - :deleted_due_cards,
  updated_at = NOW()
WHERE folder_id IN (:parent_chain_folder_ids);

COMMIT;
```

### Database Changes (Hard Delete - Permanent)
```sql
-- Hard delete executes in reverse order (FK constraints)
DELETE FROM review_logs WHERE card_id IN (...);
DELETE FROM card_box_position WHERE card_id IN (...);
DELETE FROM cards WHERE deck_id IN (...);
DELETE FROM decks WHERE folder_id IN (...);
DELETE FROM folder_stats WHERE folder_id IN (...);
DELETE FROM folders WHERE id = :folder_id OR path LIKE :folder_path || '%';
```

## 10. UI Mockup

### Delete Confirmation Dialog
```
┌────────────────────────────────────────┐
│  Delete Folder?                     × │
├────────────────────────────────────────┤
│                                        │
│  ⚠️  You're about to delete:           │
│                                        │
│  📁 IELTS Preparation                  │
│  │  ├─ 📁 Vocabulary (2 decks)        │
│  │  ├─ 📁 Grammar (1 deck)            │
│  │  └─ 📁 Listening (2 decks)         │
│                                        │
│  This will delete:                     │
│  • 3 sub-folders                       │
│  • 5 decks                             │
│  • 120 cards                           │
│                                        │
│  📌 Items will be moved to Trash       │
│     and can be recovered within 30 days│
│                                        │
│  Type folder name to confirm:          │
│  [IELTS Preparation_______________]    │
│                                        │
│         [Cancel]  [Delete Folder]      │
└────────────────────────────────────────┘
```

### Success Toast with Undo
```
┌────────────────────────────────────────┐
│  ✓ Folder moved to Trash               │
│                                        │
│  📁 IELTS Preparation (120 cards)      │
│                                        │
│  [Undo]  [View Trash]                  │
│                                        │
│  (Auto-dismiss in 5 seconds)           │
└────────────────────────────────────────┘
```

### Trash View
```
┌────────────────────────────────────────┐
│  Trash                                 │
│  Items deleted in the last 30 days     │
├────────────────────────────────────────┤
│                                        │
│  📁 IELTS Preparation (120 cards)      │
│     Deleted 5 days ago                 │
│     [Restore]  [Delete Permanently]    │
│                                        │
│  📁 Old Notes (50 cards)               │
│     Deleted 25 days ago (5 days left)  │
│     [Restore]  [Delete Permanently]    │
│                                        │
│  [Empty Trash]                         │
└────────────────────────────────────────┘
```

## 11. Testing Scenarios

### Happy Path
1. Delete small folder (20 cards)
2. Verify folder and contents soft-deleted
3. Verify folder removed from tree view
4. Verify folder appears in Trash
5. Restore folder, verify contents intact

### Undo Flow
1. Delete folder
2. Click "Undo" within 5 seconds
3. Verify folder restored in tree view
4. Verify all contents restored
5. Verify folder_stats recalculated

### Permanent Delete
1. Soft-delete folder
2. Navigate to Trash
3. Permanently delete folder
4. Verify hard-delete (row removed from DB)
5. Verify no recovery possible

### Edge Cases
1. Delete empty folder → Fast, no name confirmation
2. Delete root folder → Works same as nested
3. Delete folder with 5000 cards → Async job
4. Concurrent delete → Second shows "already deleted"
5. Delete then restore → Preserves SRS progress

### Error Cases
1. Delete already-deleted folder → Error: "Already deleted"
2. Async delete failure → Restore in tree view
3. Transaction failure → Rollback, no partial delete

## 12. Performance Benchmarks

| Operation | Cards | Target | Max |
|-----------|-------|--------|-----|
| Delete folder | 10 | < 100ms | 200ms |
| Delete folder | 100 | < 500ms | 1s |
| Delete folder | 1000 | < 2s | 5s |
| Delete folder (async) | 5000 | < 2min | 5min |
| Undo delete | Any | < 200ms | 500ms |

## 13. Related Use Cases

- **UC-005**: Create Folder Hierarchy
- **UC-008**: Copy Folder
- **UC-010**: View Trash (Future)
- **UC-011**: Restore from Trash (Future)

## 14. Acceptance Criteria

- [ ] User can delete folder with confirmation
- [ ] All descendants soft-deleted (cascade)
- [ ] All decks and cards soft-deleted (cascade)
- [ ] Folder removed from tree view immediately
- [ ] Undo option available for 5 seconds
- [ ] Large folders (>1000 cards) deleted asynchronously
- [ ] Safety confirmation for folders with >100 cards
- [ ] folder_stats updated for parent chain
- [ ] Deleted items appear in Trash view
- [ ] Permanent delete from Trash requires "DELETE" confirmation
- [ ] Auto-purge after 30 days (configurable)
- [ ] Delete completes in < 1s for <100 cards

---

**Version**: 1.0
**Last Updated**: 2025-01
