# UC-019: Review Cards with SRS

## 1. Use Case Information

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-019 |
| **Use Case Name** | Review Cards with Spaced Repetition System |
| **Primary Actor** | Student (Learner) |
| **Secondary Actors** | SRS Calculation Service, Notification Service |
| **Priority** | Critical (P0) - Core Feature |
| **Complexity** | High |
| **Status** | MVP |

## 2. Brief Description

User reviews flashcards that are due today based on SRS algorithm. System shows cards one by one, user self-assesses recall quality, and system updates card box position and due date according to the 7-box Leitner system.

## 3. Preconditions

- User is logged in
- User has cards with due_date ≤ today
- User has configured SRS settings (or using defaults)
- Daily review limit not exceeded

## 4. Postconditions

**Success**:
- All due cards reviewed (or user stops session)
- Card box positions updated
- Review logs created for each card
- User stats updated (streak, cards_learned count)
- Next due dates calculated
- User sees session summary

**Failure**:
- Review session interrupted
- Partial progress saved
- User can resume later

## 5. Main Success Scenario

### Step 1: Access Review Dashboard
**Actor**: User logs in and sees dashboard

**System**:
- Calculates due cards count:
  ```sql
  SELECT COUNT(*) FROM card_box_position cbp
  WHERE cbp.user_id = ? AND cbp.due_date <= CURRENT_DATE
  ```
- Shows summary:
  - "20 cards due today"
  - "5 new cards to learn"
  - "Current streak: 7 days"
- Shows "Start Review" button (prominent)

**Actor**: User clicks "Start Review"

### Step 2: Load Review Session
**System**:
1. Queries due cards:
   ```sql
   SELECT c.*, cbp.*, d.name as deck_name
   FROM card_box_position cbp
   JOIN cards c ON c.id = cbp.card_id
   JOIN decks d ON d.id = c.deck_id
   WHERE cbp.user_id = ?
     AND cbp.due_date <= CURRENT_DATE
   ORDER BY cbp.due_date ASC, cbp.current_box ASC
   LIMIT 200;
   ```
2. Applies review order strategy (user setting: RANDOM):
   - If RANDOM: Shuffle cards randomly
   - If ASCENDING: Order by box 1→7 (hard cards first)
   - If DESCENDING: Order by box 7→1 (easy cards first)
3. Loads first 20 cards
4. Starts session timer
5. Shows review UI with:
   - Progress: "Card 1 of 20"
   - Current box indicator: "Box 2"
   - Card content (front side only)
   - "Show Answer" button

### Step 3: Review First Card
**Actor**: User sees card:
- Front: "ubiquitous"
- Box: 2 (interval: 3 days)
- From deck: "IELTS Vocabulary"

**System**: Shows only front side, hides back

**Actor**: User thinks about answer (tries to recall "existing everywhere")

### Step 4: Reveal Answer
**Actor**: User clicks "Show Answer" button

**System**:
- Flips card to show back side
- Shows: "existing or being everywhere at the same time"
- Shows rating buttons:
  - ❌ **Again** (<1 min) - Didn't remember
  - 😐 **Hard** (<6 min) - Barely remembered
  - ✅ **Good** (7 days) - Remembered well
  - 😊 **Easy** (28 days) - Very easy

**Actor**: User evaluates recall quality

### Step 5: Rate Card "Good"
**Actor**: User clicks "Good" button (remembered well)

**System**:
1. Retrieves current box position:
   - current_box = 2 (interval: 3 days)
2. Calculates next position based on rating "GOOD":
   - next_box = 3 (move to next box)
   - next_interval = 7 days (Box 3 interval)
   - next_due_date = today + 7 days
3. Updates card_box_position:
   ```sql
   UPDATE card_box_position SET
     current_box = 3,
     interval_days = 7,
     due_date = CURRENT_DATE + 7,
     last_reviewed_at = NOW(),
     review_count = review_count + 1
   WHERE card_id = ? AND user_id = ?;
   ```
4. Creates review log:
   ```sql
   INSERT INTO review_logs (card_id, user_id, rating, previous_box, new_box, interval_days, reviewed_at)
   VALUES (?, ?, 'GOOD', 2, 3, 7, NOW());
   ```
5. Shows brief feedback:
   - "Good! Next review: [Date]"
   - Box transition: 2 → 3
6. Automatically loads next card (after 500ms)

### Step 6: Rate Card "Again" (Forgotten)
**Actor**: On card 5, user clicks "Again" (didn't remember)

**System**:
1. Current position: Box 4
2. Applies forgotten_card_action setting (user setting: MOVE_TO_BOX_1):
   - next_box = 1 (reset to start)
   - next_interval = 1 day
   - next_due_date = tomorrow
   - lapse_count += 1
3. Updates card position
4. Shows feedback:
   - "Don't worry! You'll see this again tomorrow."
   - Box transition: 4 → 1
5. Continues to next card

**Alternative forgotten actions**:
- **MOVE_DOWN_N_BOXES** (N=2): Box 4 → Box 2
- **STAY_IN_BOX**: Box 4 → Box 4, but reduce interval

### Step 7: Use Review Actions

#### Action: Undo Last Review
**Actor**: User realizes they mis-clicked rating, presses "Undo" button

**System**:
1. Retrieves last review from session history
2. Rolls back card_box_position update
3. Deletes last review_log entry
4. Shows previous card again
5. User can re-rate

#### Action: Skip Card
**Actor**: User not sure about answer, clicks "Skip" button

**System**:
1. Adds card to end of review queue
2. Shows next card
3. Skipped card will appear again at end of session

#### Action: Edit Card
**Actor**: User found typo in card, clicks "Edit" button

**System**:
1. Opens inline edit modal
2. User corrects: "ubiquituous" → "ubiquitous"
3. System saves changes
4. Review continues with corrected card

### Step 8: Complete Review Session
**Actor**: User reviews all 20 cards

**System**:
1. Detects last card completed
2. Updates user_stats:
   ```sql
   UPDATE user_stats SET
     total_cards_learned = total_cards_learned + 20,
     streak_days = <calculated>,
     last_study_date = CURRENT_DATE
   WHERE user_id = ?;
   ```
3. Calculates streak:
   - If last_study_date = yesterday: streak_days += 1
   - If last_study_date = today: no change
   - Else: streak_days = 1 (reset)
4. Invalidates folder_stats cache
5. Shows session summary:
   ```
   ┌─────────────────────────────────────┐
   │    🎉 Review Session Complete!      │
   ├─────────────────────────────────────┤
   │  Cards reviewed: 20                 │
   │  Time spent: 5 minutes 30 seconds   │
   │  Average time per card: 16 seconds  │
   │                                     │
   │  Ratings:                           │
   │    😊 Easy: 3                       │
   │    ✅ Good: 12                      │
   │    😐 Hard: 3                       │
   │    ❌ Again: 2                      │
   │                                     │
   │  Current streak: 8 days! 🔥         │
   │  Next review: 15 cards tomorrow     │
   │                                     │
   │  [View Statistics] [Done]           │
   └─────────────────────────────────────┘
   ```

**Actor**: User clicks "Done" and returns to dashboard

## 6. Alternative Flows

### A1: No Cards Due Today
**Trigger**: User starts review but no cards due (Step 1)

**Flow**:
1. System queries due cards: 0 found
2. System shows message:
   - "🎉 All caught up! No cards due today."
   - "Next review: 15 cards tomorrow"
   - "Your streak: 7 days"
3. System offers options:
   - [Learn New Cards] - Start learning new cards
   - [Cram Mode] - Review all cards (no schedule impact)
   - [Browse Decks] - Go to deck management

**End Use Case**

---

### A2: Daily Limit Reached
**Trigger**: User has reviewed 200 cards today (daily limit) (Step 1)

**Flow**:
1. System checks review count for today: 200 ≥ max_reviews_per_day (200)
2. System shows message:
   - "Daily limit reached (200 cards)"
   - "Great job! Come back tomorrow."
   - "Your streak: 7 days"
3. System offers:
   - [Override Limit] - Continue reviewing (advanced users)
   - [View Statistics] - See today's stats
   - [Done]

**End Use Case** (or continue if override)

---

### A3: Session Interrupted
**Trigger**: User closes app mid-review (Step 5)

**Flow**:
1. System detects app close event
2. System saves current progress:
   - Reviews completed so far are saved
   - Current card returns to due queue
3. Next time user opens app:
   - System shows: "Continue review session? (15 cards remaining)"
   - User can [Continue] or [Start Fresh]

**Return to**: Step 2 (resume from last position)

---

### A4: Rating "Easy" - Skip Boxes
**Trigger**: User rates card "Easy" (Step 5)

**Flow**:
1. Current box: 2 (interval: 3 days)
2. Rating: EASY (very easy to remember)
3. System calculates:
   - next_box = 3 (move to next box, same as Good)
   - next_interval = 7 × 4 = 28 days (4x multiplier for Easy)
   - next_due_date = today + 28 days
4. Shows feedback: "Easy! Next review in 28 days"
5. Note: Easy doesn't skip multiple boxes in MVP, only extends interval

**Continue to**: Next card

---

### A5: Rating "Hard" - Reduce Interval
**Trigger**: User rates card "Hard" (Step 5)

**Flow**:
1. Current box: 3 (interval: 7 days)
2. Rating: HARD (barely remembered)
3. System calculates:
   - next_box = 3 (stay in same box)
   - next_interval = 7 / 2 = 3 days (reduce interval by half)
   - next_due_date = today + 3 days
4. Shows feedback: "You'll see this again in 3 days"

**Continue to**: Next card

## 7. Exception Flows

### E1: Database Error During Update
**Trigger**: DB connection lost while updating box position (Step 5)

**Flow**:
1. System attempts to update card_box_position
2. Transaction fails
3. System shows error: "Failed to save review. Please try again."
4. System keeps card in queue (not marked as reviewed)
5. User can retry rating

**Return to**: Step 5

---

### E2: Concurrent Review Sessions
**Trigger**: User starts review on two devices simultaneously (Step 2)

**Flow**:
1. Device A loads 20 due cards
2. Device B loads same 20 cards
3. Device A rates card 1 → updates due_date
4. Device B tries to rate card 1 → detects already reviewed (due_date changed)
5. System shows warning: "This card was reviewed on another device"
6. Device B refreshes card list (exclude reviewed cards)

**Continue to**: Next unreviewed card

## 8. Special Requirements

### Performance
- Load review session: < 500ms
- Card flip animation: < 300ms
- Rating submission: < 200ms
- Session summary calculation: < 300ms
- Query optimization: Use idx_card_box_user_due index

### Usability
- Keyboard shortcuts:
  - Space: Show answer
  - 1: Again
  - 2: Hard
  - 3: Good
  - 4: Easy
  - U: Undo
  - S: Skip
  - E: Edit
- Swipe gestures (mobile):
  - Swipe up: Show answer
  - Swipe left: Again
  - Swipe right: Good
- Progress indicator always visible
- Smooth card transitions (no jarring jumps)

### Accessibility
- Screen reader announces card front/back
- High contrast mode support
- Focus management for keyboard navigation

## 9. Business Rules

### BR-030: Review Order Strategies
- **ASCENDING**: Review Box 1 → Box 7 (harder cards first)
- **DESCENDING**: Review Box 7 → Box 1 (easier cards first)
- **RANDOM**: Shuffle due cards randomly

### BR-031: Rating Impact on Box Position
| Current Box | Again | Hard | Good | Easy |
|-------------|-------|------|------|------|
| 1 | → Box 1 | → Box 1 | → Box 2 | → Box 2 (4x interval) |
| 2 | → Box 1 | → Box 2 | → Box 3 | → Box 3 (4x interval) |
| 3 | → Box 1 | → Box 3 | → Box 4 | → Box 4 (4x interval) |
| 4 | → Box 1 | → Box 4 | → Box 5 | → Box 5 (4x interval) |
| 5 | → Box 1 | → Box 5 | → Box 6 | → Box 6 (4x interval) |
| 6 | → Box 1 | → Box 6 | → Box 7 | → Box 7 (4x interval) |
| 7 | → Box 1 | → Box 7 | → Box 7 | → Box 7 (4x interval) |

### BR-032: Forgotten Card Actions
- **MOVE_TO_BOX_1** (default): Reset to Box 1, interval = 1 day
- **MOVE_DOWN_N_BOXES**: Move down N boxes (user configurable: 1-3)
- **STAY_IN_BOX**: Stay in current box, reduce interval by half

### BR-033: Daily Limits
- new_cards_per_day: Default 20, configurable
- max_reviews_per_day: Default 200, configurable
- User can override limits (shows confirmation)

### BR-034: Streak Calculation
```
IF last_study_date = NULL THEN
  streak_days = 1
ELSE IF last_study_date = today THEN
  streak_days = streak_days (no change)
ELSE IF last_study_date = yesterday THEN
  streak_days = streak_days + 1
ELSE
  streak_days = 1 (reset)
END IF
```

## 10. Data Requirements

### Input
- user_id: UUID
- scope: "all", "folder", or "deck"
- scope_id: UUID (if scope = folder or deck)

### Output
- Cards: List<{ id, front, back, current_box, deck_name }>
- Session summary: { cards_reviewed, time_spent, rating_distribution, streak }

### Database Queries

**Critical Query** (Most Important):
```sql
-- Get due cards (uses idx_card_box_user_due index)
SELECT c.id, c.front, c.back, c.deck_id,
       cbp.current_box, cbp.due_date,
       d.name as deck_name
FROM card_box_position cbp
JOIN cards c ON c.id = cbp.card_id
JOIN decks d ON d.id = c.deck_id
WHERE cbp.user_id = :userId
  AND cbp.due_date <= CURRENT_DATE
ORDER BY cbp.due_date ASC, cbp.current_box ASC
LIMIT 200;
```

**Update After Review**:
```sql
UPDATE card_box_position SET
  current_box = :newBox,
  interval_days = :newInterval,
  due_date = :newDueDate,
  last_reviewed_at = NOW(),
  review_count = review_count + 1,
  lapse_count = CASE WHEN :rating = 'AGAIN' THEN lapse_count + 1 ELSE lapse_count END,
  updated_at = NOW()
WHERE card_id = :cardId AND user_id = :userId;
```

## 11. UI Mockup

### Review Screen
```
┌─────────────────────────────────────────────┐
│  Review Session            [≡] [×]          │
├─────────────────────────────────────────────┤
│  Progress: 5 / 20 cards     Box: 2          │
│  ▓▓▓▓▓░░░░░░░░░░░░░░░░ 25%                  │
├─────────────────────────────────────────────┤
│                                             │
│                                             │
│             ubiquitous                      │
│                                             │
│         (click to show answer)              │
│                                             │
│                                             │
├─────────────────────────────────────────────┤
│  Deck: IELTS Vocabulary                     │
│                                             │
│  [Show Answer]                              │
│                                             │
│  Actions: [Undo] [Skip] [Edit]              │
└─────────────────────────────────────────────┘

After showing answer:
┌─────────────────────────────────────────────┐
│  Progress: 5 / 20 cards     Box: 2          │
│  ▓▓▓▓▓░░░░░░░░░░░░░░░░ 25%                  │
├─────────────────────────────────────────────┤
│  Front:                                     │
│    ubiquitous                               │
│                                             │
│  Back:                                      │
│    existing or being everywhere             │
│    at the same time                         │
│                                             │
├─────────────────────────────────────────────┤
│  How well did you remember?                 │
│                                             │
│  [❌ Again]  [😐 Hard]  [✅ Good]  [😊 Easy] │
│   <1 min     <6 min      7 days     28 days │
│                                             │
│  Actions: [Undo] [Skip] [Edit]              │
└─────────────────────────────────────────────┘
```

## 12. Testing Scenarios

### Happy Path
1. Start review with 20 due cards
2. Review all cards with mix of ratings
3. Complete session successfully
4. Verify box positions updated
5. Verify streak incremented
6. Session summary displayed

### Edge Cases
1. All cards rated "Again" → All back to Box 1
2. All cards rated "Easy" → All get 4x interval
3. Review exactly at midnight → Correct date handling
4. 200th card review → Hit daily limit
5. Resume interrupted session → Correct state

### Error Cases
1. No due cards → Show "all caught up" message
2. Database error mid-session → Rollback, retry option
3. Concurrent reviews → Handle gracefully
4. Invalid rating → Validation error

## 13. Related Use Cases

- **UC-020**: Review with Cram Mode
- **UC-021**: Review with Random Mode
- **UC-022**: Configure SRS Settings
- **UC-023**: View Statistics

## 14. Acceptance Criteria

- [ ] User can start review with due cards
- [ ] Cards displayed one at a time
- [ ] Front shown first, back revealed on click
- [ ] 4 rating options work correctly
- [ ] Box positions updated based on rating
- [ ] Forgotten card action applied correctly
- [ ] Review order strategy applied
- [ ] Undo, Skip, Edit actions work
- [ ] Session summary shows correct stats
- [ ] Streak calculated correctly
- [ ] Daily limits enforced
- [ ] Performance targets met (< 500ms load)
- [ ] Keyboard shortcuts work
- [ ] Mobile swipe gestures work

---

**Version**: 1.0
**Last Updated**: 2025-01
