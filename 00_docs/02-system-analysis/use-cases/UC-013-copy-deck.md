# UC-013: Copy Deck

## 1. Use Case Information

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-013 |
| **Use Case Name** | Copy Deck |
| **Primary Actor** | Student (Learner) |
| **Secondary Actors** | Background Job Worker (for large decks) |
| **Priority** | Medium (P2) |
| **Complexity** | Medium-High |
| **Status** | MVP |

## 2. Brief Description

User copies a deck with all its cards to create a duplicate or backup. For small decks (<100 cards), copying is synchronous. For large decks (≥100 cards), copying is performed asynchronously in the background with progress tracking.

## 3. Preconditions

- User is logged in
- User has at least one deck to copy
- User has permission to create decks in destination folder
- System has available storage
- Background job system operational (for async copies)

## 4. Postconditions

**Success**:
- New deck created with unique ID
- All cards copied to new deck
- Cards initialized with fresh SRS state (Box 1, never reviewed)
- Deck statistics calculated for copied deck
- Folder statistics updated for destination folder
- User notified of completion (async: via notification)

**Failure**:
- No deck or cards created
- Error message displayed
- User remains on current view
- Partial copies cleaned up (rollback)

## 5. Main Success Scenario

### Step 1: Initiate Deck Copy
**Actor**: User right-clicks deck "Academic Vocabulary" (50 cards) and selects "Copy Deck"

**System**:
- Shows copy deck dialog
- Pre-fills name: "Academic Vocabulary (Copy)"
- Shows destination folder selector
- Shows card count: "50 cards will be copied"
- Estimates time: "< 1 second"

### Step 2: Configure Copy Options
**Actor**: User enters:
- New name: "IELTS Vocabulary Backup"
- Destination folder: "Backups" (dropdown)
- Reset progress: ✓ Checked (default)

**System**:
- Validates name (1-100 chars, unique in destination folder)
- Shows preview: "📁 Backups > IELTS Vocabulary Backup"
- Confirms reset progress: "Cards will start in Box 1, due tomorrow"

### Step 3: Execute Copy (Synchronous - Small Deck)
**Actor**: User clicks "Copy Deck"

**System**:
1. Determines copy mode: 50 cards < 100 → Synchronous
2. Shows progress indicator: "Copying deck..."
3. Creates new deck record:
```sql
INSERT INTO decks (id, user_id, folder_id, name, description)
VALUES (
    gen_random_uuid(),
    :user_id,
    :destination_folder_id,
    'IELTS Vocabulary Backup',
    :original_description
);
```
4. Copies all cards:
```sql
INSERT INTO cards (id, deck_id, front, back, created_at, updated_at)
SELECT gen_random_uuid(), :new_deck_id, front, back, NOW(), NOW()
FROM cards
WHERE deck_id = :source_deck_id AND deleted_at IS NULL;
```
5. Initializes SRS state for all copied cards:
```sql
INSERT INTO card_box_position (card_id, user_id, current_box, interval_days, due_date)
SELECT c.id, :user_id, 1, 1, CURRENT_DATE + 1
FROM cards c
WHERE c.deck_id = :new_deck_id;
```
6. Updates folder statistics
7. Logs event: "Deck copied: 'Academic Vocabulary' → 'IELTS Vocabulary Backup'"

### Step 4: Display Success
**System**:
- Closes dialog
- Shows success toast: "Deck 'IELTS Vocabulary Backup' created with 50 cards"
- New deck appears in destination folder
- Highlights new deck

**Actor**: User sees copied deck and can:
- Open and review new deck
- Edit copied deck
- Continue working with original deck

## 6. Alternative Flows

### A1: Large Deck - Asynchronous Copy
**Trigger**: Source deck has ≥100 cards (Step 3)

**Flow**:
1. System determines: 250 cards ≥ 100 → Asynchronous
2. System shows dialog: "This deck is large (250 cards) and will be copied in the background"
3. User confirms: "Start Background Copy"
4. System creates background job:
```sql
INSERT INTO background_jobs (id, user_id, job_type, status, parameters)
VALUES (
    gen_random_uuid(),
    :user_id,
    'COPY_DECK',
    'PENDING',
    '{"source_deck_id": "...", "destination_folder_id": "...", "new_name": "..."}'
);
```
5. System shows toast: "Deck copy started. You'll be notified when complete."
6. User can continue working
7. Background worker processes job:
   - Creates deck
   - Copies cards in batches (50 at a time)
   - Updates job progress: 0% → 20% → 40% → ... → 100%
8. On completion:
   - Job status → 'COMPLETED'
   - Notification sent: "Deck 'IELTS Vocabulary Backup' copied successfully"
   - User sees new deck in destination folder

**Continue to**: Step 4 (when job completes)

---

### A2: Duplicate Name in Destination Folder
**Trigger**: Name already exists in destination folder (Step 2)

**Flow**:
1. User enters name: "IELTS Vocabulary Backup"
2. System checks destination folder for duplicates
3. Found duplicate: "IELTS Vocabulary Backup" exists
4. System shows error: "A deck with this name already exists in 'Backups' folder"
5. System suggests: "IELTS Vocabulary Backup (Copy 2)"
6. User accepts suggestion or enters new name

**Return to**: Step 2

---

### A3: Copy to Same Folder (Quick Backup)
**Trigger**: User keeps destination as current folder (Step 2)

**Flow**:
1. User selects same folder as source
2. System auto-appends " (Copy)" to name
3. Name: "Academic Vocabulary (Copy)"
4. System validates uniqueness
5. Copy proceeds normally
6. Both decks now in same folder, visually distinguished

**Continue to**: Step 3

---

### A4: Copy to Root Level
**Trigger**: User selects "None (Root Level)" as destination (Step 2)

**Flow**:
1. User selects destination: "Root Level"
2. System sets folder_id = NULL
3. System shows preview: "📁 Home > IELTS Vocabulary Backup"
4. Copy proceeds with folder_id = NULL

**Continue to**: Step 3

---

### A5: Background Job Fails
**Trigger**: Error during asynchronous copy (Alternative A1)

**Flow**:
1. Background worker encounters error:
   - Database connection lost
   - Disk full
   - Timeout
2. Job status → 'FAILED'
3. System rolls back partial changes:
   - Deletes partially created deck
   - Deletes any copied cards
4. Notification sent: "Deck copy failed: [error reason]"
5. User can retry or contact support

**End Use Case**

## 7. Special Requirements

### Performance
- Synchronous copy (< 100 cards): Complete in < 2 seconds
- Asynchronous copy: Start job in < 500ms
- Background job: Process 50 cards/second
- Progress updates every 10% or 5 seconds

### Usability
- Auto-append " (Copy)" to name
- Show card count and time estimate
- Clear distinction between sync/async modes
- Progress indicator for async copies
- Ability to view job status

### Data Integrity
- Atomic operations (transaction for sync copies)
- Rollback on failure (both sync and async)
- No duplicate card IDs
- Fresh SRS state for all copied cards

## 8. Business Rules

### BR-048: Copy Thresholds
- Small deck: < 100 cards → Synchronous copy
- Large deck: ≥ 100 cards → Asynchronous copy
- Very large deck: > 1000 cards → Warn user, confirm intent

### BR-049: Card State Reset
- All copied cards start in Box 1
- interval_days = 1, due_date = tomorrow
- last_reviewed_at = NULL, review_count = 0
- Original deck's card progress unchanged

### BR-050: Naming Convention
- Default name: "[Original Name] (Copy)"
- If duplicate: "[Original Name] (Copy 2)", "(Copy 3)", etc.
- User can override default name

### BR-051: Background Job Priority
- Deck copy jobs: Medium priority
- Timeout: 30 minutes
- Retry policy: 3 attempts with exponential backoff
- Cleanup on failure: Delete partial deck and cards

## 9. Data Requirements

### Input
- source_deck_id: UUID, required
- new_name: VARCHAR(100), required
- destination_folder_id: UUID, nullable
- reset_progress: BOOLEAN, default TRUE

### Output
- New deck object with all copied cards
- Background job ID (for async copies)

### Database Changes

**Synchronous Copy**:
```sql
BEGIN TRANSACTION;

-- 1. Create new deck
INSERT INTO decks (id, user_id, folder_id, name, description)
SELECT gen_random_uuid(), :user_id, :destination_folder_id, :new_name, description
FROM decks WHERE id = :source_deck_id;

-- 2. Copy all cards
INSERT INTO cards (id, deck_id, front, back, created_at, updated_at)
SELECT gen_random_uuid(), :new_deck_id, front, back, NOW(), NOW()
FROM cards WHERE deck_id = :source_deck_id AND deleted_at IS NULL;

-- 3. Initialize SRS state
INSERT INTO card_box_position (card_id, user_id, current_box, interval_days, due_date)
SELECT c.id, :user_id, 1, 1, CURRENT_DATE + 1
FROM cards c WHERE c.deck_id = :new_deck_id;

-- 4. Update folder stats
UPDATE folder_stats SET total_cards_count = total_cards_count + :card_count
WHERE folder_id = :destination_folder_id;

COMMIT;
```

**Asynchronous Copy**:
```sql
-- Create background job
INSERT INTO background_jobs (id, user_id, job_type, status, parameters)
VALUES (
    gen_random_uuid(),
    :user_id,
    'COPY_DECK',
    'PENDING',
    jsonb_build_object(
        'source_deck_id', :source_deck_id,
        'destination_folder_id', :destination_folder_id,
        'new_name', :new_name
    )
);
```

## 10. UI Mockup

### Copy Deck Dialog (Small Deck)
```
┌────────────────────────────────────────┐
│  Copy Deck                          × │
├────────────────────────────────────────┤
│                                        │
│  Source: Academic Vocabulary           │
│  50 cards • Current folder: Vocabulary │
│                                        │
│  New Deck Name *                       │
│  [IELTS Vocabulary Backup________] 25/100│
│                                        │
│  Destination Folder                    │
│  [Backups                         ▼]   │
│  📁 Backups                            │
│                                        │
│  Preview:                              │
│  📁 Backups > IELTS Vocabulary Backup  │
│                                        │
│  Options:                              │
│  ☑ Reset review progress (start in Box 1)│
│                                        │
│  ℹ️ 50 cards will be copied (< 1 sec)  │
│                                        │
│  [Cancel]  [Copy Deck]                 │
└────────────────────────────────────────┘
```

### Copy Deck Dialog (Large Deck)
```
┌────────────────────────────────────────┐
│  Copy Deck (Background Job)         × │
├────────────────────────────────────────┤
│                                        │
│  Source: Master Vocabulary Set         │
│  850 cards • Very large deck           │
│                                        │
│  New Deck Name *                       │
│  [Master Vocabulary Set (Copy)___] 26/100│
│                                        │
│  Destination Folder                    │
│  [Backups                         ▼]   │
│                                        │
│  ⚠️ Large deck detected                 │
│  This copy will run in the background  │
│  Estimated time: ~15 seconds           │
│  You'll be notified when complete      │
│                                        │
│  [Cancel]  [Start Background Copy]     │
└────────────────────────────────────────┘
```

### Background Job Progress
```
┌────────────────────────────────────────┐
│  🔄 Background Jobs                     │
├────────────────────────────────────────┤
│                                        │
│  Copying: Master Vocabulary Set        │
│  [████████████░░░░░░░░] 60%            │
│  510 / 850 cards copied                │
│  ~6 seconds remaining                  │
│                                        │
└────────────────────────────────────────┘
```

## 11. Testing Scenarios

### Happy Path
1. Copy small deck (50 cards) to different folder
2. Verify new deck created with correct name
3. Verify all 50 cards copied
4. Verify cards in Box 1, due tomorrow
5. Verify folder stats updated

### Large Deck (Async)
1. Copy large deck (250 cards)
2. Verify background job created
3. Monitor job progress
4. Verify notification on completion
5. Verify all cards copied correctly

### Edge Cases
1. Copy deck with 99 cards → Synchronous
2. Copy deck with 100 cards → Asynchronous
3. Copy to same folder → Name appended "(Copy)"
4. Copy empty deck (0 cards) → Should succeed
5. Copy deck with Unicode name → Should work

### Error Cases
1. Duplicate name in destination → Error
2. Destination folder deleted during copy → Error
3. Background job fails → Rollback, notification
4. Disk full during copy → Error, cleanup

## 12. Performance Benchmarks

| Operation | Target | Max |
|-----------|--------|-----|
| Copy small deck (50 cards) | < 1s | 2s |
| Start async job | < 200ms | 500ms |
| Process cards (async) | 50 cards/s | - |
| Large deck (1000 cards) | < 30s | 60s |

## 13. Related Use Cases

- **UC-011**: Create Deck
- **UC-012**: Move Deck
- **UC-014**: Delete Deck
- **UC-017**: Create/Edit Card
- **UC-008**: Copy Folder (similar async pattern)

## 14. Acceptance Criteria

- [ ] User can copy deck to same or different folder
- [ ] Small decks (< 100 cards) copied synchronously
- [ ] Large decks (≥ 100 cards) copied asynchronously
- [ ] All cards copied with fresh SRS state
- [ ] Duplicate names prevented in destination folder
- [ ] Background job progress tracked
- [ ] User notified on async completion
- [ ] Failed copies rolled back completely
- [ ] Folder statistics updated correctly
- [ ] Original deck and progress unchanged
- [ ] Default name "[Original] (Copy)" suggested
- [ ] Copy completes within time limits

---

**Version**: 1.0
**Last Updated**: 2025-01
