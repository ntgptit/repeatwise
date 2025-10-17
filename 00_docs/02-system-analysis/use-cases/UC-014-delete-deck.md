# UC-014: Delete Deck

## 1. Use Case Information

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-014 |
| **Use Case Name** | Delete Deck |
| **Primary Actor** | Student (Learner) |
| **Secondary Actors** | None |
| **Priority** | High (P1) |
| **Complexity** | Medium |
| **Status** | MVP |

## 2. Brief Description

User soft-deletes a deck, which marks the deck and all its cards as deleted without permanently removing them from the database. Deleted decks can be restored within 30 days, after which they are permanently purged by a background job.

## 3. Preconditions

- User is logged in
- User has at least one deck
- Deck belongs to user
- User has permission to delete deck
- Deck is not currently in an active review session

## 4. Postconditions

**Success**:
- Deck marked as deleted (deleted_at = NOW())
- All cards in deck marked as deleted
- Deck hidden from user's deck list
- Folder statistics updated (decremented)
- Review sessions exclude deleted cards
- Deck can be restored within 30 days
- User notified of successful deletion

**Failure**:
- Deck remains unchanged
- Error message displayed
- User remains on current view

## 5. Main Success Scenario

### Step 1: Select Deck to Delete
**Actor**: User right-clicks deck "Old Vocabulary" and selects "Delete Deck"

**System**:
- Shows delete confirmation dialog
- Displays deck info:
  - Name: "Old Vocabulary"
  - Cards: 45
  - Folder: "Archived"
  - Last reviewed: 2 weeks ago
- Shows warning message

### Step 2: Confirm Deletion
**Actor**: User reads warning and clicks "Delete"

**System**:
- Shows confirmation prompt:
  - "Are you sure you want to delete 'Old Vocabulary'?"
  - "This deck has 45 cards"
  - "You can restore it within 30 days"
  - [Cancel] [Delete Deck]

### Step 3: Execute Soft Delete
**Actor**: User confirms deletion

**System**:
1. Validates request:
   - Deck exists and belongs to user
   - Deck not in active review session
   - User has delete permission

2. Marks deck as deleted:
```sql
UPDATE decks SET
    deleted_at = NOW(),
    updated_at = NOW()
WHERE id = :deck_id AND user_id = :user_id;
```

3. Marks all cards in deck as deleted:
```sql
UPDATE cards SET
    deleted_at = NOW(),
    updated_at = NOW()
WHERE deck_id = :deck_id;
```

4. Updates folder statistics:
```sql
-- Decrement card count for deck's folder and ancestors
UPDATE folder_stats SET
    total_cards_count = total_cards_count - :card_count,
    updated_at = NOW()
WHERE folder_id IN (
    SELECT id FROM folders
    WHERE id = :folder_id OR :folder_path LIKE path || '%'
);
```

5. Logs event: "Deck deleted: 'Old Vocabulary' (45 cards, can restore until [date])"

### Step 4: Display Success
**System**:
- Closes dialog
- Shows success toast: "Deck 'Old Vocabulary' deleted. Restore within 30 days from Trash."
- Removes deck from folder view
- Updates folder card count: "120 cards" → "75 cards"
- Provides undo option (5 seconds): "Undo"

**Actor**: User sees deck removed and can:
- Continue working
- Click "Undo" to restore immediately
- View Trash to restore later

## 6. Alternative Flows

### A1: Undo Deletion (Immediate)
**Trigger**: User clicks "Undo" in toast (Step 4)

**Flow**:
1. User clicks "Undo" within 5 seconds
2. System restores deck:
```sql
UPDATE decks SET deleted_at = NULL WHERE id = :deck_id;
UPDATE cards SET deleted_at = NULL WHERE deck_id = :deck_id;
```
3. Updates folder statistics (restore counts)
4. Deck reappears in folder view
5. Toast: "Deck 'Old Vocabulary' restored"

**Continue to**: User can use deck normally

---

### A2: Delete Empty Deck
**Trigger**: Deck has 0 cards (Step 1)

**Flow**:
1. User deletes deck with 0 cards
2. System shows simplified confirmation: "Delete empty deck 'Old Vocabulary'?"
3. User confirms
4. Deck deleted (no cards to mark deleted)
5. Success toast: "Empty deck deleted"

**Continue to**: Step 4

---

### A3: Deck in Active Review Session
**Trigger**: User tries to delete deck being reviewed (Step 1)

**Flow**:
1. User attempts to delete deck currently open in review session
2. System detects active session
3. System shows error: "Cannot delete deck during active review session"
4. System suggests: "Please finish or exit the review session first"
5. User cancels deletion or exits review

**End Use Case**

---

### A4: Delete Deck with Many Cards (Warning)
**Trigger**: Deck has > 500 cards (Step 2)

**Flow**:
1. User attempts to delete large deck (850 cards)
2. System shows enhanced warning:
   - "⚠️ This deck has 850 cards"
   - "All review progress will be hidden"
   - "Are you sure you want to delete?"
3. User must type deck name to confirm: [Type "Master Vocabulary" to confirm]
4. User types name correctly and confirms
5. Deletion proceeds

**Continue to**: Step 3

---

### A5: Restore from Trash (Later)
**Trigger**: User navigates to Trash and selects deleted deck (Alternative Step 1)

**Flow**:
1. User opens "Trash" view
2. System shows deleted items:
   - Decks deleted in last 30 days
   - Folders deleted in last 30 days
   - Deletion date and days remaining
3. User selects "Old Vocabulary"
4. User clicks "Restore"
5. System restores deck and cards (deleted_at = NULL)
6. Updates folder statistics
7. Deck appears in original folder
8. Toast: "Deck 'Old Vocabulary' restored"

**End Use Case**

---

### A6: Permanent Deletion (After 30 Days)
**Trigger**: Background job runs daily (Alternative scenario)

**Flow**:
1. Background job checks for expired deleted items:
```sql
SELECT id FROM decks
WHERE deleted_at < NOW() - INTERVAL '30 days';
```
2. Found: "Old Vocabulary" deleted 31 days ago
3. System permanently deletes:
   - Delete all card_box_position records
   - Delete all cards
   - Delete deck record
4. No recovery possible
5. Storage reclaimed

**End Use Case**

## 7. Special Requirements

### Performance
- Soft delete completes in < 200ms
- Folder statistics update in < 100ms
- Undo operation in < 100ms

### Usability
- Two-step confirmation (prevent accidents)
- Clear warning about card count
- 5-second undo window
- Restore option in Trash view
- 30-day retention period

### Data Safety
- Soft delete (not permanent)
- 30-day recovery window
- Undo option available
- Clear communication about restoration

## 8. Business Rules

### BR-052: Soft Delete Policy
- Decks marked deleted, not permanently removed
- deleted_at timestamp records deletion time
- Deleted decks hidden from all views (except Trash)
- 30-day retention period before permanent deletion

### BR-053: Cascade Deletion
- Deleting deck soft-deletes all cards in deck
- Card SRS state (card_box_position) NOT deleted (for potential restore)
- Deck statistics preserved (for restore)
- Related records maintained for recovery

### BR-054: Deletion Restrictions
- Cannot delete deck in active review session
- Cannot delete if user lacks permission
- Must confirm deletion (prevent accidents)
- Large decks (> 500 cards) require name confirmation

### BR-055: Restoration
- Can restore within 30 days
- Restores deck and all cards
- Restores to original folder (if folder not deleted)
- Preserves all review progress and statistics

### BR-056: Permanent Deletion
- After 30 days, permanent deletion eligible
- Background job runs daily
- Deletes: cards, card_box_position, deck
- No recovery after permanent deletion

## 9. Data Requirements

### Input
- deck_id: UUID, required

### Output
- Success/failure status
- Number of cards affected

### Database Changes

**Soft Delete**:
```sql
BEGIN TRANSACTION;

-- 1. Get card count for statistics
SELECT COUNT(*) INTO :card_count FROM cards
WHERE deck_id = :deck_id AND deleted_at IS NULL;

-- 2. Soft delete deck
UPDATE decks SET
    deleted_at = NOW(),
    updated_at = NOW()
WHERE id = :deck_id AND user_id = :user_id;

-- 3. Soft delete all cards
UPDATE cards SET
    deleted_at = NOW(),
    updated_at = NOW()
WHERE deck_id = :deck_id;

-- 4. Update folder statistics
UPDATE folder_stats SET
    total_cards_count = total_cards_count - :card_count
WHERE folder_id = (SELECT folder_id FROM decks WHERE id = :deck_id);

COMMIT;
```

**Restore**:
```sql
BEGIN TRANSACTION;

-- 1. Restore deck
UPDATE decks SET deleted_at = NULL WHERE id = :deck_id;

-- 2. Restore cards
UPDATE cards SET deleted_at = NULL WHERE deck_id = :deck_id;

-- 3. Restore folder statistics
UPDATE folder_stats SET
    total_cards_count = total_cards_count + :card_count
WHERE folder_id = (SELECT folder_id FROM decks WHERE id = :deck_id);

COMMIT;
```

**Permanent Delete** (Background Job):
```sql
BEGIN TRANSACTION;

-- 1. Delete card SRS states
DELETE FROM card_box_position
WHERE card_id IN (
    SELECT id FROM cards WHERE deck_id = :deck_id
);

-- 2. Delete cards
DELETE FROM cards WHERE deck_id = :deck_id;

-- 3. Delete deck statistics (if separate table)
DELETE FROM deck_stats WHERE deck_id = :deck_id;

-- 4. Delete deck
DELETE FROM decks WHERE id = :deck_id;

COMMIT;
```

## 10. UI Mockup

### Delete Confirmation Dialog
```
┌────────────────────────────────────────┐
│  Delete Deck                        × │
├────────────────────────────────────────┤
│                                        │
│  ⚠️ Are you sure?                       │
│                                        │
│  Deck: Old Vocabulary                  │
│  Cards: 45                             │
│  Folder: Archived                      │
│  Last reviewed: 2 weeks ago            │
│                                        │
│  ⚠️ This will delete the deck and all  │
│     its cards. You can restore within  │
│     30 days from Trash.                │
│                                        │
│  [Cancel]  [Delete Deck]               │
└────────────────────────────────────────┘
```

### Success Toast with Undo
```
┌────────────────────────────────────────┐
│  ✓ Deck "Old Vocabulary" Deleted       │
│                                        │
│  Restore within 30 days from Trash     │
│                                        │
│  [Undo] [Dismiss]              ⏱ 5s    │
└────────────────────────────────────────┘
```

### Trash View
```
┌────────────────────────────────────────┐
│  🗑️ Trash                               │
│  Items deleted in last 30 days         │
├────────────────────────────────────────┤
│                                        │
│  📂 Old Vocabulary                     │
│  45 cards • Deleted 5 days ago         │
│  From: Archived                        │
│  Days remaining: 25                    │
│  [Restore] [Delete Permanently]        │
│                                        │
│  📂 Test Deck                          │
│  10 cards • Deleted 28 days ago        │
│  From: Root                            │
│  Days remaining: 2                     │
│  [Restore] [Delete Permanently]        │
│                                        │
└────────────────────────────────────────┘
```

## 11. Testing Scenarios

### Happy Path
1. Delete deck "Old Vocabulary" (45 cards)
2. Verify deck marked deleted in database
3. Verify all 45 cards marked deleted
4. Verify deck hidden from folder view
5. Verify folder statistics decremented
6. Verify deck in Trash view

### Undo Deletion
1. Delete deck
2. Click "Undo" within 5 seconds
3. Verify deck restored
4. Verify cards restored
5. Verify folder statistics restored

### Restore from Trash
1. Delete deck
2. Wait > 5 seconds
3. Navigate to Trash
4. Restore deck
5. Verify deck appears in original folder

### Edge Cases
1. Delete empty deck (0 cards) → Should succeed
2. Delete large deck (850 cards) → Requires name confirmation
3. Undo after 5 seconds → Undo button disabled
4. Restore deck after 29 days → Should succeed
5. Restore deck after 31 days → Not in Trash (permanently deleted)

### Error Cases
1. Delete deck in active review → Error: "Cannot delete during review"
2. Delete non-existent deck → Error: "Deck not found"
3. Delete another user's deck → Error: "Permission denied"

## 12. Performance Benchmarks

| Operation | Target | Max |
|-----------|--------|-----|
| Soft delete deck | < 100ms | 200ms |
| Restore deck | < 100ms | 200ms |
| Update folder stats | < 50ms | 100ms |
| Permanent delete (background) | < 500ms | 1s |

## 13. Related Use Cases

- **UC-011**: Create Deck
- **UC-012**: Move Deck
- **UC-013**: Copy Deck
- **UC-017**: Create/Edit Card
- **UC-018**: Delete Card (similar soft delete)
- **UC-009**: Delete Folder (similar pattern)

## 14. Acceptance Criteria

- [ ] User can delete deck with confirmation
- [ ] Deck soft-deleted (deleted_at set)
- [ ] All cards in deck soft-deleted
- [ ] Folder statistics updated correctly
- [ ] Deck hidden from all views except Trash
- [ ] 5-second undo window works
- [ ] Can restore from Trash within 30 days
- [ ] Large decks require name confirmation
- [ ] Cannot delete deck in active review
- [ ] Permanent deletion after 30 days
- [ ] Review progress preserved for restore
- [ ] Deletion completes in < 200ms

---

**Version**: 1.0
**Last Updated**: 2025-01
