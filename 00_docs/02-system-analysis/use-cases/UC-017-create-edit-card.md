# UC-017: Create/Edit Card

## 1. Use Case Information

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-017 |
| **Use Case Name** | Create/Edit Card |
| **Primary Actor** | Student (Learner) |
| **Secondary Actors** | None |
| **Priority** | High (P0) |
| **Complexity** | Low |
| **Status** | MVP |

## 2. Brief Description

User creates a single flashcard manually by entering front and back text. The card is added to a specific deck and initialized with SRS state in Box 1.

## 3. Preconditions

- User is logged in
- User has at least one deck
- User has permission to create cards
- System has available storage

## 4. Postconditions

**Success**:
- New card created in database
- Card initialized in Box 1 (due tomorrow)
- card_box_position record created
- Card appears in deck card list
- Deck stats updated (total_cards_count += 1)
- User can immediately create another card or start review

**Failure**:
- No card created
- Error message displayed
- User remains on card creation form

## 5. Main Success Scenario

### Step 1: Navigate to Card Creation
**Actor**: User opens deck "Academic Vocabulary" and clicks "Add Card" button

**System**:
- Shows card creation form/dialog
- Displays deck name: "Academic Vocabulary"
- Shows breadcrumb: "IELTS Preparation > Vocabulary > Academic Vocabulary"
- Auto-focuses on Front field

### Step 2: Enter Card Content
**Actor**: User enters:
- Front: "ubiquitous"
- Back: "existing or being everywhere, especially at the same time; omnipresent"

**System**:
- Validates input in real-time:
  - Front not empty, ≤5000 chars
  - Back not empty, ≤5000 chars
- Shows character counter:
  - Front: "10 / 5000"
  - Back: "74 / 5000"
- Shows preview (optional):
  - Front side displayed as card
  - Flip to see back

### Step 3: Submit Card Creation
**Actor**: User clicks "Create" button (or presses Ctrl+Enter)

**System**:
1. Validates input (server-side):
   - Front not empty and ≤5000 characters
   - Back not empty and ≤5000 characters
   - Deck exists and belongs to user
   - Check for duplicate (optional warning, not blocking)
2. Generates UUID for card
3. Creates card record:
```sql
INSERT INTO cards (id, deck_id, front, back, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    :deck_id,
    'ubiquitous',
    'existing or being everywhere, especially at the same time; omnipresent',
    NOW(),
    NOW()
);
```
4. Initializes SRS state in Box 1:
```sql
INSERT INTO card_box_position (card_id, user_id, current_box, interval_days, due_date, last_reviewed_at)
VALUES (
    :card_id,
    :user_id,
    1,                                -- Start in Box 1
    1,                                -- 1-day interval
    CURRENT_DATE + INTERVAL '1 day',  -- Due tomorrow
    NULL                              -- Never reviewed
);
```
5. Updates deck stats:
```sql
UPDATE deck_stats SET
    total_cards = total_cards + 1,
    updated_at = NOW()
WHERE deck_id = :deck_id;
```
6. Updates folder_stats (cascade upward):
```sql
-- Increment for deck's folder and all ancestors
UPDATE folder_stats SET
    total_cards_count = total_cards_count + 1,
    updated_at = NOW()
WHERE folder_id IN (
    SELECT id FROM folders
    WHERE id = :folder_id OR :folder_path LIKE path || '%'
);
```
7. Logs event: "Card created: 'ubiquitous' in deck 'Academic Vocabulary'"

### Step 4: Display New Card
**System**:
- Shows success toast: "Card 'ubiquitous' added to deck"
- Options for next action:
  - **Option A (Default)**: Clear form and keep dialog open for next card
  - **Option B**: Close dialog and return to deck view
  - **Option C**: Show "Add Another" vs "Done" buttons

**Actor** (Option A - Recommended):
- Form clears: Front and Back fields reset
- Auto-focus returns to Front field
- User can immediately add next card: "pertinent"

### Step 5: Continue Adding Cards (Optional)
**Actor**: User adds 5 more cards in quick succession:
- "pertinent" / "relevant to a particular matter; applicable"
- "meticulous" / "showing great attention to detail; very careful and precise"
- "pragmatic" / "dealing with things sensibly and realistically"
- "articulate" / "having or showing the ability to speak fluently and coherently"
- "comprehensive" / "complete; including all or nearly all elements or aspects"

**System**:
- Creates each card with same process (Steps 3-4)
- Batch processing NOT used (MVP, manual entry)
- Each card gets new UUID and Box 1 initialization

**Actor**: User clicks "Done" when finished

**System**:
- Closes dialog
- Returns to deck view
- Shows deck stats: "6 cards • 6 due tomorrow"

## 6. Alternative Flows

### A1: Empty Front or Back
**Trigger**: User submits with empty field (Step 3)

**Flow**:
1. User leaves Front or Back empty
2. System validates fields
3. System shows error:
   - If Front empty: "Front side is required"
   - If Back empty: "Back side is required"
4. System highlights empty field in red
5. System focuses on empty field
6. User enters content

**Return to**: Step 2

---

### A2: Text Too Long
**Trigger**: User enters > 5000 characters (Step 2)

**Flow**:
1. User pastes very long text (5001+ characters)
2. System validates length
3. System shows warning: "Text too long (5001/5000 characters)"
4. System options:
   - Prevent further typing (recommended)
   - OR Truncate to 5000 chars (show warning)
5. Character counter shows red: "5001 / 5000"
6. User shortens text or splits into multiple cards

**Return to**: Step 2

---

### A3: Duplicate Card Warning
**Trigger**: Card with same front text exists in deck (Step 3)

**Flow**:
1. System detects duplicate front: "ubiquitous" already exists
2. System shows warning: "⚠️ A card with this front side already exists in this deck"
3. System shows existing card back for comparison:
   - Existing: "existing everywhere; omnipresent"
   - New: "existing or being everywhere, especially at the same time; omnipresent"
4. System offers options:
   - **Create Anyway** (allow duplicates in MVP)
   - **Cancel** (don't create)
   - **Edit Existing** (navigate to existing card)
5. User chooses "Create Anyway"
6. System creates duplicate card with warning logged

**Continue to**: Step 4

---

### A4: Quick Add Mode (Keyboard Shortcuts)
**Trigger**: User enables quick add mode (Alternative Step 2)

**Flow**:
1. User presses Ctrl+Shift+A (quick add mode)
2. System shows simplified inline form:
   - Front: [_______] Tab to back
   - Back: [_______] Ctrl+Enter to save
3. User types: "ubiquitous" [Tab] "existing everywhere" [Ctrl+Enter]
4. System creates card immediately
5. Form clears, focus returns to Front
6. User continues rapid entry without mouse
7. Press Esc to exit quick add mode

**Continue to**: Step 4

---

### A5: Deck Deleted During Card Creation
**Trigger**: Deck deleted by another session (Step 3)

**Flow**:
1. User enters card content
2. Another session soft-deletes the deck
3. User submits card
4. System checks deck existence
5. Deck not found (deleted_at IS NOT NULL)
6. System shows error: "Deck no longer exists. It may have been deleted."
7. System suggests: "Please refresh and select a different deck"

**End Use Case**

---

### A6: Whitespace-Only Content
**Trigger**: User enters only spaces/tabs/newlines (Step 3)

**Flow**:
1. User enters "    " (spaces only) in Front
2. System trims whitespace: TRIM(front) = ""
3. System detects empty after trim
4. System shows error: "Front side cannot be empty or whitespace only"
5. User enters valid content

**Return to**: Step 2

---

### A7: Create Card from Deck View (Inline)
**Trigger**: User creates card directly in deck card list (Alternative Step 1)

**Flow**:
1. User clicks "+ Add Card" at top of deck card list
2. System shows inline card editor (not dialog):
   - Row appears at top of list
   - Front: [______] | Back: [______] | [Save] [Cancel]
3. User enters content inline
4. User clicks Save or presses Ctrl+Enter
5. System creates card (same as Step 3)
6. Inline editor disappears, card appears in list
7. Card highlighted as "New"

**Continue to**: Step 4

## 7. Special Requirements

### Performance
- Card creation completes in < 100ms
- Deck view updates immediately (optimistic UI)
- No refresh needed

### Usability
- Auto-focus on Front field
- Tab key moves Front → Back
- Ctrl+Enter (or Cmd+Enter) submits form
- Escape key cancels
- Form persists on success (for rapid entry)
- Show character counter
- Preview card appearance (optional)
- Keyboard shortcut: Ctrl+Shift+A for quick add

### Validation
- Front: 1-5000 characters, required
- Back: 1-5000 characters, required
- Trim leading/trailing whitespace
- Warn on duplicates (but allow in MVP)

### Accessibility
- Form fields have proper labels
- Error messages announced to screen readers
- Keyboard navigation fully supported

## 8. Business Rules

### BR-035: Card Content
- Front and Back required (not empty)
- Length: 1-5000 characters each
- Plain text only (MVP, no formatting)
- Trim leading/trailing whitespace
- Whitespace-only content rejected

### BR-036: Card Uniqueness
- Duplicate front text allowed (with warning) in MVP
- Future: Strict duplicate prevention option
- Case-sensitive comparison
- Whitespace normalized before comparison

### BR-037: Initial SRS State
- New cards start in Box 1
- interval_days = 1
- due_date = tomorrow (CURRENT_DATE + 1)
- last_reviewed_at = NULL
- review_count = 0, lapse_count = 0

### BR-038: Stats Update
- Increment deck_stats.total_cards
- Increment folder_stats.total_cards_count (cascade upward)
- due_cards_count NOT incremented (card due tomorrow, not today)

## 9. Data Requirements

### Input
- deck_id: UUID, required
- front: VARCHAR(5000), required
- back: VARCHAR(5000), required

### Output
- Card object: { id, deck_id, front, back, created_at }
- CardBoxPosition object: { card_id, current_box, due_date }

### Database Changes
```sql
BEGIN TRANSACTION;

-- 1. Create card
INSERT INTO cards (id, deck_id, front, back, created_at, updated_at)
VALUES (gen_random_uuid(), :deck_id, TRIM(:front), TRIM(:back), NOW(), NOW())
RETURNING id INTO :card_id;

-- 2. Initialize SRS state
INSERT INTO card_box_position (card_id, user_id, current_box, interval_days, due_date)
VALUES (:card_id, :user_id, 1, 1, CURRENT_DATE + 1);

-- 3. Update deck stats
UPDATE deck_stats SET total_cards = total_cards + 1 WHERE deck_id = :deck_id;

-- 4. Update folder stats (cascade)
UPDATE folder_stats SET total_cards_count = total_cards_count + 1
WHERE folder_id IN (
    SELECT id FROM folders WHERE id = :folder_id OR :folder_path LIKE path || '%'
);

COMMIT;
```

### Duplicate Check Query (Optional Warning)
```sql
SELECT COUNT(*) FROM cards
WHERE deck_id = :deck_id
  AND LOWER(TRIM(front)) = LOWER(TRIM(:front))
  AND deleted_at IS NULL;
```

## 10. UI Mockup

### Card Creation Dialog
```
┌────────────────────────────────────────┐
│  Add Card to Deck                   × │
│  📂 Academic Vocabulary                │
├────────────────────────────────────────┤
│                                        │
│  Front *                               │
│  ┌──────────────────────────────────┐ │
│  │ ubiquitous                       │ │
│  │                                  │ │
│  └──────────────────────────────────┘ │
│  10 / 5000                             │
│                                        │
│  Back *                                │
│  ┌──────────────────────────────────┐ │
│  │ existing or being everywhere,    │ │
│  │ especially at the same time;     │ │
│  │ omnipresent                      │ │
│  └──────────────────────────────────┘ │
│  74 / 5000                             │
│                                        │
│  [Preview Card]                        │
│                                        │
│  Ctrl+Enter to save and add another   │
│                                        │
│  [Cancel]  [Create & Add Another]     │
└────────────────────────────────────────┘
```

### Quick Add Mode (Inline)
```
┌────────────────────────────────────────┐
│  📂 Academic Vocabulary (Quick Add)    │
│  ─────────────────────────────────────│
│  Front: [ubiquitous____________] →     │
│  Back:  [existing everywhere___] ✓     │
│                                        │
│  Ctrl+Enter: Save | Esc: Exit          │
└────────────────────────────────────────┘
```

### Deck View After Adding Cards
```
┌────────────────────────────────────────┐
│  📂 Academic Vocabulary                │
│  6 cards • 6 due tomorrow              │
├────────────────────────────────────────┤
│                                        │
│  [+ Add Card]  [Import]  [Study]       │
│                                        │
│  ┌──────────────────────────────────┐ │
│  │ ubiquitous                  New! │ │
│  │ existing everywhere...           │ │
│  │ Due tomorrow • Box 1             │ │
│  └──────────────────────────────────┘ │
│                                        │
│  ┌──────────────────────────────────┐ │
│  │ pertinent                   New! │ │
│  │ relevant to a particular...      │ │
│  │ Due tomorrow • Box 1             │ │
│  └──────────────────────────────────┘ │
│                                        │
│  ... 4 more cards ...                  │
│                                        │
└────────────────────────────────────────┘
```

## 11. Testing Scenarios

### Happy Path
1. Create card "ubiquitous" / "existing everywhere"
2. Verify card created in database
3. Verify card in Box 1, due tomorrow
4. Verify deck stats incremented
5. Verify card appears in deck view

### Rapid Entry
1. Create 10 cards in quick succession
2. Use "Create & Add Another" for all
3. Verify all 10 cards created
4. Verify all in Box 1
5. Verify deck shows "10 cards • 10 due tomorrow"

### Edge Cases
1. Create card with 5000-character front → Should succeed
2. Create card with 5001-character front → Should fail
3. Create card with only whitespace → Should fail (after trim)
4. Create card with Unicode text "学习" / "to study" → Should succeed
5. Create card with newlines in text → Should succeed (plain text)
6. Create duplicate front text → Warning, but allow

### Error Cases
1. Empty front → Error: "Front side is required"
2. Empty back → Error: "Back side is required"
3. Front too long → Error: "Text too long"
4. Deck deleted during creation → Error: "Deck no longer exists"

## 12. Performance Benchmarks

| Operation | Target | Max |
|-----------|--------|-----|
| Create single card | < 50ms | 100ms |
| Create 10 cards (sequential) | < 500ms | 1s |
| Deck view update | Immediate | 100ms |

## 13. Related Use Cases

- **UC-013**: Create Deck
- **UC-015**: Import Cards from File (bulk creation)
- **UC-016**: Export Cards to File
- **UC-019**: Review Cards with SRS
- **UC-020**: Edit Card (Future)
- **UC-021**: Delete Card (Future)

## 14. Acceptance Criteria

- [ ] User can create card with front and back text
- [ ] Front and back fields required (1-5000 chars)
- [ ] Card initialized in Box 1, due tomorrow
- [ ] Deck stats updated immediately
- [ ] Folder stats updated (cascade upward)
- [ ] Form clears for next card ("Create & Add Another")
- [ ] Card appears in deck view immediately
- [ ] Creation completes in < 100ms (p95)
- [ ] Character counters work for front and back
- [ ] Ctrl+Enter submits form
- [ ] Escape key cancels
- [ ] Duplicate warning shown (but allows creation)
- [ ] Unicode text supported
- [ ] Whitespace trimmed from input

---

**Version**: 1.0
**Last Updated**: 2025-01
