# UC-008: Copy Folder

## 1. Use Case Information

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-008 |
| **Use Case Name** | Copy Folder |
| **Primary Actor** | Student (Learner) |
| **Secondary Actors** | Background Job Service |
| **Priority** | Medium (P1) |
| **Complexity** | Very High |
| **Status** | MVP |

## 2. Brief Description

User creates a duplicate of an existing folder, including all sub-folders, decks, and cards. For small folders (<50 items), the copy is performed synchronously. For large folders, a background job handles the copy operation and notifies the user upon completion.

## 3. Preconditions

- User is logged in
- User owns the source folder
- User has permission to create folders
- Target location will not exceed max depth (10)
- System has sufficient storage

## 4. Postconditions

**Success**:
- New folder created with copied structure
- All sub-folders duplicated with new IDs
- All decks duplicated with new IDs
- All cards duplicated with new IDs
- All cards reset to Box 1 (fresh SRS state)
- Copy appears in tree view at target location
- User notified when complete (async jobs)

**Failure**:
- No folder created
- Error message displayed
- Partial copies rolled back (transactional)

## 5. Main Success Scenario (Synchronous - Small Folder)

### Step 1: Initiate Copy Operation
**Actor**: User right-clicks folder "IELTS Preparation" and selects "Copy to..."

**System**:
- Opens folder picker dialog
- Shows folder tree (excludes source and its descendants)
- Shows source info:
  - Name: "IELTS Preparation"
  - Contains: 3 sub-folders, 5 decks, 120 cards
  - Total items: 128
- Pre-selects current parent as default target

### Step 2: Select Target Location
**Actor**: User selects target parent: "Programming"

**System**:
- Highlights selected folder: "Programming"
- Shows preview: "Programming / IELTS Preparation (Copy)"
- Calculates new depth: Programming depth (1) + 1 = 2
- Validates max depth for all descendants
- Shows estimated time: "< 5 seconds" (small folder)

### Step 3: Configure Copy Options
**Actor**: User configures options:
- Name: "IELTS Preparation (Copy)" (editable)
- Include sub-folders: ✓ (checked)
- Include cards: ✓ (checked)
- Reset SRS progress: ✓ (checked, default)

**System**:
- Shows copy scope:
  - Folders: 4 (including root)
  - Decks: 5
  - Cards: 120
- Shows warning: "Cards will be reset to Box 1 (new learning)"

### Step 4: Execute Synchronous Copy
**Actor**: User clicks "Copy Folder"

**System**:
1. Validates inputs and constraints
2. Starts database transaction
3. Shows progress dialog: "Copying folder..."
4. Performs recursive copy:

**Recursive Copy Algorithm**:
```java
Folder copyFolder(Folder source, Folder targetParent, String newName) {
    // 1. Create new folder
    Folder copy = new Folder();
    copy.id = UUID.randomUUID();
    copy.user_id = currentUser.id;
    copy.parent_folder_id = targetParent.id;
    copy.name = newName;
    copy.description = source.description;
    copy.path = targetParent.path + '/' + copy.id;
    copy.depth = targetParent.depth + 1;
    folderRepository.save(copy);

    // 2. Copy all sub-folders (recursive)
    for (Folder subFolder : source.subFolders) {
        copyFolder(subFolder, copy, subFolder.name);
    }

    // 3. Copy all decks
    for (Deck deck : source.decks) {
        Deck deckCopy = new Deck();
        deckCopy.id = UUID.randomUUID();
        deckCopy.folder_id = copy.id;
        deckCopy.name = deck.name;
        deckCopy.description = deck.description;
        deckRepository.save(deckCopy);

        // 4. Copy all cards in deck
        for (Card card : deck.cards) {
            Card cardCopy = new Card();
            cardCopy.id = UUID.randomUUID();
            cardCopy.deck_id = deckCopy.id;
            cardCopy.front = card.front;
            cardCopy.back = card.back;
            cardRepository.save(cardCopy);

            // 5. Initialize SRS state (Box 1)
            CardBoxPosition position = new CardBoxPosition();
            position.card_id = cardCopy.id;
            position.user_id = currentUser.id;
            position.current_box = 1;
            position.interval_days = 1;
            position.due_date = LocalDate.now().plusDays(1);
            boxPositionRepository.save(position);
        }
    }

    return copy;
}
```

5. Commits transaction
6. Recalculates folder_stats for target parent chain
7. Logs event: "Folder copied: 'IELTS Preparation' to 'Programming'"

### Step 5: Display Copied Folder
**System**:
- Inserts copied folder into tree view
- Expands target parent to show new folder
- Highlights new folder
- Shows success toast: "Folder 'IELTS Preparation (Copy)' created with 120 cards"

**Actor**: User sees copied folder with all contents, ready to study independently

## 6. Alternative Flows

### A1: Large Folder - Asynchronous Copy
**Trigger**: Folder has > 50 items OR > 1000 cards (Step 4)

**Flow**:
1. System detects large folder: 500 cards
2. System shows warning: "This folder is large and will be copied in the background"
3. System shows estimated time: "~2 minutes"
4. User clicks "Copy in Background"
5. System creates background job:
```java
CopyJob job = new CopyJob();
job.id = UUID.randomUUID();
job.user_id = currentUser.id;
job.source_folder_id = source.id;
job.target_parent_id = targetParent.id;
job.new_name = newName;
job.status = JobStatus.PENDING;
job.total_items = 500;
job.processed_items = 0;
jobRepository.save(job);

// Execute async
@Async
copyFolderAsync(job);
```
6. System shows notification: "Copy started. We'll notify you when done."
7. User can continue working
8. Background job processes copy with progress updates:
   - Every 100 cards: Update job.processed_items
   - UI shows progress: "Copying... 200/500 cards"
9. On completion:
   - System shows notification: "✓ Folder 'IELTS Preparation (Copy)' ready"
   - Tree view updates with new folder
   - User clicks notification to navigate to folder
10. On error:
    - System rolls back partial copy
    - System shows notification: "✗ Copy failed. Please try again."
    - User can retry

**End Use Case**

---

### A2: Duplicate Name in Target
**Trigger**: Target parent already has folder with same name (Step 4)

**Flow**:
1. User copies "IELTS Preparation" to "Programming"
2. "Programming" already has "IELTS Preparation (Copy)"
3. System auto-renames to: "IELTS Preparation (Copy 2)"
4. If "Copy 2" exists → "IELTS Preparation (Copy 3)"
5. System proceeds with auto-renamed folder
6. Success message shows: "Folder 'IELTS Preparation (Copy 2)' created"

**Continue to**: Step 5

---

### A3: Max Depth Exceeded
**Trigger**: Copy would exceed depth 10 for descendants (Step 2)

**Flow**:
1. Source folder at depth 3 with descendants at depth 9
2. User tries to copy to parent at depth 5
3. System calculates: New max depth = 5 + 1 + (9 - 3) = 11 (EXCEEDS!)
4. System shows error: "Cannot copy: Would exceed maximum depth (11 levels)"
5. System shows details:
   - "Source folder depth: 3"
   - "Deepest sub-folder: 9"
   - "Target depth: 6"
   - "Resulting max: 11 (exceeds limit)"
6. System suggests: "Copy to a higher-level folder"

**End Use Case**

---

### A4: Copy Without Cards (Folder Structure Only)
**Trigger**: User unchecks "Include cards" option (Step 3)

**Flow**:
1. User unchecks "Include cards"
2. System updates copy scope:
   - Folders: 4 ✓
   - Decks: 5 ✓ (empty)
   - Cards: 0 (skipped)
3. User confirms copy
4. System copies folder structure and decks, but not cards
5. Result: Empty decks ready for new content
6. Success message: "Folder structure copied (no cards)"

**Continue to**: Step 5

---

### A5: Copy Without Sub-folders (Flat Copy)
**Trigger**: User unchecks "Include sub-folders" (Step 3)

**Flow**:
1. User unchecks "Include sub-folders"
2. System updates scope:
   - Folders: 1 (root only)
   - Decks: 2 (only in root folder, not in sub-folders)
   - Cards: 50 (from root decks only)
3. User confirms copy
4. System copies only the root folder and its direct decks
5. Sub-folders not copied
6. Success message: "Folder copied (without sub-folders)"

**Continue to**: Step 5

---

### A6: Insufficient Storage (Future)
**Trigger**: User exceeds storage quota (Step 4)

**Flow**:
1. System calculates required storage: 10MB (120 cards × ~80KB avg)
2. User has 2MB remaining quota
3. System shows error: "Insufficient storage. Need 10MB, have 2MB."
4. System suggests: "Delete unused decks or upgrade plan"

**End Use Case**

---

### A7: Transaction Failure - Partial Copy
**Trigger**: Database error during copy (Step 4)

**Flow**:
1. System starts copying
2. Successfully creates 3 folders, 2 decks, 50 cards
3. Database connection lost
4. System catches exception
5. System rolls back transaction
6. No partial folder created
7. System shows error: "Copy failed. Please try again."
8. System logs error with details

**End Use Case**

## 7. Special Requirements

### Performance
- Small folder (<50 items): < 5 seconds (synchronous)
- Medium folder (50-500 items): < 30 seconds (async)
- Large folder (500-5000 items): < 5 minutes (async)
- Progress updates every 100 cards (async jobs)

### Usability
- Auto-rename on duplicate (Copy, Copy 2, Copy 3...)
- Show estimated time before copy
- Progress bar for async operations
- Notification on completion
- Cancel option for async jobs (Future)

### Reliability
- Transactional copy (all-or-nothing)
- Rollback on any error
- Job retry on failure (max 3 attempts)
- Job status tracking (PENDING, RUNNING, COMPLETED, FAILED)

### Data Integrity
- All copied cards reset to Box 1
- New UUIDs for all entities
- No references to original entities
- updated_at and created_at set to NOW()

## 8. Business Rules

### BR-021: Copy Scope
- Default: Copy entire folder tree with all contents
- Options: Include/exclude sub-folders, Include/exclude cards
- Decks always copied (but may be empty if cards excluded)

### BR-022: SRS State Reset
- All copied cards reset to Box 1 (new learning)
- due_date = NOW() + 1 day
- interval_days = 1
- review_count = 0, lapse_count = 0
- No review_logs copied (fresh start)

### BR-023: Async Threshold
- Folder with > 50 total items → Async job
- Folder with > 1000 cards → Async job
- User can manually choose async (Future)

### BR-024: Auto-Naming
- Default name: "{original_name} (Copy)"
- If duplicate: "{original_name} (Copy 2)", "(Copy 3)", etc.
- User can edit name before copy

### BR-025: Depth Validation
- Same as move: Resulting max depth ≤ 10
- Validate before starting copy
- Include all descendants in calculation

## 9. Data Requirements

### Input
- source_folder_id: UUID, required
- target_parent_folder_id: UUID, nullable (NULL = root)
- new_name: VARCHAR(100), required
- include_subfolders: BOOLEAN, default TRUE
- include_cards: BOOLEAN, default TRUE

### Output
- Copied folder: { id, name, parent_folder_id, path, depth }
- Copy summary: { folders_copied, decks_copied, cards_copied, duration_ms }
- Job ID (for async operations)

### Database Changes (Synchronous)
```sql
BEGIN TRANSACTION;

-- 1. Insert new folder
INSERT INTO folders (id, user_id, parent_folder_id, name, description, path, depth)
VALUES (UUID(), :user_id, :parent_id, :name, :description, :path, :depth);

-- 2. Insert sub-folders (recursive)
-- 3. Insert decks
INSERT INTO decks (id, user_id, folder_id, name, description)
SELECT UUID(), :user_id, :new_folder_id, name, description
FROM decks WHERE folder_id = :source_folder_id;

-- 4. Insert cards
INSERT INTO cards (id, deck_id, front, back)
SELECT UUID(), :new_deck_id, front, back
FROM cards WHERE deck_id = :source_deck_id;

-- 5. Initialize SRS positions (Box 1)
INSERT INTO card_box_position (card_id, user_id, current_box, interval_days, due_date)
SELECT id, :user_id, 1, 1, CURRENT_DATE + INTERVAL '1 day'
FROM cards WHERE deck_id = :new_deck_id;

-- 6. Create folder_stats records
INSERT INTO folder_stats (folder_id, user_id, total_cards_count, due_cards_count)
VALUES (:new_folder_id, :user_id, :total_cards, :total_cards);

COMMIT;
```

### Async Job Schema
```sql
CREATE TABLE copy_jobs (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    source_folder_id UUID NOT NULL,
    target_parent_id UUID,
    new_name VARCHAR(100),
    status VARCHAR(20), -- PENDING, RUNNING, COMPLETED, FAILED
    total_items INT,
    processed_items INT DEFAULT 0,
    result_folder_id UUID, -- Set on completion
    error_message TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    started_at TIMESTAMP,
    completed_at TIMESTAMP
);
```

## 10. UI Mockup

### Copy Dialog
```
┌────────────────────────────────────────┐
│  Copy Folder                        × │
├────────────────────────────────────────┤
│                                        │
│  Copying:                              │
│  📁 English Learning > IELTS Preparation │
│  │  ├─ 📁 Vocabulary (2 decks, 50 cards)│
│  │  ├─ 📁 Grammar (1 deck, 30 cards)    │
│  │  └─ 📁 Listening (2 decks, 40 cards) │
│                                        │
│  Copy To:                              │
│  [📁 Programming ▼]                    │
│                                        │
│  New Name:                             │
│  [IELTS Preparation (Copy)___] 25/100  │
│                                        │
│  Options:                              │
│  ☑ Include sub-folders                 │
│  ☑ Include cards (120 total)           │
│  ☑ Reset SRS progress (start fresh)    │
│                                        │
│  Preview:                              │
│  📁 Programming > IELTS Preparation (Copy) │
│     ├─ 📁 Vocabulary (50 cards)        │
│     ├─ 📁 Grammar (30 cards)           │
│     └─ 📁 Listening (40 cards)         │
│                                        │
│  Estimated time: ~5 seconds            │
│                                        │
│         [Cancel]  [Copy Folder]        │
└────────────────────────────────────────┘
```

### Async Copy - Progress Notification
```
┌────────────────────────────────────────┐
│  Copying Folder...                     │
├────────────────────────────────────────┤
│  📁 IELTS Preparation (Copy)           │
│                                        │
│  ▓▓▓▓▓▓▓▓▓▓░░░░░░░░░░  200 / 500 cards │
│                                        │
│  [View Details]  [Continue Working]    │
└────────────────────────────────────────┘
```

### Completion Notification
```
┌────────────────────────────────────────┐
│  ✓ Folder Copied Successfully          │
├────────────────────────────────────────┤
│  📁 IELTS Preparation (Copy)           │
│                                        │
│  ✓ 4 folders copied                    │
│  ✓ 5 decks copied                      │
│  ✓ 120 cards copied                    │
│                                        │
│  All cards reset to Box 1              │
│                                        │
│  [View Folder]  [Dismiss]              │
└────────────────────────────────────────┘
```

## 11. Testing Scenarios

### Happy Path - Synchronous
1. Copy small folder (20 cards) to different parent
2. Verify all folders, decks, cards copied
3. Verify new UUIDs generated
4. Verify all cards in Box 1
5. Verify folder appears in tree view
6. Verify operation completes in < 5 seconds

### Happy Path - Asynchronous
1. Copy large folder (2000 cards)
2. Verify background job created
3. Verify progress updates every 100 cards
4. Verify notification on completion
5. Verify all contents copied correctly

### Edge Cases
1. Copy folder to same parent (auto-rename: "Copy", "Copy 2")
2. Copy folder to root level
3. Copy folder with max depth (depth 10) → Should work
4. Copy empty folder (no decks/cards) → Should succeed
5. Copy with "Include cards" unchecked → Empty decks
6. Copy with "Include sub-folders" unchecked → Flat copy

### Error Cases
1. Copy causing depth > 10 → Error before starting
2. Transaction failure mid-copy → Rollback, no partial folder
3. Async job failure → Retry up to 3 times
4. Insufficient storage (Future) → Error
5. Source folder deleted during copy → Error

## 12. Performance Benchmarks

| Operation | Items | Target | Max |
|-----------|-------|--------|-----|
| Copy folder | 10 cards | < 1s | 2s |
| Copy folder | 50 cards | < 5s | 10s |
| Copy folder (async) | 500 cards | < 30s | 60s |
| Copy folder (async) | 5000 cards | < 5min | 10min |

## 13. Related Use Cases

- **UC-005**: Create Folder Hierarchy
- **UC-007**: Move Folder
- **UC-009**: Delete Folder
- **UC-015**: Import Cards (bulk operations)

## 14. Acceptance Criteria

- [ ] User can copy folder with all contents
- [ ] Small folders (<50 items) copied synchronously in < 5s
- [ ] Large folders (>50 items) copied asynchronously
- [ ] All copied cards reset to Box 1
- [ ] New UUIDs generated for all entities
- [ ] Auto-rename on duplicate name (Copy, Copy 2...)
- [ ] Progress shown for async operations
- [ ] Notification on async completion
- [ ] Transactional copy (all-or-nothing)
- [ ] Rollback on error, no partial copies
- [ ] Max depth validated before copy
- [ ] Options: Include/exclude sub-folders and cards
- [ ] Copy completes successfully for 5000 cards in < 5 minutes

---

**Version**: 1.0
**Last Updated**: 2025-01
