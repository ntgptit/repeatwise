# UC-005: Create Folder Hierarchy

## 1. Use Case Information

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-005 |
| **Use Case Name** | Create Folder Hierarchy |
| **Primary Actor** | Student (Learner) |
| **Secondary Actors** | None |
| **Priority** | High (P0) |
| **Complexity** | Medium |
| **Status** | MVP |

## 2. Brief Description

User creates a hierarchical folder structure to organize their flashcard decks by subject, topic, or any custom taxonomy. Folders can be nested up to 10 levels deep.

## 3. Preconditions

- User is logged in
- User has permission to create folders
- System has available storage

## 4. Postconditions

**Success**:
- New folder created in database
- Folder appears in sidebar tree view
- Folder path and depth calculated
- User can see the new folder immediately

**Failure**:
- No folder created
- Error message displayed
- User remains on current view

## 5. Main Success Scenario

### Step 1: Navigate to Folder Creation
**Actor**: User clicks "New Folder" button in sidebar or right-clicks parent folder

**System**:
- Shows folder creation dialog/modal
- Pre-selects current folder as parent (if applicable)
- Shows breadcrumb of parent path

### Step 2: Enter Folder Information
**Actor**: User enters:
- Name: "English Learning"
- Description: "All English language learning materials" (optional)
- Parent: None (root level)

**System**:
- Validates name in real-time (not empty, ≤100 chars)
- Shows character counter: "18 / 100"
- Previews path: "Home / English Learning"

### Step 3: Submit Folder Creation
**Actor**: User clicks "Create" button

**System**:
1. Validates input (server-side):
   - Name not empty and ≤100 characters
   - Name unique within parent folder
   - Parent folder exists (if not root)
   - Depth will not exceed 10
2. Generates UUID for folder
3. Calculates path:
   - If root: path = `/folder_id`
   - If child: path = `parent_path/folder_id`
4. Calculates depth:
   - If root: depth = 0
   - If child: depth = parent_depth + 1
5. Creates folder record in database
6. Creates folder_stats record (initialized to zeros)
7. Logs event: "Folder created: {name}"

### Step 4: Display New Folder
**System**:
- Inserts folder into tree view at correct position
- Expands parent folder (if collapsed)
- Highlights new folder
- Shows success toast: "Folder 'English Learning' created"
- Auto-focuses on new folder for quick action

**Actor**: User sees new folder and can immediately:
- Create sub-folders
- Create decks inside
- Rename or move folder

### Step 5: Create Nested Sub-Folders
**Actor**: User right-clicks "English Learning" folder and selects "New Folder"

**System**: Opens creation dialog with parent = "English Learning"

**Actor**: User creates:
- Name: "IELTS Preparation"
- Parent: English Learning
- Depth will be: 1

**System**:
- Validates depth: 0 + 1 = 1 ≤ 10 ✓
- Calculates path: `/folder1_id/folder2_id`
- Creates folder successfully

**Actor**: User continues nesting:
- English Learning (depth 0)
  - IELTS Preparation (depth 1)
    - Vocabulary (depth 2)
      - Academic Words (depth 3)

## 6. Alternative Flows

### A1: Duplicate Folder Name
**Trigger**: User enters name that already exists in parent (Step 2)

**Flow**:
1. System checks existing folders in same parent
2. Found duplicate: "IELTS Preparation" already exists
3. System shows warning: "A folder with this name already exists here"
4. System suggests: "Choose a different name or use the existing folder"
5. User can:
   - Change name to "IELTS Preparation 2"
   - Cancel and use existing folder

**Return to**: Step 2

---

### A2: Max Depth Exceeded
**Trigger**: Creating folder would exceed depth 10 (Step 3)

**Flow**:
1. System calculates new depth: parent_depth + 1
2. If new_depth > 10:
   - System returns error: "Cannot create folder: Maximum depth (10 levels) reached"
   - System shows current depth: "Current: Level 10 of 10"
   - System suggests: "Create folder at a higher level"
3. User must:
   - Select different parent (higher in tree)
   - Or restructure folder hierarchy

**End Use Case**

---

### A3: Invalid Folder Name
**Trigger**: User enters invalid characters (Step 2)

**Flow**:
1. System validates name against constraints:
   - No special characters: < > " ' \ / |
   - Not just whitespace
2. System shows error: "Folder name contains invalid characters"
3. System highlights invalid characters in red
4. User corrects name

**Return to**: Step 2

---

### A4: Empty Folder Name
**Trigger**: User submits without entering name (Step 2)

**Flow**:
1. System validates name is not empty
2. System shows error: "Folder name is required"
3. System focuses on name field
4. User enters name

**Return to**: Step 2

---

### A5: Parent Folder Deleted
**Trigger**: Parent folder was deleted while dialog open (Step 3)

**Flow**:
1. System tries to create folder under parent
2. Parent not found or soft-deleted
3. System shows error: "Parent folder no longer exists"
4. System suggests: "Please refresh and try again"
5. User closes dialog and refreshes view

**End Use Case**

## 7. Special Requirements

### Performance
- Folder creation completes in < 200ms
- Tree view updates immediately (optimistic UI)
- Path calculation should be fast (O(1) with trigger)

### Usability
- Show breadcrumb preview of full path
- Auto-expand parent folder after creation
- Highlight newly created folder
- Keyboard shortcut: Ctrl+N for new folder
- Drag-and-drop to move folders (Future)

### Validation
- Name: 1-100 characters, no special chars
- Depth: 0-10 levels
- Unique name within same parent
- Parent must exist and not be deleted

## 8. Business Rules

### BR-010: Folder Naming
- Name required, not empty
- Length: 1-100 characters
- Trim leading/trailing whitespace
- No special characters: < > " ' \ / |
- Case-sensitive (but warn on case-only differences)

### BR-011: Folder Hierarchy
- Max depth: 10 levels (0-indexed, so root = 0, max child = 10)
- Unlimited folders per parent
- Unlimited folders per user (practical limit ~1000)

### BR-012: Folder Path
- Materialized path format: `/id1/id2/id3`
- Auto-calculated via database trigger
- Used for descendant queries: WHERE path LIKE '/parent_id/%'

### BR-013: Folder Uniqueness
- Name must be unique within same parent folder
- Same name allowed in different parents
- Case-sensitive comparison

## 9. Data Requirements

### Input
- name: VARCHAR(100), required
- description: VARCHAR(500), optional
- parent_folder_id: UUID, nullable

### Output
- Folder object: { id, name, description, path, depth, created_at }

### Database Changes
```sql
INSERT INTO folders (id, user_id, parent_folder_id, name, description, path, depth)
VALUES (?, ?, ?, ?, ?, ?, ?);

INSERT INTO folder_stats (folder_id, user_id, total_cards_count, due_cards_count)
VALUES (?, ?, 0, 0);
```

### Trigger Execution
```sql
-- Auto-calculate path and depth
CREATE TRIGGER trg_folder_path BEFORE INSERT ON folders
FOR EACH ROW EXECUTE FUNCTION update_folder_path();
```

## 10. UI Mockup

### Folder Creation Dialog
```
┌────────────────────────────────────────┐
│  Create New Folder                  × │
├────────────────────────────────────────┤
│                                        │
│  Parent Folder                         │
│  Home > English Learning               │
│                                        │
│  Folder Name *                         │
│  [IELTS Preparation___________] 18/100 │
│                                        │
│  Description (Optional)                │
│  [Materials for IELTS exam_____]       │
│  [______________________________]       │
│                                        │
│  Preview Path:                         │
│  📁 Home > English Learning >          │
│     IELTS Preparation                  │
│                                        │
│  Depth: Level 2 of 10                  │
│                                        │
│         [Cancel]  [Create Folder]      │
└────────────────────────────────────────┘
```

### Tree View After Creation
```
📁 Home
├─ 📁 English Learning
│  ├─ 📁 IELTS Preparation ← New!
│  │  └─ (empty)
│  └─ 📁 Business English
└─ 📁 Programming
   └─ 📁 Java
```

## 11. Testing Scenarios

### Happy Path
1. Create root folder "English Learning"
2. Create sub-folder "IELTS" under "English Learning"
3. Create "Vocabulary" under "IELTS"
4. Verify depth calculations: 0, 1, 2
5. Verify paths: `/id1`, `/id1/id2`, `/id1/id2/id3`

### Edge Cases
1. Create 10 levels deep (should succeed)
2. Try to create 11th level (should fail)
3. Create folder with name "Test", then try "test" (should fail)
4. Create folder with 100-character name (should succeed)
5. Create folder with 101-character name (should fail)
6. Unicode characters in name "Học Tiếng Anh" (should succeed)

### Error Cases
1. Empty name → Error message
2. Name with special chars "<Test>" → Error
3. Duplicate name in same parent → Error
4. Max depth exceeded → Error with suggestion
5. Parent folder deleted → Error with refresh suggestion

## 12. Performance Benchmarks

| Operation | Target | Max |
|-----------|--------|-----|
| Create folder (root) | < 100ms | 200ms |
| Create folder (nested) | < 150ms | 300ms |
| Path calculation | < 10ms | 50ms |
| Tree view update | Immediate | 100ms |
| Validate name uniqueness | < 50ms | 100ms |

## 13. Related Use Cases

- **UC-006**: Rename Folder
- **UC-007**: Move Folder
- **UC-008**: Copy Folder
- **UC-009**: Delete Folder
- **UC-010**: View Folder Contents
- **UC-013**: Create Deck (in folder)

## 14. Acceptance Criteria

- [ ] User can create root-level folder
- [ ] User can create nested sub-folders (up to depth 10)
- [ ] Duplicate names in same parent prevented
- [ ] Max depth limit enforced
- [ ] Path and depth auto-calculated correctly
- [ ] Folder appears in tree view immediately
- [ ] Validation errors displayed clearly
- [ ] Character counter works
- [ ] Breadcrumb preview shows full path
- [ ] Creation completes in < 200ms (p95)

---

**Version**: 1.0
**Last Updated**: 2025-01
