# UC-007: Move Folder

## 1. Use Case Information

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-007 |
| **Use Case Name** | Move Folder |
| **Primary Actor** | Student (Learner) |
| **Secondary Actors** | None |
| **Priority** | Medium (P1) |
| **Complexity** | High |
| **Status** | MVP |

## 2. Brief Description

User moves a folder (and all its descendants) to a new parent folder, reorganizing their knowledge hierarchy. The system validates depth constraints, prevents circular references, and updates all descendant paths accordingly.

## 3. Preconditions

- User is logged in
- User owns both source folder and target parent folder
- Both folders exist and are not deleted
- Move operation will not exceed max depth (10 levels)

## 4. Postconditions

**Success**:
- Folder parent_folder_id updated
- Folder path and depth recalculated
- All descendant paths and depths updated
- folder_stats recalculated for old and new parent chains
- Folder appears under new parent in tree view
- Operation logged

**Failure**:
- No changes to folder hierarchy
- Error message displayed
- User remains on current view

## 5. Main Success Scenario

### Step 1: Initiate Move Operation
**Actor**: User drags folder "IELTS Preparation" and drops it onto "Programming" folder (Future: drag-drop)

**OR** (MVP):

**Actor**: User right-clicks "IELTS Preparation" and selects "Move to..."

**System**:
- Opens folder picker dialog
- Shows folder tree (excludes current folder and its descendants)
- Shows breadcrumb: Current: "English Learning / IELTS Preparation"
- Pre-selects current parent: "English Learning"

### Step 2: Select Target Parent
**Actor**: User selects new parent: "Programming"

**System**:
- Highlights selected folder: "Programming"
- Shows preview: "Programming / IELTS Preparation"
- Calculates new depth: Programming depth (1) + 1 = 2
- Shows depth info: "New depth: Level 2 of 10"

### Step 3: Validate Move Operation
**System**: Performs validation checks:

1. **Circular Reference Check**:
```java
// Cannot move folder into itself or its descendants
if (targetParent.path.startsWith(sourceFolder.path)) {
    throw new CircularReferenceException();
}
```

2. **Depth Validation**:
```java
// Calculate depth impact
int depthDelta = newDepth - sourceFolder.depth;
int maxDescendantDepth = calculateMaxDescendantDepth(sourceFolder);
int resultingMaxDepth = maxDescendantDepth + depthDelta;

if (resultingMaxDepth > 10) {
    throw new MaxDepthExceededException(
        "Moving this folder would result in depth " + resultingMaxDepth
    );
}
```

3. **Name Uniqueness Check**:
```sql
-- Check if name exists in target parent
SELECT COUNT(*) FROM folders
WHERE parent_folder_id = :target_parent_id
  AND name = :folder_name
  AND id != :folder_id
  AND user_id = :user_id
  AND deleted_at IS NULL;
```

### Step 4: Confirm Move
**Actor**: User clicks "Move Folder"

**System**:
1. Starts database transaction
2. Updates folder record:
```sql
UPDATE folders SET
  parent_folder_id = :new_parent_id,
  path = :new_path,
  depth = :new_depth,
  updated_at = NOW()
WHERE id = :folder_id AND user_id = :user_id;
```

3. Updates all descendant paths and depths:
```sql
-- Update descendants using path prefix replacement
UPDATE folders SET
  path = REPLACE(path, :old_path_prefix, :new_path_prefix),
  depth = depth + :depth_delta,
  updated_at = NOW()
WHERE path LIKE :old_path_prefix || '%'
  AND user_id = :user_id
  AND deleted_at IS NULL;
```

4. Recalculates folder_stats for affected folders:
   - Old parent chain (upward from old parent to root)
   - New parent chain (upward from new parent to root)
   - Moved folder and its descendants

5. Commits transaction
6. Logs event: "Folder moved: 'IELTS Preparation' from 'English Learning' to 'Programming'"

### Step 5: Update Tree View
**System**:
- Removes folder from old parent in tree
- Inserts folder under new parent
- Expands new parent to show moved folder
- Highlights moved folder
- Shows success toast: "Folder 'IELTS Preparation' moved to 'Programming'"

**Actor**: User sees folder in new location with all sub-folders intact

### Example Before/After:

**Before**:
```
📁 English Learning (depth 0)
│  ├─ 📁 IELTS Preparation (depth 1)
│  │  └─ 📁 Vocabulary (depth 2)
│  └─ 📁 Business English (depth 1)
📁 Programming (depth 0)
   └─ 📁 Java (depth 1)
```

**After**:
```
📁 English Learning (depth 0)
│  └─ 📁 Business English (depth 1)
📁 Programming (depth 0)
   ├─ 📁 Java (depth 1)
   └─ 📁 IELTS Preparation (depth 1) ← Moved
      └─ 📁 Vocabulary (depth 2)
```

## 6. Alternative Flows

### A1: Circular Reference Detected
**Trigger**: User tries to move folder into itself or descendant (Step 3)

**Flow**:
1. User selects "Vocabulary" as target parent
2. "Vocabulary" is descendant of "IELTS Preparation"
3. System detects: targetParent.path starts with sourceFolder.path
4. System shows error: "Cannot move folder into itself or its sub-folders"
5. System highlights invalid selection in red
6. User must select different parent

**Return to**: Step 2

---

### A2: Max Depth Exceeded
**Trigger**: Move would cause descendants to exceed depth 10 (Step 3)

**Flow**:
1. Source folder "A" at depth 3 has descendants down to depth 8 (max descendant depth = 8)
2. User tries to move to parent at depth 5
3. System calculates:
   - New depth for folder A: 5 + 1 = 6
   - Depth delta: 6 - 3 = +3
   - Resulting max descendant depth: 8 + 3 = 11 (EXCEEDS 10!)
4. System shows error: "Cannot move: Would exceed maximum depth (would be 11, max is 10)"
5. System shows details:
   - "Current folder depth: 3"
   - "Deepest sub-folder: 8 levels"
   - "Target depth: 6"
   - "Resulting max: 11 (exceeds limit)"
6. System suggests: "Move to a higher-level folder or reduce nesting in sub-folders"

**End Use Case**

---

### A3: Duplicate Name in Target
**Trigger**: Target parent already has folder with same name (Step 3)

**Flow**:
1. User moves "IELTS Preparation" to "Programming"
2. "Programming" already has child named "IELTS Preparation"
3. System shows error: "A folder named 'IELTS Preparation' already exists in 'Programming'"
4. System offers options:
   - Rename during move: "IELTS Preparation (2)"
   - Cancel move
   - Merge folders (Future feature)
5. User chooses to rename: "IELTS Preparation (English)"
6. System proceeds with move using new name

**Return to**: Step 4

---

### A4: Target is Root Level
**Trigger**: User moves folder to root level (Step 2)

**Flow**:
1. User selects "None (Root Level)" as parent
2. System sets parent_folder_id = NULL
3. System calculates new path: `/folder_id`
4. System calculates new depth: 0
5. System updates folder and descendants normally
6. Move succeeds

**Continue to**: Step 4

---

### A5: Source Folder Deleted During Move
**Trigger**: Folder deleted by another session (Step 4)

**Flow**:
1. User initiates move
2. Another session soft-deletes the folder
3. System tries to update folder
4. Folder not found (deleted_at IS NOT NULL)
5. System rolls back transaction
6. System shows error: "Folder no longer exists. It may have been deleted."
7. System refreshes tree view

**End Use Case**

---

### A6: Concurrent Move Conflict
**Trigger**: Two sessions move same folder simultaneously (Step 4)

**Flow**:
1. Session A: User moves folder to Parent X
2. Session B: User moves same folder to Parent Y (concurrent)
3. Both transactions execute
4. Last-write-wins (for MVP)
5. Session A sees folder under Parent Y (unexpected)
6. System logs warning: "Concurrent modification detected"
7. Future: Use optimistic locking with version field

**End Use Case**

## 7. Special Requirements

### Performance
- Move with < 10 descendants: < 200ms
- Move with < 100 descendants: < 500ms
- Move with > 100 descendants: Background job (Future)
- Path updates batched in single SQL statement

### Usability
- Drag-and-drop move (Future, not MVP)
- Show depth preview during selection
- Prevent selecting invalid targets (grayed out)
- Breadcrumb shows current and target paths
- Confirmation for moves affecting many items

### Validation
- Cannot move to self or descendants (circular ref)
- Max depth enforcement with clear error
- Name uniqueness in target parent
- Transaction ensures atomicity

## 8. Business Rules

### BR-017: Move Validation
- Cannot move folder into itself
- Cannot move folder into any of its descendants
- Resulting depth must not exceed 10 for any descendant
- Name must be unique in target parent

### BR-018: Path Recalculation
- All descendant paths updated atomically
- Path format: Concatenate parent.path + '/' + folder.id
- Root folders: path = '/folder_id'
- Descendant update uses string replacement:
  - Old prefix: '/parent1_id/parent2_id/folder_id'
  - New prefix: '/new_parent_id/folder_id'

### BR-019: Depth Recalculation
- Depth delta = new_depth - old_depth
- All descendants: depth = depth + depth_delta
- Root folders: depth = 0

### BR-020: Stats Recalculation
- Old parent chain: Decrement counts (folder + descendants)
- New parent chain: Increment counts (folder + descendants)
- Recalculation can be async (5-min TTL acceptable)

## 9. Data Requirements

### Input
- folder_id: UUID, required
- new_parent_folder_id: UUID, nullable (NULL = root)

### Output
- Updated folder: { id, name, parent_folder_id, path, depth, updated_at }
- List of affected descendants: [{ id, path, depth, updated_at }]

### Database Changes
```sql
BEGIN TRANSACTION;

-- Update source folder
UPDATE folders SET
  parent_folder_id = :new_parent_id,
  path = :new_path,
  depth = :new_depth,
  updated_at = NOW()
WHERE id = :folder_id AND user_id = :user_id;

-- Update all descendants
UPDATE folders SET
  path = REPLACE(path, :old_path_prefix, :new_path_prefix),
  depth = depth + :depth_delta,
  updated_at = NOW()
WHERE path LIKE :old_path_prefix || '%'
  AND user_id = :user_id
  AND deleted_at IS NULL;

-- Recalculate stats (can be async)
-- See UC-011 for folder_stats calculation

COMMIT;
```

### Validation Queries
```sql
-- 1. Check circular reference
SELECT COUNT(*) FROM folders
WHERE id = :target_parent_id
  AND path LIKE :source_folder_path || '%';
-- If count > 0 → Circular reference!

-- 2. Check max descendant depth
SELECT MAX(depth) FROM folders
WHERE path LIKE :source_folder_path || '%'
  AND user_id = :user_id;

-- 3. Check name uniqueness
SELECT COUNT(*) FROM folders
WHERE parent_folder_id = :target_parent_id
  AND name = :folder_name
  AND id != :folder_id
  AND deleted_at IS NULL;
```

## 10. UI Mockup

### Move Dialog
```
┌────────────────────────────────────────┐
│  Move Folder                        × │
├────────────────────────────────────────┤
│                                        │
│  Moving:                               │
│  📁 English Learning > IELTS Preparation │
│                                        │
│  Select New Location:                  │
│  ┌──────────────────────────────────┐ │
│  │ 📁 Home (Root)                   │ │
│  │ ├─ 📁 English Learning (current) │ │
│  │ │  └─ 📂 Business English        │ │
│  │ └─ 📁 Programming            ✓   │ │
│  │    └─ 📁 Java                    │ │
│  └──────────────────────────────────┘ │
│                                        │
│  Preview:                              │
│  📁 Programming > IELTS Preparation    │
│                                        │
│  New Depth: Level 2 of 10              │
│  Sub-folders: 3                        │
│  Total items affected: 15              │
│                                        │
│         [Cancel]  [Move Folder]        │
└────────────────────────────────────────┘
```

### Tree View After Move
```
📁 English Learning
│  └─ 📁 Business English
📁 Programming ← Expanded
   ├─ 📁 Java
   └─ 📁 IELTS Preparation ← Moved, highlighted
      ├─ 📁 Vocabulary
      ├─ 📁 Grammar
      └─ 📁 Listening
```

## 11. Testing Scenarios

### Happy Path
1. Move folder with 0 descendants (leaf folder)
2. Move folder with 5 descendants
3. Move folder to root level
4. Move folder between siblings
5. Verify all paths and depths updated correctly

### Edge Cases
1. Move folder with max depth descendants (depth 10) to root → Should succeed
2. Move root folder to become nested → Should succeed
3. Move with 100 descendants → All updated in single transaction
4. Move to same parent (no-op) → Should succeed, update timestamp
5. Move folder with duplicate name → Rename or error

### Error Cases
1. Move into self → Error: Circular reference
2. Move into descendant → Error: Circular reference
3. Move causing depth 11 → Error: Max depth exceeded
4. Duplicate name in target → Error or rename
5. Concurrent move → Last-write-wins (MVP)
6. Source deleted during move → Error, rollback

## 12. Performance Benchmarks

| Operation | Target | Max |
|-----------|--------|-----|
| Move folder (0 descendants) | < 100ms | 200ms |
| Move folder (10 descendants) | < 200ms | 300ms |
| Move folder (100 descendants) | < 500ms | 1s |
| Path calculation | < 10ms | 50ms |
| Circular ref check | < 30ms | 100ms |

## 13. Related Use Cases

- **UC-005**: Create Folder Hierarchy
- **UC-006**: Rename Folder
- **UC-008**: Copy Folder (similar complexity)
- **UC-009**: Delete Folder
- **UC-011**: View Folder Statistics

## 14. Acceptance Criteria

- [ ] User can move folder to different parent
- [ ] User can move folder to root level
- [ ] Circular reference prevented with clear error
- [ ] Max depth constraint enforced
- [ ] All descendant paths updated correctly
- [ ] All descendant depths updated correctly
- [ ] Duplicate name handled (rename or error)
- [ ] Tree view updates immediately
- [ ] Operation completes in < 500ms for <100 descendants
- [ ] Transaction ensures atomicity (all or nothing)
- [ ] folder_stats recalculated for affected folders
- [ ] Move logged for audit trail

---

**Version**: 1.0
**Last Updated**: 2025-01
