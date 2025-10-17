# UC-012: Move Deck

## 1. Use Case Information

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-012 |
| **Use Case Name** | Move Deck |
| **Primary Actor** | Student (Learner) |
| **Secondary Actors** | None |
| **Priority** | Medium (P1) |
| **Complexity** | Low |
| **Status** | MVP |

## 2. Brief Description

User moves a deck from one folder to another to reorganize their learning materials. The deck and all its cards are moved to the destination folder while maintaining all review progress and statistics.

## 3. Preconditions

- User is logged in
- User has at least one deck
- User has at least one destination folder (or can move to root level)
- Deck belongs to user
- User has permission to modify deck

## 4. Postconditions

**Success**:
- Deck moved to new location
- deck.folder_id updated in database
- All cards remain unchanged
- Review progress preserved (card_box_position unchanged)
- Folder statistics recalculated for both source and destination
- Deck visible in new location
- Deck removed from old location

**Failure**:
- Deck remains in original location
- No database changes
- Error message displayed

## 5. Main Success Scenario

### Step 1: Select Deck to Move
**Actor**: User right-clicks deck "Academic Vocabulary" in folder "Vocabulary"

**System**:
- Shows context menu with options:
  - Open Deck
  - Move Deck ✓
  - Copy Deck
  - Delete Deck
  - Export Cards

### Step 2: Choose Move Option
**Actor**: User clicks "Move Deck"

**System**:
- Shows "Move Deck" dialog
- Displays current location breadcrumb: "Home > IELTS Preparation > Vocabulary"
- Shows folder tree selector
- Pre-expands current folder path
- Grays out current folder (can't move to same location)
- Shows deck info:
  - Name: "Academic Vocabulary"
  - Cards: 120 cards
  - Due cards: 20 due today

### Step 3: Select Destination Folder
**Actor**: User navigates folder tree and selects "Grammar" folder

**System**:
- Highlights selected folder "Grammar"
- Shows destination breadcrumb: "Home > IELTS Preparation > Grammar"
- Shows preview: "Move to: 📁 IELTS Preparation > Grammar > Academic Vocabulary"
- Enables "Move" button

### Step 4: Confirm Move
**Actor**: User clicks "Move" button

**System**:
1. Validates move:
   - Destination folder exists
   - Destination folder belongs to user
   - Destination ≠ current location
   - No deck with same name in destination (optional: allow or rename)
2. Updates database:
```sql
-- Move deck to new folder
UPDATE decks
SET folder_id = :new_folder_id,
    updated_at = NOW()
WHERE id = :deck_id
  AND user_id = :user_id;
```
3. Invalidates folder statistics cache:
```sql
-- Mark old and new folder stats for recalculation
UPDATE folder_stats
SET last_computed_at = NULL
WHERE folder_id IN (:old_folder_id, :new_folder_id);
```
4. Logs event: "Deck 'Academic Vocabulary' moved from 'Vocabulary' to 'Grammar'"

### Step 5: Update UI
**System**:
- Removes deck from "Vocabulary" folder view
- Adds deck to "Grammar" folder view
- Updates folder statistics:
  - Vocabulary: 2 decks • 130 cards (was 3 decks • 250 cards)
  - Grammar: 3 decks • 180 cards (was 2 decks • 60 cards)
- Shows success toast: "Deck moved to Grammar"
- Auto-navigates to destination folder (optional)
- Highlights moved deck

**Actor**: User sees deck in new location

## 6. Alternative Flows

### A1: Duplicate Name in Destination
**Trigger**: Destination folder already has deck with same name (Step 4)

**Flow**:
1. System detects duplicate: "Academic Vocabulary" exists in "Grammar"
2. System shows options:
   - **Option 1**: Cancel move
   - **Option 2**: Rename deck (append " (2)" to name)
   - **Option 3**: Merge decks (move all cards, delete source deck)
3. User selects "Rename deck"
4. System renames to "Academic Vocabulary (2)"
5. System proceeds with move

**Continue to**: Step 5

---

### A2: Move to Root Level
**Trigger**: User selects "None (Root Level)" as destination (Step 3)

**Flow**:
1. User selects "📁 Root Level" option
2. System sets new_folder_id = NULL
3. System shows preview: "Move to: 📁 Home > Academic Vocabulary"
4. System updates deck:
```sql
UPDATE decks
SET folder_id = NULL
WHERE id = :deck_id;
```
5. Deck appears at root level (outside any folder)

**Continue to**: Step 5

---

### A3: Move from Root to Folder
**Trigger**: Deck currently at root level (Step 1)

**Flow**:
1. User selects root-level deck "Quick Deck"
2. Current location: "📁 Home"
3. User selects destination folder "Vocabulary"
4. System moves deck from root into folder:
```sql
UPDATE decks
SET folder_id = :folder_id
WHERE id = :deck_id AND folder_id IS NULL;
```
5. Deck now organized in folder

**Continue to**: Step 5

---

### A4: Cancel Move
**Trigger**: User changes mind (Step 3)

**Flow**:
1. User clicks "Cancel" or presses Escape
2. System closes dialog
3. No database changes
4. Deck remains in original location

**End Use Case**

---

### A5: Move Multiple Decks (Batch Move)
**Trigger**: User selects multiple decks with Ctrl+Click (Step 1)

**Flow**:
1. User selects 3 decks: "Vocab 1", "Vocab 2", "Vocab 3"
2. User right-clicks, selects "Move Selected Decks"
3. System shows dialog: "Move 3 decks"
4. User selects destination folder
5. System moves all 3 decks:
```sql
UPDATE decks
SET folder_id = :new_folder_id
WHERE id IN (:deck_ids);
```
6. System shows: "3 decks moved to Grammar"

**Note**: Batch move is **P2** (Future enhancement)

**Continue to**: Step 5

## 7. Exception Flows

### E1: Destination Folder Deleted
**Trigger**: Destination folder deleted while dialog open (Step 4)

**Flow**:
1. User selects folder "Grammar"
2. Another user/session deletes "Grammar" folder
3. User clicks "Move"
4. System validates: Folder not found
5. System shows error: "Destination folder no longer exists"
6. System suggests: "Please refresh and try again"
7. User closes dialog
8. System refreshes folder tree

**End Use Case**

---

### E2: Network Error During Move
**Trigger**: Network failure during move operation (Step 4)

**Flow**:
1. System sends move request
2. Network timeout (no response in 5 seconds)
3. System shows error: "Move failed due to network error"
4. System suggests: "Please try again"
5. User retries
6. System successfully moves deck

**Return to**: Step 4

---

### E3: Permission Denied
**Trigger**: User lost permission to modify deck (Step 4)

**Flow**:
1. System validates user permission
2. Permission denied (deck shared, user role changed, etc.)
3. System shows error: "You don't have permission to move this deck"
4. System logs security event
5. User sees error, cannot proceed

**End Use Case**

## 8. Special Requirements

### Performance
- Move operation completes in < 200ms
- Folder statistics recalculated in < 300ms
- UI updates immediately (optimistic UI)

### Usability
- Keyboard shortcut: Ctrl+M for move
- Escape key cancels dialog
- Show breadcrumb for current and destination
- Folder tree expandable/collapsible
- Remember last used destination folder
- Drag & drop support (Future - P3)

### Data Integrity
- Move is atomic (all-or-nothing)
- Review progress preserved
- Card statistics unchanged
- No data loss during move

## 9. Business Rules

### BR-040: Move Validation
- Destination folder must exist and belong to user
- Cannot move to same folder
- Deck ownership verified before move
- Folder_id can be NULL (root level)

### BR-041: Name Conflict Handling
- If duplicate name in destination:
  - Option 1: Cancel move (default)
  - Option 2: Auto-rename with " (2)", " (3)", etc.
  - Option 3: User manually renames before move
- Name uniqueness enforced per folder

### BR-042: Statistics Update
- Old folder stats decremented (total_cards, decks_count)
- New folder stats incremented
- Recursive statistics recalculated for parent folders
- Stats update async (not blocking move operation)

### BR-043: Review Progress Preservation
- card_box_position unchanged (all review data preserved)
- due_dates unchanged
- review_logs unchanged
- User can continue reviewing without interruption

## 10. Data Requirements

### Input
- deck_id: UUID, required
- new_folder_id: UUID, nullable (NULL = root level)

### Output
- Success: { success: true, message: "Deck moved" }
- Error: { success: false, error: "Error message" }

### Database Changes
```sql
-- Move deck
UPDATE decks
SET folder_id = :new_folder_id,
    updated_at = NOW()
WHERE id = :deck_id
  AND user_id = :user_id;

-- Invalidate folder stats cache
UPDATE folder_stats
SET last_computed_at = NULL
WHERE folder_id IN (:old_folder_id, :new_folder_id)
  AND user_id = :user_id;
```

### Validation Queries
```sql
-- Check destination folder exists
SELECT id FROM folders
WHERE id = :new_folder_id
  AND user_id = :user_id
  AND deleted_at IS NULL;

-- Check duplicate name in destination
SELECT COUNT(*) FROM decks
WHERE folder_id = :new_folder_id
  AND name = :deck_name
  AND user_id = :user_id
  AND deleted_at IS NULL;
```

## 11. UI Mockup

### Move Deck Dialog
```
┌────────────────────────────────────────┐
│  Move Deck                          × │
├────────────────────────────────────────┤
│                                        │
│  Moving: Academic Vocabulary           │
│  120 cards • 20 due                    │
│                                        │
│  From: 📁 IELTS Preparation >          │
│        Vocabulary                      │
│                                        │
│  To: Select destination folder         │
│                                        │
│  ┌──────────────────────────────────┐ │
│  │ 📁 Home                          │ │
│  │   ├─ 📁 English Learning    ▼   │ │
│  │   │   ├─ 📁 IELTS Prep      ▼   │ │
│  │   │   │   ├─ 📁 Vocabulary (current)
│  │   │   │   ├─ 📁 Grammar     ✓   │ │
│  │   │   │   └─ 📁 Speaking        │ │
│  │   │   └─ 📁 Business English    │ │
│  │   └─ 📁 Programming              │ │
│  │ 📁 None (Root Level)             │ │
│  └──────────────────────────────────┘ │
│                                        │
│  Move to:                              │
│  📁 IELTS Preparation > Grammar >      │
│     📂 Academic Vocabulary             │
│                                        │
│         [Cancel]  [Move Deck]          │
└────────────────────────────────────────┘
```

## 12. Testing Scenarios

### Happy Path
1. Move deck from "Vocabulary" to "Grammar" → Should succeed
2. Verify deck in new location
3. Verify deck removed from old location
4. Verify folder stats updated
5. Verify review progress unchanged

### Alternative Flows
1. Move deck to root level → Should succeed
2. Move deck from root to folder → Should succeed
3. Cancel move → No changes
4. Batch move 3 decks → Should succeed (Future)

### Edge Cases
1. Move deck with 10,000 cards → Should complete in < 500ms
2. Move deck with duplicate name → Show rename options
3. Move deck with active review session → Review continues with new location
4. Move deck back to original folder → Should succeed

### Error Cases
1. Move to non-existent folder → Error
2. Move to same folder → Prevent (gray out)
3. Destination folder deleted during move → Error with suggestion
4. Network error → Retry option
5. Permission denied → Error message

## 13. Performance Benchmarks

| Operation | Target | Max |
|-----------|--------|-----|
| Move deck | < 100ms | 200ms |
| Update folder stats | < 200ms | 300ms |
| UI update | Immediate | 100ms |
| Validation | < 30ms | 50ms |

## 14. Related Use Cases

- **UC-005**: Create Folder Hierarchy
- **UC-007**: Move Folder (similar logic)
- **UC-013**: Create Deck
- **UC-013**: Copy Deck (similar but creates new deck)
- **UC-014**: Delete Deck

## 15. Notes & Assumptions

### Assumptions
- Move is synchronous (instant, not background job)
- Small operation (just update folder_id)
- No cards data moved (cards stay linked to deck_id)
- Statistics recalculation is async (not blocking)

### Future Enhancements
- Drag & drop to move deck
- Batch move multiple decks
- Undo move operation
- Move history log

## 16. Acceptance Criteria

- [ ] User can move deck between folders
- [ ] User can move deck to root level
- [ ] User can move deck from root to folder
- [ ] Duplicate name conflict handled
- [ ] Move completes in < 200ms
- [ ] Folder statistics updated correctly
- [ ] Review progress preserved
- [ ] All cards remain accessible
- [ ] UI updates immediately
- [ ] Validation prevents invalid moves
- [ ] Error messages clear and actionable
- [ ] Cancel works at any time
- [ ] Keyboard shortcut Ctrl+M works

---

**Version**: 1.0
**Last Updated**: 2025-01
