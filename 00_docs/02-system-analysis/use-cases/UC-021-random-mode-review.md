# UC-021: Random Mode Review

## 1. Use Case Information

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-021 |
| **Use Case Name** | Random Mode Review |
| **Primary Actor** | Student (Learner) |
| **Secondary Actors** | None |
| **Priority** | Medium (P2) |
| **Complexity** | Low |
| **Status** | MVP |

## 2. Brief Description

User reviews a random selection of cards from a deck without affecting their SRS schedules. This mode is useful for variety, avoiding predictability, testing overall knowledge, or casual practice sessions without the pressure of scheduled reviews.

## 3. Preconditions

- User is logged in
- User has at least one deck with cards
- Deck contains at least 1 card
- User is not in another active review session

## 4. Postconditions

**Success**:
- User reviewed random cards without affecting SRS schedule
- Session statistics recorded (separate from regular reviews)
- No changes to card_box_position (box, due_date, interval unchanged)
- Random session history saved
- User can view session statistics

**Failure**:
- No review session started
- Error message displayed
- User remains on deck view

## 5. Main Success Scenario

### Step 1: Start Random Mode
**Actor**: User opens deck "Academic Vocabulary" (100 cards) and clicks "Random Review"

**System**:
- Shows random mode configuration dialog:
  - Total cards available: 100
  - Number of cards to review: [20 ▼]
    - 10 cards
    - 20 cards (default)
    - 50 cards
    - 100 cards (all)
    - Custom...
  - Show answer time: ☑
  - Truly random: ☑ (same card can appear multiple times)

### Step 2: Configure Random Session
**Actor**: User selects:
- Number of cards: 20
- Show answer time: Checked
- Truly random: Unchecked (each card once)

**System**:
- Validates configuration
- Estimates time: "~3 minutes (20 cards × 9s avg)"
- Shows preview: "Review 20 random cards (no repeats)"

### Step 3: Load Random Cards
**Actor**: User clicks "Start Random Review"

**System**:
1. Queries random sample of cards:
```sql
-- Without repeats (default)
SELECT c.id, c.front, c.back
FROM cards c
WHERE c.deck_id = :deck_id
  AND c.deleted_at IS NULL
ORDER BY RANDOM()
LIMIT 20;

-- With repeats (truly random)
-- SELECT with replacement using multiple random queries
```

2. Creates random session record:
```sql
INSERT INTO random_sessions (id, user_id, deck_id, total_cards, started_at)
VALUES (gen_random_uuid(), :user_id, :deck_id, 20, NOW());
```

3. Initializes session state:
   - Cards to review: 20 (random selection)
   - Current index: 0
   - Reviewed count: 0
   - Show answer time: TRUE

### Step 4: Review Random Cards (Show Front)
**System**:
- Shows card 1/20
- Displays front: "meticulous"
- Shows progress: "1 / 20"
- Shows timer (starts counting)
- Buttons: [Show Answer]

**Actor**: User reads front side for 4 seconds

### Step 5: Review Random Cards (Show Back)
**Actor**: User clicks "Show Answer"

**System**:
- Stops timer: 4s
- Shows back: "showing great attention to detail; very careful and precise"
- Shows answer time: "4s" (optional)
- Buttons: [Again] [Hard] [Good] [Easy]
- ℹ️ Note: "Random mode: Ratings won't affect card schedules"

**Actor**: User rates card "Good"

### Step 6: Record Random Rating (No SRS Update)
**System**:
1. Records random review result:
```sql
INSERT INTO random_reviews (session_id, card_id, rating, answer_time_seconds)
VALUES (:session_id, :card_id, 'GOOD', 4);
```

2. **DOES NOT** update card_box_position:
   - No box change
   - No interval change
   - No due_date change
   - No lapse_count increment

3. Increments reviewed count: 1 → 2
4. Loads next random card (2/20)

**System**: Repeats Steps 4-6 for all 20 cards

### Step 7: Complete Random Session
**System** (after 20th card):
- Ends session:
```sql
UPDATE random_sessions SET
    ended_at = NOW(),
    cards_reviewed = 20,
    status = 'COMPLETED'
WHERE id = :session_id;
```

- Calculates statistics:
  - Total time: 2 minutes 40 seconds
  - Cards reviewed: 20
  - Average time per card: 8.0s
  - Ratings breakdown:
    - Again: 3 (15%)
    - Hard: 5 (25%)
    - Good: 10 (50%)
    - Easy: 2 (10%)

### Step 8: Display Random Session Summary
**System**:
- Shows completion screen:
  - "🎲 Random Review Complete!"
  - Deck: "Academic Vocabulary"
  - Cards reviewed: 20
  - Time: 2m 40s
  - Average: 8.0s per card
  - Ratings chart
- Options:
  - [Random Again] (new random selection)
  - [Regular Review] (switch to SRS mode)
  - [Back to Deck]

**Actor**: User clicks "Random Again" or returns to deck

## 6. Alternative Flows

### A1: Truly Random (With Repeats)
**Trigger**: User checks "Truly random" option (Step 2)

**Flow**:
1. User selects: "Truly random" (same card can appear multiple times)
2. System uses random selection WITH replacement
3. Example: 20-card session might show:
   - Card A appears 3 times
   - Card B appears 0 times
   - Card C appears 2 times
   - Total: 20 reviews (possibly < 20 unique cards)
4. Review proceeds with potential repeats

**Continue to**: Step 8

---

### A2: Custom Card Count
**Trigger**: User selects "Custom" card count (Step 2)

**Flow**:
1. User selects "Custom..."
2. System shows input: "Number of cards: [____] (1-100)"
3. User enters: 35
4. System validates: 1 ≤ 35 ≤ 100 (total cards)
5. Session configured for 35 random cards
6. Review proceeds

**Continue to**: Step 3

---

### A3: All Cards (Random Order)
**Trigger**: User selects "All cards" (Step 2)

**Flow**:
1. User selects: "100 cards (all)"
2. System queries all cards in random order:
```sql
SELECT c.id, c.front, c.back
FROM cards c
WHERE c.deck_id = :deck_id AND c.deleted_at IS NULL
ORDER BY RANDOM();
```
3. Session includes all 100 cards (in random order)
4. Review proceeds

**Continue to**: Step 3

---

### A4: Exit Random Session Early
**Trigger**: User clicks "Exit" during review (Step 5)

**Flow**:
1. User clicks "Exit" after reviewing 12/20 cards
2. System shows confirmation: "Exit random review? You've reviewed 12/20 cards."
3. User confirms exit
4. System saves partial session:
```sql
UPDATE random_sessions SET
    ended_at = NOW(),
    cards_reviewed = 12,
    status = 'INCOMPLETE'
WHERE id = :session_id;
```
5. Returns to deck view
6. Shows toast: "Random session paused. Reviewed 12 cards."

**End Use Case**

---

### A5: Repeat Random Session (New Selection)
**Trigger**: User clicks "Random Again" (Step 8)

**Flow**:
1. User completes 20-card random session
2. User clicks "Random Again"
3. System generates NEW random selection (different 20 cards)
4. New session created
5. Review proceeds with new cards

**Continue to**: Step 3

---

### A6: Small Deck - All Cards Mode
**Trigger**: Deck has fewer cards than requested (Step 3)

**Flow**:
1. User requests 50 random cards
2. Deck only has 30 cards
3. System adjusts: "Only 30 cards available. Reviewing all cards in random order."
4. Session includes all 30 cards
5. Review proceeds

**Continue to**: Step 3

---

### A7: Empty Deck - No Cards
**Trigger**: Deck has 0 cards (Step 1)

**Flow**:
1. User opens empty deck and clicks "Random Review"
2. System detects 0 cards
3. System shows message: "No cards available for random review"
4. System suggests: "Add cards to start studying"
5. User cancels

**End Use Case**

## 7. Special Requirements

### Performance
- Load random session in < 300ms
- Random card selection in < 100ms
- Card transitions in < 50ms
- No lag during review

### Usability
- Clear distinction from SRS and cram modes
- Visual indicator: "🎲 RANDOM MODE" badge
- Prominent note: "Ratings won't affect schedules"
- Keyboard shortcuts supported
- Progress bar always visible

### Randomness
- Use cryptographically secure random (for fairness)
- Option for "truly random" (with replacement)
- Default: no repeats (without replacement)
- Shuffled uniformly (no bias)

### SRS Isolation
- **Critical**: No changes to card_box_position
- No impact on due dates
- No impact on intervals
- Regular SRS reviews unaffected

## 8. Business Rules

### BR-067: Random Mode Isolation
- Random reviews stored separately (random_reviews table)
- **NEVER** update card_box_position during random mode
- Regular SRS schedule completely unaffected
- Random statistics separate from SRS statistics

### BR-068: Randomness Options
- **Without repeats** (default): Each card appears at most once
- **With repeats** (truly random): Same card can appear multiple times
- Uniform random distribution
- Use secure random number generator

### BR-069: Card Count Selection
- Default: 20 cards
- Options: 10, 20, 50, 100 (all), Custom
- Custom range: 1 to total_cards
- If requested > available, use all cards

### BR-070: Rating Collection
- Still collect ratings: Again / Hard / Good / Easy
- Ratings used only for session statistics
- Help user gauge performance
- No impact on card scheduling

### BR-071: Session Management
- One random session at a time per user
- Can exit early (partial completion)
- Can repeat with new random selection
- History preserved for analytics

## 9. Data Requirements

### Input
- deck_id: UUID, required
- card_count: INTEGER (1 to total_cards)
- truly_random: BOOLEAN (with/without replacement)
- show_answer_time: BOOLEAN

### Output
- Random session summary with statistics

### Database Changes

**Create Random Session**:
```sql
INSERT INTO random_sessions (
    id, user_id, deck_id, total_cards,
    truly_random, started_at, status
)
VALUES (
    gen_random_uuid(), :user_id, :deck_id,
    :card_count, :truly_random, NOW(), 'IN_PROGRESS'
);
```

**Query Random Cards (Without Repeats)**:
```sql
SELECT c.id, c.front, c.back
FROM cards c
WHERE c.deck_id = :deck_id AND c.deleted_at IS NULL
ORDER BY RANDOM()
LIMIT :card_count;
```

**Record Random Review**:
```sql
-- Store review (NO UPDATE to card_box_position!)
INSERT INTO random_reviews (
    session_id, card_id, rating,
    answer_time_seconds, reviewed_at
)
VALUES (
    :session_id, :card_id, :rating,
    :answer_time, NOW()
);
```

**Complete Session**:
```sql
UPDATE random_sessions SET
    ended_at = NOW(),
    cards_reviewed = :reviewed_count,
    status = :status  -- 'COMPLETED' or 'INCOMPLETE'
WHERE id = :session_id;
```

## 10. UI Mockup

### Random Mode Configuration
```
┌────────────────────────────────────────┐
│  🎲 Random Review Setup                 │
│  📂 Academic Vocabulary                │
├────────────────────────────────────────┤
│                                        │
│  Total cards available: 100            │
│                                        │
│  Number of cards to review             │
│  [20 cards                        ▼]   │
│    • 10 cards                          │
│    • 20 cards (recommended)            │
│    • 50 cards                          │
│    • 100 cards (all)                   │
│    • Custom...                         │
│                                        │
│  Options                               │
│  ☑ Show answer time                    │
│  ☐ Truly random (may repeat cards)     │
│                                        │
│  Estimated time: ~3 minutes            │
│                                        │
│  ℹ️ Random mode: Review without affecting│
│     your SRS schedule                  │
│                                        │
│  [Cancel]  [Start Random Review]       │
└────────────────────────────────────────┘
```

### Random Review Screen
```
┌────────────────────────────────────────┐
│  🎲 RANDOM MODE                         │
│  📂 Academic Vocabulary                │
│  [══════════░░░░░░░░░░] 12 / 20        │
├────────────────────────────────────────┤
│                                        │
│            meticulous                  │
│                                        │
│                                        │
│                                        │
│                                        │
│                                        │
│  [Show Answer]                    4.1s │
│                                        │
│  ℹ️ Ratings won't affect card schedule │
│                                        │
│  [Exit]                                │
└────────────────────────────────────────┘
```

### Random Session Complete
```
┌────────────────────────────────────────┐
│  🎲 Random Review Complete!             │
├────────────────────────────────────────┤
│                                        │
│  Deck: Academic Vocabulary             │
│  Cards reviewed: 20                    │
│  Time: 2m 40s                          │
│  Average: 8.0s per card                │
│                                        │
│  Ratings:                              │
│  ▓▓▓░░░░░░░ Again: 3 (15%)             │
│  ▓▓▓▓▓░░░░░ Hard:  5 (25%)             │
│  ▓▓▓▓▓▓▓▓▓▓ Good:  10 (50%)            │
│  ▓▓░░░░░░░░ Easy:  2 (10%)             │
│                                        │
│  💡 Great practice session!            │
│                                        │
│  [Random Again] [Regular Review] [Done]│
└────────────────────────────────────────┘
```

## 11. Testing Scenarios

### Happy Path
1. Start random mode for deck (20 cards)
2. Review all 20 random cards
3. Rate cards with different ratings
4. Complete session
5. Verify NO changes to card_box_position
6. Verify random_reviews recorded
7. Verify statistics displayed

### Alternative Scenarios
1. Truly random (with repeats) → Same card may appear multiple times
2. Custom count (35 cards) → 35 random cards shown
3. All cards (100) → All cards in random order
4. Exit early after 12 cards → Partial session saved
5. Random again → New random selection

### Edge Cases
1. Random review on empty deck → Error message
2. Random review on deck with 1 card → Shows that card
3. Request 50 cards from 30-card deck → Shows all 30
4. Truly random with 100 cards → Some cards repeat, some never shown
5. Very large deck (1000 cards), request 20 → Fast random selection

### Critical Tests (SRS Isolation)
1. **Verify card_box_position unchanged after random review**
2. **Verify due_date unchanged**
3. **Verify interval_days unchanged**
4. **Verify lapse_count unchanged**
5. Regular SRS review after random → Uses original schedule

### Error Cases
1. Start random with no cards → Error
2. Start random with deleted deck → Error
3. Request invalid card count (0 or negative) → Error

## 12. Performance Benchmarks

| Operation | Target | Max |
|-----------|--------|-----|
| Load random session | < 200ms | 300ms |
| Random card selection | < 50ms | 100ms |
| Card transition | < 50ms | 100ms |
| Record rating | < 50ms | 100ms |
| Complete session | < 150ms | 300ms |

## 13. Related Use Cases

- **UC-019**: Review Cards with SRS (main review mode)
- **UC-020**: Cram Mode Review (similar non-SRS review)
- **UC-022**: Configure SRS Settings
- **UC-023**: View Statistics (includes random stats)

## 14. Acceptance Criteria

- [ ] User can start random mode from any deck
- [ ] Can select card count: 10, 20, 50, all, custom
- [ ] Can choose "truly random" (with replacement)
- [ ] Random selection uniform and unbiased
- [ ] Cards shown in random order
- [ ] Ratings collected: Again/Hard/Good/Easy
- [ ] **NO changes to card_box_position**
- [ ] **NO impact on SRS schedule**
- [ ] Random reviews stored separately
- [ ] Session statistics calculated correctly
- [ ] Can exit early (partial completion)
- [ ] Can repeat with new random selection
- [ ] Clear visual distinction from other modes
- [ ] Performance within benchmarks

---

**Version**: 1.0
**Last Updated**: 2025-01
