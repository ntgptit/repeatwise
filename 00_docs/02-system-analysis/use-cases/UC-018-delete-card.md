# UC-018: Delete Card

## 1. Use Case Information

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-018 |
| **Use Case Name** | Delete Card |
| **Primary Actor** | Student (Learner) |
| **Secondary Actors** | None |
| **Priority** | Medium (P2) |
| **Complexity** | Low |
| **Status** | MVP |

## 2. Brief Description

User soft-deletes a flashcard from a deck, marking it as deleted without permanently removing it. Deleted cards can be restored within 30 days. This allows users to remove unwanted or duplicate cards while maintaining data safety.

## 3. Preconditions

- User is logged in
- User has at least one deck with cards
- Card belongs to user's deck
- User has permission to delete cards
- Card is not currently being reviewed

## 4. Postconditions

**Success**:
- Card marked as deleted (deleted_at = NOW())
- Card SRS state preserved (for potential restore)
- Card hidden from deck card list
- Deck statistics updated (total_cards -= 1)
- Folder statistics updated (cascade upward)
- Card excluded from future review sessions
- User notified of successful deletion

**Failure**:
- Card remains unchanged
- Error message displayed
- User remains on current view

## 5. Main Success Scenario

### Step 1: Select Card to Delete
**Actor**: User right-clicks card "obsolete" in deck "Academic Vocabulary" and selects "Delete Card"

**System**:
- Shows delete confirmation dialog
- Displays card info:
  - Front: "obsolete"
  - Back: "no longer in use; outdated"
  - Current box: 3
  - Next review: Tomorrow
- Shows warning message

### Step 2: Confirm Deletion
**Actor**: User clicks "Delete Card"

**System**:
- Shows confirmation prompt:
  - "Delete this card?"
  - Front: "obsolete"
  - "You can restore it within 30 days from Trash"
  - [Cancel] [Delete]

### Step 3: Execute Soft Delete
**Actor**: User confirms deletion

**System**:
1. Validates request:
   - Card exists and belongs to user's deck
   - Card not currently in review session
   - User has delete permission

2. Marks card as deleted:
```sql
UPDATE cards SET
    deleted_at = NOW(),
    updated_at = NOW()
WHERE id = :card_id
  AND deck_id IN (SELECT id FROM decks WHERE user_id = :user_id);
```

3. Updates deck statistics:
```sql
UPDATE deck_stats SET
    total_cards = total_cards - 1,
    updated_at = NOW()
WHERE deck_id = (SELECT deck_id FROM cards WHERE id = :card_id);
```

4. Updates folder statistics (cascade):
```sql
UPDATE folder_stats SET
    total_cards_count = total_cards_count - 1,
    updated_at = NOW()
WHERE folder_id IN (
    SELECT f.id FROM folders f
    JOIN decks d ON d.folder_id = f.id OR d.folder_id IN (
        SELECT id FROM folders WHERE path LIKE f.path || '%'
    )
    WHERE d.id = (SELECT deck_id FROM cards WHERE id = :card_id)
);
```

5. Logs event: "Card deleted: 'obsolete' from deck 'Academic Vocabulary'"

### Step 4: Display Success
**System**:
- Removes card from deck card list
- Shows success toast: "Card deleted. Restore within 30 days from Trash."
- Updates deck stats: "51 cards" → "50 cards"
- Provides undo option (5 seconds): "Undo"

**Actor**: User sees card removed and can:
- Continue working
- Click "Undo" to restore immediately
- View Trash to restore later

## 6. Alternative Flows

### A1: Undo Deletion (Immediate)
**Trigger**: User clicks "Undo" in toast (Step 4)

**Flow**:
1. User clicks "Undo" within 5 seconds
2. System restores card:
```sql
UPDATE cards SET deleted_at = NULL WHERE id = :card_id;
```
3. Updates deck and folder statistics (restore counts)
4. Card reappears in deck card list
5. Toast: "Card restored"

**Continue to**: User can use card normally

---

### A2: Delete Multiple Cards (Batch)
**Trigger**: User selects multiple cards (Step 1)

**Flow**:
1. User selects 5 cards using checkboxes
2. User clicks "Delete Selected"
3. System shows confirmation: "Delete 5 cards?"
4. User confirms
5. System soft-deletes all 5 cards in transaction:
```sql
UPDATE cards SET deleted_at = NOW()
WHERE id = ANY(:card_ids);
```
6. Updates statistics (decrement by 5)
7. Toast: "5 cards deleted"

**Continue to**: Step 4

---

### A3: Delete Card During Edit
**Trigger**: User deletes card while editing (Alternative Step 1)

**Flow**:
1. User opens card edit dialog
2. User clicks "Delete Card" button in dialog
3. Confirmation shown
4. User confirms
5. Card deleted
6. Dialog closes
7. User returned to deck view

**Continue to**: Step 4

---

### A4: Card Currently in Review Session
**Trigger**: User tries to delete card being reviewed (Step 1)

**Flow**:
1. User attempts to delete card currently in review
2. System detects active review session
3. System shows error: "Cannot delete card during active review"
4. System suggests: "Please finish or skip this card first"
5. User cancels deletion or exits review

**End Use Case**

---

### A5: Restore from Trash (Later)
**Trigger**: User navigates to Trash and selects deleted card (Alternative Step 1)

**Flow**:
1. User opens "Trash" view
2. System shows deleted cards from last 30 days
3. User finds card "obsolete"
4. User clicks "Restore"
5. System restores card (deleted_at = NULL)
6. Updates deck and folder statistics
7. Card appears in original deck
8. Toast: "Card restored"

**End Use Case**

---

### A6: Permanent Deletion (After 30 Days)
**Trigger**: Background job runs daily (Alternative scenario)

**Flow**:
1. Background job checks for expired deleted cards:
```sql
SELECT id FROM cards
WHERE deleted_at < NOW() - INTERVAL '30 days';
```
2. System permanently deletes:
   - Delete card_box_position record
   - Delete card record
3. No recovery possible
4. Storage reclaimed

**End Use Case**

## 7. Special Requirements

### Performance
- Single card delete: < 100ms
- Batch delete (10 cards): < 500ms
- Statistics update: < 50ms
- Undo operation: < 100ms

### Usability
- One-click delete with confirmation
- 5-second undo window
- Batch delete for multiple cards
- Restore option in Trash view
- 30-day retention period
- Clear visual feedback

### Data Safety
- Soft delete (not permanent)
- 30-day recovery window
- Undo option available
- SRS progress preserved for restore

## 8. Business Rules

### BR-057: Card Soft Delete
- Cards marked deleted, not permanently removed
- deleted_at timestamp records deletion time
- Deleted cards hidden from all views (except Trash)
- 30-day retention before permanent deletion

### BR-058: SRS State Preservation
- card_box_position NOT deleted (for restore)
- Review progress preserved
- Due dates preserved
- Statistics recalculated on restore

### BR-059: Deletion Restrictions
- Cannot delete card in active review session
- Cannot delete if user lacks permission
- Must belong to user's deck
- Confirmation required (prevent accidents)

### BR-060: Statistics Update
- Deck statistics decremented immediately
- Folder statistics cascaded upward
- Statistics restored on undo/restore
- Permanent deletion updates NOT needed (already decremented)

### BR-061: Batch Operations
- Can delete multiple cards at once
- All-or-nothing transaction
- Single confirmation for batch
- Single undo for batch (restore all)

## 9. Data Requirements

### Input
- card_id: UUID, required (or card_ids for batch)

### Output
- Success/failure status
- Number of cards affected

### Database Changes

**Soft Delete (Single)**:
```sql
BEGIN TRANSACTION;

-- 1. Soft delete card
UPDATE cards SET
    deleted_at = NOW(),
    updated_at = NOW()
WHERE id = :card_id
  AND deck_id IN (SELECT id FROM decks WHERE user_id = :user_id);

-- 2. Update deck statistics
UPDATE deck_stats SET
    total_cards = total_cards - 1
WHERE deck_id = (SELECT deck_id FROM cards WHERE id = :card_id);

-- 3. Update folder statistics
UPDATE folder_stats SET
    total_cards_count = total_cards_count - 1
WHERE folder_id = (SELECT folder_id FROM decks d JOIN cards c ON c.deck_id = d.id WHERE c.id = :card_id);

COMMIT;
```

**Soft Delete (Batch)**:
```sql
BEGIN TRANSACTION;

-- 1. Soft delete all cards
UPDATE cards SET
    deleted_at = NOW(),
    updated_at = NOW()
WHERE id = ANY(:card_ids)
  AND deck_id IN (SELECT id FROM decks WHERE user_id = :user_id);

-- 2. Update deck statistics
UPDATE deck_stats SET
    total_cards = total_cards - :count
WHERE deck_id = (SELECT DISTINCT deck_id FROM cards WHERE id = ANY(:card_ids));

-- 3. Update folder statistics
UPDATE folder_stats SET
    total_cards_count = total_cards_count - :count
WHERE folder_id = (SELECT folder_id FROM decks WHERE id = (SELECT deck_id FROM cards WHERE id = :card_ids[0]));

COMMIT;
```

**Restore**:
```sql
BEGIN TRANSACTION;

-- 1. Restore card
UPDATE cards SET deleted_at = NULL WHERE id = :card_id;

-- 2. Restore deck statistics
UPDATE deck_stats SET total_cards = total_cards + 1
WHERE deck_id = (SELECT deck_id FROM cards WHERE id = :card_id);

-- 3. Restore folder statistics
UPDATE folder_stats SET total_cards_count = total_cards_count + 1
WHERE folder_id = (SELECT folder_id FROM decks d JOIN cards c ON c.deck_id = d.id WHERE c.id = :card_id);

COMMIT;
```

**Permanent Delete** (Background Job):
```sql
BEGIN TRANSACTION;

-- 1. Delete card SRS state
DELETE FROM card_box_position WHERE card_id = :card_id;

-- 2. Delete card
DELETE FROM cards WHERE id = :card_id;

COMMIT;
```

## 10. UI Mockup

### Delete Confirmation Dialog
```
┌────────────────────────────────────────┐
│  Delete Card                        × │
├────────────────────────────────────────┤
│                                        │
│  Front: obsolete                       │
│  Back: no longer in use; outdated      │
│                                        │
│  Current box: 3                        │
│  Next review: Tomorrow                 │
│                                        │
│  ⚠️ You can restore this card within    │
│     30 days from Trash.                │
│                                        │
│  [Cancel]  [Delete Card]               │
└────────────────────────────────────────┘
```

### Batch Delete Confirmation
```
┌────────────────────────────────────────┐
│  Delete Multiple Cards              × │
├────────────────────────────────────────┤
│                                        │
│  Delete 5 selected cards?              │
│                                        │
│  • obsolete                            │
│  • outdated                            │
│  • deprecated                          │
│  • archaic                             │
│  • antiquated                          │
│                                        │
│  ⚠️ You can restore within 30 days      │
│                                        │
│  [Cancel]  [Delete All]                │
└────────────────────────────────────────┘
```

### Success Toast with Undo
```
┌────────────────────────────────────────┐
│  ✓ Card Deleted                        │
│                                        │
│  Restore within 30 days from Trash     │
│                                        │
│  [Undo] [Dismiss]              ⏱ 5s    │
└────────────────────────────────────────┘
```

### Trash View (Cards)
```
┌────────────────────────────────────────┐
│  🗑️ Trash > Cards                       │
├────────────────────────────────────────┤
│                                        │
│  obsolete                              │
│  no longer in use; outdated            │
│  Deck: Academic Vocabulary             │
│  Deleted: 3 days ago • 27 days left    │
│  [Restore] [Delete Permanently]        │
│  ─────────────────────────────────────│
│                                        │
│  outdated                              │
│  not current; obsolete                 │
│  Deck: Academic Vocabulary             │
│  Deleted: 1 week ago • 23 days left    │
│  [Restore] [Delete Permanently]        │
│                                        │
└────────────────────────────────────────┘
```

## 11. Testing Scenarios

### Happy Path
1. Delete card "obsolete"
2. Verify card marked deleted in database
3. Verify card hidden from deck
4. Verify deck stats decremented
5. Verify folder stats decremented

### Undo Deletion
1. Delete card
2. Click "Undo" within 5 seconds
3. Verify card restored
4. Verify statistics restored

### Batch Delete
1. Select 5 cards
2. Delete all at once
3. Verify all marked deleted
4. Verify stats decremented by 5

### Restore from Trash
1. Delete card
2. Navigate to Trash
3. Restore card
4. Verify card in original deck

### Edge Cases
1. Delete last card in deck → Deck shows "0 cards"
2. Delete card with long text → Should work
3. Undo after 6 seconds → Undo disabled
4. Restore after 29 days → Should work
5. Restore after 31 days → Not in Trash

### Error Cases
1. Delete card in active review → Error
2. Delete non-existent card → Error
3. Delete another user's card → Error

## 12. Performance Benchmarks

| Operation | Target | Max |
|-----------|--------|-----|
| Delete single card | < 50ms | 100ms |
| Delete 10 cards (batch) | < 200ms | 500ms |
| Restore card | < 50ms | 100ms |
| Permanent delete (background) | < 50ms | 100ms |

## 13. Related Use Cases

- **UC-011**: Create Deck
- **UC-014**: Delete Deck (similar soft delete)
- **UC-017**: Create/Edit Card
- **UC-019**: Review Cards with SRS
- **UC-009**: Delete Folder (similar pattern)

## 14. Acceptance Criteria

- [ ] User can delete single card with confirmation
- [ ] User can delete multiple cards (batch)
- [ ] Card soft-deleted (deleted_at set)
- [ ] SRS state preserved for restore
- [ ] Deck statistics updated correctly
- [ ] Folder statistics cascaded upward
- [ ] Card hidden from deck and reviews
- [ ] 5-second undo window works
- [ ] Can restore from Trash within 30 days
- [ ] Cannot delete card in active review
- [ ] Permanent deletion after 30 days
- [ ] Deletion completes in < 100ms

---

**Version**: 1.0
**Last Updated**: 2025-01
