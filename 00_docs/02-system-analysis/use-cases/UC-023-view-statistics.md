# UC-023: View Statistics

## 1. Use Case Information

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-023 |
| **Use Case Name** | View Statistics |
| **Primary Actor** | Student (Learner) |
| **Secondary Actors** | None |
| **Priority** | Medium (P2) |
| **Complexity** | Medium |
| **Status** | MVP |

## 2. Brief Description

User views comprehensive learning statistics to track progress, identify strengths/weaknesses, and stay motivated. Statistics include deck performance, review accuracy, learning streaks, SRS box distribution, time spent studying, and historical trends.

## 3. Preconditions

- User is logged in
- User has reviewed at least some cards (for meaningful statistics)
- Statistics data exists in database

## 4. Postconditions

**Success**:
- User views current statistics dashboard
- Statistics refreshed from database
- Charts and graphs displayed
- User gains insights into learning progress

**Failure**:
- Statistics page fails to load
- Error message displayed
- User redirected to dashboard

## 5. Main Success Scenario

### Step 1: Navigate to Statistics
**Actor**: User clicks "Statistics" in main navigation menu

**System**:
- Loads user's statistics dashboard
- Queries aggregated data from database
- Displays loading indicator during data fetch

### Step 2: Display Overview Statistics
**System** shows main dashboard with key metrics:

**Overall Progress**:
- Total cards: 850
- Cards learned: 320 (38%)
- Cards in progress: 530 (62%)
- Total decks: 12
- Study streak: 15 days 🔥

**Today's Activity**:
- Cards reviewed: 45
- Time studied: 28 minutes
- Accuracy: 82% (Good/Easy)
- Cards due remaining: 5

**This Week**:
- Days studied: 5 / 7
- Total reviews: 210
- Time studied: 2h 15m
- Average accuracy: 78%

### Step 3: Display SRS Box Distribution
**System** shows box distribution chart:

```
Box Distribution (850 cards)
Box 1: ▓▓▓▓▓▓▓▓▓░ 180 cards (21%)  [Due tomorrow]
Box 2: ▓▓▓▓▓░░░░░ 120 cards (14%)  [Due in 3 days]
Box 3: ▓▓▓▓░░░░░░ 100 cards (12%)  [Due in 7 days]
Box 4: ▓▓▓░░░░░░░  80 cards (9%)   [Due in 14 days]
Box 5: ▓▓▓▓░░░░░░  90 cards (11%)  [Due in 30 days]
Box 6: ▓▓▓▓▓░░░░░ 140 cards (16%)  [Due in 60 days]
Box 7: ▓▓▓▓▓▓░░░░ 140 cards (16%)  [Due in 120 days]
```

### Step 4: Display Deck-Level Statistics
**System** shows per-deck breakdown:

| Deck | Cards | Mastered | In Progress | Due Today | Accuracy |
|------|-------|----------|-------------|-----------|----------|
| Academic Vocabulary | 250 | 95 (38%) | 155 (62%) | 20 | 85% |
| IELTS Speaking | 180 | 70 (39%) | 110 (61%) | 15 | 78% |
| Grammar Rules | 120 | 50 (42%) | 70 (58%) | 8 | 90% |
| Phrasal Verbs | 150 | 55 (37%) | 95 (63%) | 12 | 75% |
| ... | ... | ... | ... | ... | ... |

**Actor**: User clicks on "Academic Vocabulary" to see detailed stats

### Step 5: Display Detailed Deck Statistics
**System** shows drill-down for selected deck:

**Academic Vocabulary - Detailed Stats**:

**Box Distribution**:
- Box 1-3 (Learning): 120 cards (48%)
- Box 4-5 (Familiar): 70 cards (28%)
- Box 6-7 (Mastered): 60 cards (24%)

**Review History (Last 30 Days)**:
- Line chart showing daily reviews
- Peak: 25 cards on Day 12
- Average: 8 cards/day
- Total: 240 reviews

**Accuracy Breakdown**:
- Easy: 50 (21%)
- Good: 150 (62%)
- Hard: 30 (13%)
- Again: 10 (4%)

**Difficult Cards** (rated "Again" > 3 times):
1. ubiquitous - Failed 5 times
2. meticulous - Failed 4 times
3. articulate - Failed 4 times

**Actor**: User identifies weak cards and focuses review

### Step 6: Display Time-Based Statistics
**System** shows temporal trends:

**Study Time Heatmap (Last 90 Days)**:
```
Mon  ░▓▓░▓▓░░▓▓░▓▓
Tue  ▓▓▓░░▓▓▓░▓▓▓
Wed  ░▓▓▓▓░░▓▓▓░▓
Thu  ▓░▓▓▓▓░░▓▓▓▓
Fri  ▓▓░░▓▓▓▓░░▓▓
Sat  ░░░▓▓▓▓▓▓▓░░
Sun  ░░░░▓▓▓▓▓▓░░
```
Legend: ░ = 0-5 min, ▓ = 5-30 min, ▓▓ = 30+ min

**Monthly Progress**:
- January: 420 reviews, 6h 30m, 80% accuracy
- February: 380 reviews, 5h 45m, 78% accuracy
- March: 210 reviews, 3h 15m, 82% accuracy (current)

### Step 7: Display Streak and Achievements
**System** shows motivation metrics:

**Study Streak**: 🔥 15 days
- Current streak: 15 days
- Longest streak: 28 days (Jan 5 - Feb 1)
- Total study days: 65 / 90 (72%)

**Milestones**:
- ✅ 100 cards reviewed (Feb 10)
- ✅ 7-day streak (Feb 15)
- ✅ 500 cards reviewed (Mar 1)
- 🎯 1000 cards reviewed (Goal: 450 more)
- 🎯 30-day streak (Goal: 15 more days)

**Actor**: User feels motivated by progress visualization

### Step 8: Filter and Customize View
**Actor**: User adjusts filters:
- Time range: [Last 30 days ▼]
  - Last 7 days
  - Last 30 days
  - Last 90 days
  - All time
- Deck filter: [All decks ▼]
- Review type: [All types ▼] (SRS / Cram / Random)

**System**:
- Refreshes statistics based on filters
- Updates all charts and metrics
- Shows filtered data

## 6. Alternative Flows

### A1: No Statistics Available (New User)
**Trigger**: User has never reviewed any cards (Step 1)

**Flow**:
1. User navigates to Statistics
2. System detects 0 review history
3. System shows empty state:
   - "📊 No statistics yet"
   - "Start reviewing cards to see your progress here"
   - [Start First Review]
4. User clicks button and starts review

**End Use Case**

---

### A2: Export Statistics
**Trigger**: User wants to export data (Step 7)

**Flow**:
1. User clicks "Export Statistics"
2. System shows export options:
   - Format: CSV / JSON / PDF
   - Data: All stats / Selected deck / Time range
3. User selects: CSV, All stats, Last 90 days
4. System generates CSV file:
```csv
date,deck,reviews,accuracy,time_minutes
2025-01-01,Academic Vocabulary,12,83,9
2025-01-01,IELTS Speaking,8,75,6
...
```
5. File downloaded: `repeatwise-stats-2025-03-10.csv`

**Continue to**: User can view statistics

---

### A3: Compare Decks
**Trigger**: User selects multiple decks (Step 4)

**Flow**:
1. User checks: Academic Vocabulary, IELTS Speaking
2. User clicks "Compare Decks"
3. System shows side-by-side comparison:
   - Box distribution comparison
   - Accuracy comparison
   - Time spent comparison
   - Progress over time (line chart)
4. User identifies which deck needs more focus

**Continue to**: Step 7

---

### A4: View Difficult Cards Across All Decks
**Trigger**: User wants to see all weak cards (Step 5)

**Flow**:
1. User clicks "All Difficult Cards"
2. System queries cards with lapse_count > 3:
```sql
SELECT c.front, d.name, cbp.lapse_count
FROM cards c
JOIN decks d ON d.id = c.deck_id
JOIN card_box_position cbp ON cbp.card_id = c.id
WHERE cbp.user_id = :user_id AND cbp.lapse_count > 3
ORDER BY cbp.lapse_count DESC
LIMIT 50;
```
3. System displays top 50 difficult cards
4. User can:
   - Create custom review session with these cards
   - Edit cards to improve them
   - Flag for later review

**End Use Case**

---

### A5: Set Goals
**Trigger**: User wants to set learning goals (Step 7)

**Flow**:
1. User clicks "Set Goals"
2. System shows goal configuration:
   - Daily review goal: [20] cards
   - Daily study time: [15] minutes
   - Weekly study days: [5] days
   - Monthly new cards: [100] cards
3. User sets goals
4. System saves preferences
5. Dashboard shows progress toward goals:
   - Today: 12 / 20 cards (60%)
   - This week: 3 / 5 days (60%)

**Continue to**: Step 7

## 7. Special Requirements

### Performance
- Load statistics dashboard in < 1 second
- Chart rendering in < 500ms
- Filter updates in < 300ms
- Support up to 10,000 cards per user

### Usability
- Interactive charts (hover for details)
- Responsive design (mobile/desktop)
- Clear data visualization
- Export functionality
- Printable format

### Data Accuracy
- Real-time statistics (no caching lag)
- Accurate calculations
- Consistent across all views
- Historical data preserved

## 8. Business Rules

### BR-072: Statistics Calculation
- **Accuracy** = (Good + Easy) / Total Reviews × 100%
- **Mastered** = Cards in Box 6-7
- **In Progress** = Cards in Box 1-5
- **Study Streak** = Consecutive days with ≥ 1 review

### BR-073: Box Classification
- **Learning**: Box 1-3 (short intervals)
- **Familiar**: Box 4-5 (medium intervals)
- **Mastered**: Box 6-7 (long intervals)

### BR-074: Time Tracking
- Track time per review session
- Aggregate daily, weekly, monthly
- Include all review types (SRS, Cram, Random)
- Exclude pauses > 5 minutes (inactive)

### BR-075: Privacy and Data
- User can only view own statistics
- Statistics not shared publicly (MVP)
- Export includes only user's data
- GDPR-compliant data handling

## 9. Data Requirements

### Input
- user_id: UUID
- time_range: ENUM('7_days', '30_days', '90_days', 'all_time')
- deck_filter: UUID[] (optional)
- review_type_filter: ENUM('SRS', 'CRAM', 'RANDOM', 'ALL')

### Output
- Aggregated statistics object with all metrics

### Database Queries

**Overall Statistics**:
```sql
-- Total cards
SELECT COUNT(*) FROM cards c
JOIN decks d ON d.id = c.deck_id
WHERE d.user_id = :user_id AND c.deleted_at IS NULL;

-- Cards by box
SELECT cbp.current_box, COUNT(*) as count
FROM card_box_position cbp
WHERE cbp.user_id = :user_id
GROUP BY cbp.current_box;

-- Study streak
SELECT COUNT(*) FROM (
    SELECT DATE(reviewed_at) as review_date
    FROM reviews
    WHERE user_id = :user_id AND reviewed_at >= :streak_start_date
    GROUP BY DATE(reviewed_at)
) daily_reviews;
```

**Deck-Level Statistics**:
```sql
SELECT
    d.id,
    d.name,
    COUNT(c.id) as total_cards,
    COUNT(CASE WHEN cbp.current_box >= 6 THEN 1 END) as mastered,
    COUNT(CASE WHEN cbp.due_date <= CURRENT_DATE THEN 1 END) as due_today,
    ROUND(AVG(CASE WHEN r.rating IN ('GOOD', 'EASY') THEN 100 ELSE 0 END), 2) as accuracy
FROM decks d
LEFT JOIN cards c ON c.deck_id = d.id AND c.deleted_at IS NULL
LEFT JOIN card_box_position cbp ON cbp.card_id = c.id
LEFT JOIN reviews r ON r.card_id = c.id AND r.reviewed_at >= NOW() - INTERVAL '30 days'
WHERE d.user_id = :user_id AND d.deleted_at IS NULL
GROUP BY d.id, d.name;
```

**Review History**:
```sql
SELECT
    DATE(reviewed_at) as date,
    COUNT(*) as reviews,
    SUM(answer_time_seconds) / 60 as time_minutes,
    ROUND(AVG(CASE WHEN rating IN ('GOOD', 'EASY') THEN 100 ELSE 0 END), 2) as accuracy
FROM reviews
WHERE user_id = :user_id AND reviewed_at >= :start_date
GROUP BY DATE(reviewed_at)
ORDER BY date;
```

## 10. UI Mockup

### Statistics Dashboard
```
┌────────────────────────────────────────────────────────────┐
│  📊 Statistics                 [Last 30 days ▼] [Export]   │
├────────────────────────────────────────────────────────────┤
│                                                            │
│  ┌─────────────┬─────────────┬─────────────┬────────────┐│
│  │ Total Cards │ Learned     │ In Progress │ Streak     ││
│  │ 850         │ 320 (38%)   │ 530 (62%)   │ 15 days 🔥 ││
│  └─────────────┴─────────────┴─────────────┴────────────┘│
│                                                            │
│  Today's Activity                                          │
│  ────────────────────────────────────────────────────────│
│  Reviews: 45 • Time: 28 min • Accuracy: 82% • Due: 5      │
│                                                            │
│  Box Distribution                                          │
│  ────────────────────────────────────────────────────────│
│  Box 1 (Learning)  ▓▓▓▓▓▓▓▓▓░░░░░ 180 (21%)               │
│  Box 2             ▓▓▓▓▓░░░░░░░░░ 120 (14%)               │
│  Box 3             ▓▓▓▓░░░░░░░░░░ 100 (12%)               │
│  Box 4 (Familiar)  ▓▓▓░░░░░░░░░░░  80 (9%)                │
│  Box 5             ▓▓▓▓░░░░░░░░░░  90 (11%)               │
│  Box 6 (Mastered)  ▓▓▓▓▓░░░░░░░░░ 140 (16%)               │
│  Box 7             ▓▓▓▓▓▓░░░░░░░░ 140 (16%)               │
│                                                            │
│  Review History (Last 30 Days)                             │
│  ────────────────────────────────────────────────────────│
│  [Line chart showing daily review counts]                  │
│                                                            │
│  Top Decks by Activity                                     │
│  ────────────────────────────────────────────────────────│
│  1. Academic Vocabulary  250 cards • 85% accuracy          │
│  2. IELTS Speaking       180 cards • 78% accuracy          │
│  3. Grammar Rules        120 cards • 90% accuracy          │
│                                                            │
│  [View All Decks] [Difficult Cards] [Set Goals]            │
└────────────────────────────────────────────────────────────┘
```

## 11. Testing Scenarios

### Happy Path
1. User navigates to Statistics
2. Dashboard loads with all metrics
3. User views box distribution
4. User clicks on a deck for details
5. User filters by time range
6. Statistics update correctly

### Alternative Scenarios
1. New user (no stats) → Empty state shown
2. Export statistics → CSV downloaded
3. Compare decks → Side-by-side view
4. View difficult cards → Top 50 shown
5. Set goals → Progress tracked

### Edge Cases
1. User with 10,000+ cards → Statistics load efficiently
2. User with 0 reviews today → Shows 0, not error
3. Longest streak = 365 days → Displays correctly
4. Accuracy = 100% (all Easy/Good) → Shows 100%
5. Very old data (2+ years) → All-time filter works

### Error Cases
1. Database query timeout → Error message, retry option
2. Network failure → Cached data shown (if available)
3. Invalid date range → Default to 30 days

## 12. Performance Benchmarks

| Operation | Target | Max |
|-----------|--------|-----|
| Load dashboard | < 500ms | 1s |
| Filter update | < 200ms | 300ms |
| Chart rendering | < 300ms | 500ms |
| Export CSV | < 1s | 2s |
| Query 10k cards | < 500ms | 1s |

## 13. Related Use Cases

- **UC-019**: Review Cards with SRS (generates stats)
- **UC-020**: Cram Mode Review (tracked separately)
- **UC-021**: Random Mode Review (tracked separately)
- **UC-022**: Configure SRS Settings

## 14. Acceptance Criteria

- [ ] User can view overall statistics dashboard
- [ ] Displays total cards, learned, in progress
- [ ] Shows study streak and milestones
- [ ] Box distribution chart accurate
- [ ] Deck-level statistics table shown
- [ ] Drill-down to individual deck stats
- [ ] Review history chart (last 30 days)
- [ ] Accuracy breakdown displayed
- [ ] Difficult cards identified
- [ ] Time-based heatmap shown
- [ ] Filter by time range works
- [ ] Filter by deck works
- [ ] Export to CSV functional
- [ ] Empty state for new users
- [ ] Performance within benchmarks

---

**Version**: 1.0
**Last Updated**: 2025-01
