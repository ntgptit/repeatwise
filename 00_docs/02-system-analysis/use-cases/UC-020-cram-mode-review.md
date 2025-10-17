# UC-020: Cram Mode Review

## 1. Use Case Information

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-020 |
| **Use Case Name** | Cram Mode Review |
| **Primary Actor** | Student (Learner) |
| **Secondary Actors** | None |
| **Priority** | Medium (P2) |
| **Complexity** | Low-Medium |
| **Status** | MVP |

## 2. Brief Description

User reviews all cards in a deck regardless of their due dates, without affecting their SRS schedules. This mode is useful for exam preparation, quick refreshers, or practicing specific content without impacting long-term learning progress.

## 3. Preconditions

- User is logged in
- User has at least one deck with cards
- Deck contains at least 1 card
- User is not in another active review session

## 4. Postconditions

**Success**:
- User reviewed cards without affecting SRS schedule
- Session statistics recorded (separate from regular reviews)
- No changes to card_box_position (box, due_date, interval unchanged)
- Cram session history saved
- User can view cram session statistics

**Failure**:
- No review session started
- Error message displayed
- User remains on deck view

## 5. Main Success Scenario

### Step 1: Start Cram Mode
**Actor**: User opens deck "Academic Vocabulary" (100 cards, 20 due today) and clicks "Cram Mode"

**System**:
- Shows cram mode configuration dialog:
  - Total cards available: 100
  - Cards due today: 20
  - Cards to review: [All 100 cards ▼]
  - Order: [Random ▼]
  - Show answer time: ☑
- Provides options:
  - Review all cards (100)
  - Review due cards only (20)
  - Review specific box (1-7)
  - Custom range

### Step 2: Configure Cram Session
**Actor**: User selects:
- Cards to review: "All cards" (100)
- Order: "Random"
- Show answer time: Checked

**System**:
- Validates configuration
- Estimates time: "~15 minutes (100 cards × 9s avg)"
- Shows preview: "Cram 100 cards in random order"

### Step 3: Load Cards (No SRS Filtering)
**Actor**: User clicks "Start Cram Session"

**System**:
1. Queries all cards in deck (ignores due_date):
```sql
SELECT c.id, c.front, c.back
FROM cards c
WHERE c.deck_id = :deck_id
  AND c.deleted_at IS NULL
ORDER BY RANDOM()  -- Random order
LIMIT 100;
```
2. Creates cram session record:
```sql
INSERT INTO cram_sessions (id, user_id, deck_id, total_cards, started_at)
VALUES (gen_random_uuid(), :user_id, :deck_id, 100, NOW());
```
3. Initializes session state:
   - Cards to review: 100
   - Current index: 0
   - Reviewed count: 0
   - Show answer time: TRUE

### Step 4: Review Cards (Show Front)
**System**:
- Shows card 1/100
- Displays front: "ubiquitous"
- Shows progress: "1 / 100"
- Shows timer (starts counting)
- Buttons: [Show Answer]

**Actor**: User reads front side for 3 seconds

### Step 5: Review Cards (Show Back)
**Actor**: User clicks "Show Answer"

**System**:
- Stops timer: 3s
- Shows back: "existing or being everywhere, especially at the same time; omnipresent"
- Shows answer time: "3s" (optional)
- Buttons: [Again] [Hard] [Good] [Easy]
- ℹ️ Note: "Cram mode: Your ratings won't affect card schedules"

**Actor**: User rates card "Good"

### Step 6: Record Cram Rating (No SRS Update)
**System**:
1. Records cram review result:
```sql
INSERT INTO cram_reviews (session_id, card_id, rating, answer_time_seconds)
VALUES (:session_id, :card_id, 'GOOD', 3);
```
2. **DOES NOT** update card_box_position:
   - No box change
   - No interval change
   - No due_date change
   - No lapse_count increment
3. Increments reviewed count: 1 → 2
4. Loads next card (2/100)

**System**: Repeats Steps 4-6 for all 100 cards

### Step 7: Complete Cram Session
**System** (after last card):
- Ends session:
```sql
UPDATE cram_sessions SET
    ended_at = NOW(),
    cards_reviewed = 100,
    status = 'COMPLETED'
WHERE id = :session_id;
```
- Calculates statistics:
  - Total time: 12 minutes 30 seconds
  - Cards reviewed: 100
  - Average time per card: 7.5s
  - Ratings breakdown:
    - Again: 12 (12%)
    - Hard: 25 (25%)
    - Good: 50 (50%)
    - Easy: 13 (13%)

### Step 8: Display Cram Session Summary
**System**:
- Shows completion screen:
  - "🎉 Cram Session Complete!"
  - Deck: "Academic Vocabulary"
  - Cards reviewed: 100
  - Time: 12m 30s
  - Average: 7.5s per card
  - Ratings chart
- Options:
  - [Cram Again] (restart with same settings)
  - [Regular Review] (switch to SRS mode)
  - [Back to Deck]

**Actor**: User clicks "Back to Deck" or continues studying

## 6. Alternative Flows

### A1: Cram Due Cards Only
**Trigger**: User selects "Due cards only" (Step 2)

**Flow**:
1. User selects: "Review due cards only" (20 cards)
2. System queries only due cards:
```sql
SELECT c.id, c.front, c.back
FROM cards c
JOIN card_box_position cbp ON cbp.card_id = c.id
WHERE c.deck_id = :deck_id
  AND cbp.due_date <= CURRENT_DATE
  AND c.deleted_at IS NULL
ORDER BY RANDOM()
LIMIT 20;
```
3. Cram session includes only 20 cards
4. Review proceeds normally (Steps 4-8)

**Continue to**: Step 8

---

### A2: Cram Specific Box
**Trigger**: User selects "Specific box" (Step 2)

**Flow**:
1. User selects: "Box 1 only" (new/difficult cards)
2. System queries cards in Box 1:
```sql
SELECT c.id, c.front, c.back
FROM cards c
JOIN card_box_position cbp ON cbp.card_id = c.id
WHERE c.deck_id = :deck_id
  AND cbp.current_box = 1
  AND c.deleted_at IS NULL
ORDER BY RANDOM();
```
3. Found: 15 cards in Box 1
4. Cram session includes 15 cards
5. Review proceeds

**Continue to**: Step 8

---

### A3: Exit Cram Session Early
**Trigger**: User clicks "Exit" during review (Step 5)

**Flow**:
1. User clicks "Exit" after reviewing 40/100 cards
2. System shows confirmation: "Exit cram session? You've reviewed 40/100 cards."
3. User confirms exit
4. System saves partial session:
```sql
UPDATE cram_sessions SET
    ended_at = NOW(),
    cards_reviewed = 40,
    status = 'INCOMPLETE'
WHERE id = :session_id;
```
5. Returns to deck view
6. Shows toast: "Cram session paused. Reviewed 40 cards."

**End Use Case**

---

### A4: Cram Mode with Ascending Order
**Trigger**: User selects "Ascending" order (Step 2)

**Flow**:
1. User selects order: "Ascending" (easier cards first)
2. System orders by current_box ASC:
```sql
ORDER BY cbp.current_box ASC, RANDOM()
```
3. Cards shown: Box 1 → Box 2 → ... → Box 7
4. Review proceeds

**Continue to**: Step 8

---

### A5: Repeat Failed Cards
**Trigger**: User wants to re-cram cards rated "Again" (Step 8)

**Flow**:
1. User completes cram session (12 cards rated "Again")
2. System offers: "Repeat cards you rated 'Again'?"
3. User clicks "Yes"
4. System creates new cram session with only those 12 cards
5. Review proceeds with filtered cards

**Continue to**: Step 3 (with 12 cards)

---

### A6: Empty Deck - No Cards to Cram
**Trigger**: Deck has 0 cards (Step 1)

**Flow**:
1. User opens empty deck and clicks "Cram Mode"
2. System detects 0 cards
3. System shows message: "No cards available for cramming"
4. System suggests: "Add cards to start studying"
5. User cancels

**End Use Case**

## 7. Special Requirements

### Performance
- Load cram session in < 500ms
- Card transitions in < 100ms
- No lag during review
- Statistics calculated instantly

### Usability
- Clear distinction from regular SRS review
- Visual indicator: "CRAM MODE" badge
- Prominent note: "Ratings won't affect schedules"
- Keyboard shortcuts supported
- Progress bar always visible

### SRS Isolation
- **Critical**: No changes to card_box_position
- No impact on due dates
- No impact on intervals
- No impact on lapse counts
- Regular SRS reviews unaffected

## 8. Business Rules

### BR-062: Cram Mode Isolation
- Cram reviews stored separately (cram_reviews table)
- **NEVER** update card_box_position during cram mode
- Regular SRS schedule completely unaffected
- Cram statistics separate from SRS statistics

### BR-063: Card Selection
- Can cram all cards, due cards, specific box, or custom range
- Ignore due_date filtering (unless "due cards only")
- Include all non-deleted cards
- Order: Random (default), Ascending, Descending, Sequential

### BR-064: Rating Collection
- Still collect ratings: Again / Hard / Good / Easy
- Ratings used only for cram session statistics
- Help user identify weak cards
- No impact on card scheduling

### BR-065: Session Management
- One cram session at a time per user
- Can exit early (partial completion)
- Can repeat with same or different settings
- History preserved for analytics

### BR-066: Statistics Tracking
- Track total time, average time per card
- Track rating distribution
- Track completion rate
- Separate from regular review stats

## 9. Data Requirements

### Input
- deck_id: UUID, required
- card_selection: ENUM('ALL', 'DUE', 'BOX', 'CUSTOM')
- box_filter: INTEGER (1-7), if card_selection = 'BOX'
- order: ENUM('RANDOM', 'ASCENDING', 'DESCENDING', 'SEQUENTIAL')
- show_answer_time: BOOLEAN

### Output
- Cram session summary with statistics

### Database Changes

**Create Cram Session**:
```sql
INSERT INTO cram_sessions (id, user_id, deck_id, total_cards, started_at, status)
VALUES (gen_random_uuid(), :user_id, :deck_id, :total_cards, NOW(), 'IN_PROGRESS');
```

**Record Cram Review**:
```sql
-- Store review (NO UPDATE to card_box_position!)
INSERT INTO cram_reviews (session_id, card_id, rating, answer_time_seconds, reviewed_at)
VALUES (:session_id, :card_id, :rating, :answer_time, NOW());
```

**Complete Session**:
```sql
UPDATE cram_sessions SET
    ended_at = NOW(),
    cards_reviewed = :reviewed_count,
    status = :status  -- 'COMPLETED' or 'INCOMPLETE'
WHERE id = :session_id;
```

**Query Cards for Cram**:
```sql
-- All cards
SELECT c.id, c.front, c.back, cbp.current_box
FROM cards c
LEFT JOIN card_box_position cbp ON cbp.card_id = c.id
WHERE c.deck_id = :deck_id AND c.deleted_at IS NULL
ORDER BY RANDOM();

-- Due cards only
SELECT c.id, c.front, c.back
FROM cards c
JOIN card_box_position cbp ON cbp.card_id = c.id
WHERE c.deck_id = :deck_id
  AND cbp.due_date <= CURRENT_DATE
  AND c.deleted_at IS NULL
ORDER BY RANDOM();

-- Specific box
SELECT c.id, c.front, c.back
FROM cards c
JOIN card_box_position cbp ON cbp.card_id = c.id
WHERE c.deck_id = :deck_id
  AND cbp.current_box = :box_number
  AND c.deleted_at IS NULL
ORDER BY RANDOM();
```

## 10. UI Mockup

### Cram Mode Configuration
```
┌────────────────────────────────────────┐
│  🎯 Cram Mode Setup                     │
│  📂 Academic Vocabulary                │
├────────────────────────────────────────┤
│                                        │
│  Cards to Review                       │
│  [All cards (100)                 ▼]   │
│    • All cards (100)                   │
│    • Due cards only (20)               │
│    • Specific box...                   │
│    • Custom range...                   │
│                                        │
│  Order                                 │
│  [Random                          ▼]   │
│                                        │
│  Options                               │
│  ☑ Show answer time                    │
│                                        │
│  Estimated time: ~15 minutes           │
│                                        │
│  ℹ️ Cram mode: Review without affecting │
│     your SRS schedule                  │
│                                        │
│  [Cancel]  [Start Cram Session]        │
└────────────────────────────────────────┘
```

### Cram Review Screen
```
┌────────────────────────────────────────┐
│  🎯 CRAM MODE                          │
│  📂 Academic Vocabulary                │
│  [═══════════════════░░░] 15 / 100     │
├────────────────────────────────────────┤
│                                        │
│             ubiquitous                 │
│                                        │
│                                        │
│                                        │
│                                        │
│                                        │
│  [Show Answer]                    3.2s │
│                                        │
│  ℹ️ Ratings won't affect card schedule │
│                                        │
│  [Exit]                                │
└────────────────────────────────────────┘
```

### Cram Session Complete
```
┌────────────────────────────────────────┐
│  🎉 Cram Session Complete!              │
├────────────────────────────────────────┤
│                                        │
│  Deck: Academic Vocabulary             │
│  Cards reviewed: 100                   │
│  Time: 12m 30s                         │
│  Average: 7.5s per card                │
│                                        │
│  Ratings:                              │
│  ▓▓░░░░░░░░ Again: 12 (12%)            │
│  ▓▓▓▓▓░░░░░ Hard:  25 (25%)            │
│  ▓▓▓▓▓▓▓▓▓▓ Good:  50 (50%)            │
│  ▓▓▓░░░░░░░ Easy:  13 (13%)            │
│                                        │
│  💡 12 cards rated "Again"             │
│  [Repeat Failed Cards]                 │
│                                        │
│  [Cram Again] [Regular Review] [Done]  │
└────────────────────────────────────────┘
```

## 11. Testing Scenarios

### Happy Path
1. Start cram mode for deck (100 cards)
2. Review all 100 cards
3. Rate cards with different ratings
4. Complete session
5. Verify NO changes to card_box_position
6. Verify cram_reviews recorded
7. Verify statistics displayed

### Alternative Scenarios
1. Cram due cards only → 20 cards shown
2. Cram specific box (Box 1) → Only Box 1 cards
3. Exit early after 40 cards → Partial session saved
4. Repeat failed cards → New session with 12 cards

### Edge Cases
1. Cram empty deck → Error message
2. Cram deck with 1 card → Should work
3. Cram session with all "Again" ratings → Statistics accurate
4. Cram very large deck (1000 cards) → Performance OK

### Critical Tests (SRS Isolation)
1. **Verify card_box_position unchanged after cram**
2. **Verify due_date unchanged**
3. **Verify interval_days unchanged**
4. **Verify lapse_count unchanged**
5. Regular SRS review after cram → Uses original schedule

### Error Cases
1. Start cram with no cards → Error
2. Start cram with deleted deck → Error
3. Network failure during cram → Session recoverable

## 12. Performance Benchmarks

| Operation | Target | Max |
|-----------|--------|-----|
| Load cram session | < 300ms | 500ms |
| Card transition | < 50ms | 100ms |
| Record rating | < 50ms | 100ms |
| Complete session | < 200ms | 500ms |
| Query 100 cards | < 100ms | 200ms |

## 13. Related Use Cases

- **UC-019**: Review Cards with SRS (main review mode)
- **UC-021**: Random Mode Review (similar non-SRS review)
- **UC-022**: Configure SRS Settings
- **UC-023**: View Statistics (includes cram stats)

## 14. Acceptance Criteria

- [ ] User can start cram mode from any deck
- [ ] Can select: all cards, due cards, specific box
- [ ] Can choose order: random, ascending, descending
- [ ] Cards shown without due date filtering (unless "due only")
- [ ] Ratings collected: Again/Hard/Good/Easy
- [ ] **NO changes to card_box_position**
- [ ] **NO impact on SRS schedule**
- [ ] Cram reviews stored separately
- [ ] Session statistics calculated correctly
- [ ] Can exit early (partial completion)
- [ ] Can repeat with same/different settings
- [ ] Clear visual distinction from SRS mode
- [ ] Performance within benchmarks

---

**Version**: 1.0
**Last Updated**: 2025-01
