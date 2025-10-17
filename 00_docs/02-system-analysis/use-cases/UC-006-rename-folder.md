# UC-006: Rename Folder

## 1. Use Case Information

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-006 |
| **Use Case Name** | Rename Folder |
| **Primary Actor** | Student (Learner) |
| **Secondary Actors** | None |
| **Priority** | Medium (P1) |
| **Complexity** | Low |
| **Status** | MVP |

## 2. Brief Description

User renames an existing folder to better organize their learning materials. The system validates the new name and updates the folder while maintaining the hierarchy structure.

## 3. Preconditions

- User is logged in
- User owns the folder to be renamed
- Folder exists and is not deleted

## 4. Postconditions

**Success**:
- Folder name updated in database
- Folder appears with new name in tree view
- updated_at timestamp refreshed
- Other folder properties unchanged (path, depth, parent)

**Failure**:
- Folder name remains unchanged
- Error message displayed
- User remains on current view

## 5. Main Success Scenario

### Step 1: Access Rename Function
**Actor**: User right-clicks folder "IELTS Preparation" and selects "Rename"

**System**:
- Shows inline rename field OR rename dialog
- Pre-fills current name: "IELTS Preparation"
- Selects all text for easy replacement
- Shows character counter: "18 / 100"

### Step 2: Enter New Name
**Actor**: User types new name: "IELTS 2025"

**System**:
- Validates in real-time:
  - Not empty
  - ≤ 100 characters
  - No special characters: < > " ' \ / |
- Updates character counter: "10 / 100"
- Shows preview: "Home > English Learning > IELTS 2025"

### Step 3: Submit Rename
**Actor**: User presses Enter or clicks "Save"

**System**:
1. Validates input (server-side):
   - Name not empty and ≤ 100 characters
   - Name unique within parent folder
   - No invalid characters
2. Updates folder record:
```sql
UPDATE folders SET
  name = 'IELTS 2025',
  updated_at = NOW()
WHERE id = ? AND user_id = ?;
```
3. Logs event: "Folder renamed: 'IELTS Preparation' → 'IELTS 2025'"

### Step 4: Display Updated Folder
**System**:
- Updates folder name in tree view immediately
- Maintains folder expansion state
- Shows success toast: "Folder renamed to 'IELTS 2025'"
- Keeps focus on renamed folder

**Actor**: User sees updated name and can continue working

## 6. Alternative Flows

### A1: Duplicate Folder Name
**Trigger**: New name already exists in same parent (Step 3)

**Flow**:
1. System checks existing folders in same parent
2. Found duplicate: "Business English" already exists
3. System shows error: "A folder with this name already exists in 'English Learning'"
4. System suggests: "Choose a different name"
5. User can:
   - Change to different name: "Business English 2"
   - Cancel rename operation
   - Merge folders (Future feature)

**Return to**: Step 2

---

### A2: Invalid Characters
**Trigger**: User enters special characters (Step 2)

**Flow**:
1. User types: "IELTS <2025>"
2. System validates name contains invalid character: "<"
3. System shows error: "Folder name contains invalid characters: < >"
4. System highlights invalid characters in red
5. User corrects to: "IELTS 2025"

**Return to**: Step 2

---

### A3: Empty Name
**Trigger**: User deletes all text and submits (Step 3)

**Flow**:
1. User selects all and deletes
2. User presses Enter
3. System validates name is empty
4. System shows error: "Folder name cannot be empty"
5. System restores original name: "IELTS Preparation"
6. User enters valid name

**Return to**: Step 2

---

### A4: Name Too Long
**Trigger**: User enters > 100 characters (Step 2)

**Flow**:
1. User types very long name (101+ characters)
2. System validates length
3. System shows error: "Name too long (101/100 characters)"
4. System prevents further typing OR truncates
5. System shows character counter in red: "101 / 100"
6. User shortens name

**Return to**: Step 2

---

### A5: Cancel Rename
**Trigger**: User presses Esc or clicks "Cancel" (Step 2)

**Flow**:
1. User presses Esc key
2. System discards new name
3. System restores original name: "IELTS Preparation"
4. System closes rename field/dialog
5. No changes made to database

**End Use Case**

---

### A6: Folder Deleted While Renaming
**Trigger**: Another session deletes folder (Step 3)

**Flow**:
1. User tries to rename folder
2. Folder was soft-deleted by another session
3. System returns error: "Folder no longer exists"
4. System refreshes tree view (folder disappears)
5. System shows toast: "Folder was deleted by another session"

**End Use Case**

## 7. Special Requirements

### Performance
- Rename completes in < 100ms
- Tree view updates immediately (optimistic UI)
- No refresh needed

### Usability
- Inline editing preferred over modal dialog
- Auto-select text on rename
- Enter to save, Esc to cancel
- Show breadcrumb preview during editing
- Keyboard shortcut: F2 for rename

### Validation
- Name: 1-100 characters
- No special chars: < > " ' \ / |
- Unique within parent folder
- Trim leading/trailing whitespace

## 8. Business Rules

### BR-014: Rename Validation
- Name must be unique within same parent
- Name can duplicate names in different parents
- Case-sensitive uniqueness check
- Whitespace trimmed before validation

### BR-015: Rename Scope
- Only name field can be changed via rename
- path, depth, parent_folder_id unchanged
- Renaming folder does NOT affect descendant paths (paths use IDs, not names)
- updated_at timestamp refreshed

### BR-016: Concurrent Modification
- Last-write-wins for MVP
- Optimistic locking (Future): Use version field
- If folder deleted during rename → Error

## 9. Data Requirements

### Input
- folder_id: UUID, required
- new_name: VARCHAR(100), required

### Output
- Updated folder object: { id, name, path, depth, updated_at }

### Database Changes
```sql
UPDATE folders SET
  name = :new_name,
  updated_at = NOW()
WHERE id = :folder_id
  AND user_id = :user_id
  AND deleted_at IS NULL;
```

### Validation Query
```sql
-- Check uniqueness within parent
SELECT COUNT(*) FROM folders
WHERE parent_folder_id = :parent_id
  AND name = :new_name
  AND id != :folder_id
  AND user_id = :user_id
  AND deleted_at IS NULL;
```

## 10. UI Mockup

### Inline Rename (Preferred)
```
📁 Home
├─ 📁 English Learning
│  ├─ 📦 [IELTS 2025_______] 10/100  ← Inline edit
│  │  └─ 📁 Vocabulary
│  └─ 📁 Business English
└─ 📁 Programming
```

### Rename Dialog (Alternative)
```
┌────────────────────────────────────────┐
│  Rename Folder                      × │
├────────────────────────────────────────┤
│                                        │
│  Current Path:                         │
│  Home > English Learning > IELTS...   │
│                                        │
│  New Name *                            │
│  [IELTS 2025___________________] 10/100│
│                                        │
│  Preview Path:                         │
│  📁 Home > English Learning >          │
│     IELTS 2025                         │
│                                        │
│         [Cancel]  [Rename Folder]      │
└────────────────────────────────────────┘
```

## 11. Testing Scenarios

### Happy Path
1. Right-click folder "IELTS Preparation"
2. Select "Rename"
3. Type "IELTS 2025"
4. Press Enter
5. Verify name changed in tree view
6. Verify updated_at timestamp changed

### Edge Cases
1. Rename to same name (should succeed, update timestamp)
2. Rename with Unicode: "Học IELTS" (should succeed)
3. Rename with 100 characters (should succeed)
4. Rename with leading/trailing spaces " IELTS " (should trim to "IELTS")
5. Rename root folder (should work same as nested)

### Error Cases
1. Empty name → Error: "Name cannot be empty"
2. Duplicate name in parent → Error with suggestion
3. Name with < > characters → Error with highlight
4. 101 characters → Error: "Name too long"
5. Folder deleted → Error: "Folder no longer exists"

## 12. Performance Benchmarks

| Operation | Target | Max |
|-----------|--------|-----|
| Rename folder | < 50ms | 100ms |
| Uniqueness check | < 30ms | 50ms |
| Tree view update | Immediate | 50ms |

## 13. Related Use Cases

- **UC-005**: Create Folder Hierarchy
- **UC-007**: Move Folder
- **UC-008**: Copy Folder
- **UC-009**: Delete Folder
- **UC-017**: Rename Deck (similar flow)

## 14. Acceptance Criteria

- [ ] User can rename folder inline with F2 or right-click menu
- [ ] Duplicate names in same parent prevented
- [ ] Invalid characters rejected with clear error
- [ ] Empty name rejected
- [ ] Tree view updates immediately without refresh
- [ ] Original name restored on cancel (Esc)
- [ ] Rename completes in < 100ms (p95)
- [ ] Character counter shows during editing
- [ ] Breadcrumb preview updates in real-time
- [ ] Unicode names supported

---

**Version**: 1.0
**Last Updated**: 2025-01
