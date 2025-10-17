# UC-022: Configure SRS Settings

## 1. Use Case Information

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-022 |
| **Use Case Name** | Configure SRS Settings |
| **Primary Actor** | Student (Learner) |
| **Secondary Actors** | None |
| **Priority** | Medium (P1) |
| **Complexity** | Medium |
| **Status** | MVP |

## 2. Brief Description

User customizes their Spaced Repetition System (SRS) settings to personalize their learning experience. Settings include review order, forgotten card actions, daily limits, notification preferences, and box intervals.

## 3. Preconditions

- User is logged in
- User has default SRS settings created during registration
- System has loaded current settings

## 4. Postconditions

**Success**:
- SRS settings updated in database
- New settings applied to future review sessions
- Existing cards and schedules unchanged
- Settings synced across devices
- User notified of successful update

**Failure**:
- Settings remain unchanged
- Error message displayed
- User remains on settings page

## 5. Main Success Scenario

### Step 1: Navigate to Settings
**Actor**: User clicks "Settings" menu and selects "SRS Settings"

**System**:
- Loads current SRS settings from database:
```sql
SELECT * FROM srs_settings WHERE user_id = :user_id;
```
- Displays settings form with current values
- Shows explanations for each setting
- Groups settings into logical sections

### Step 2: Review Current Settings
**System** displays current configuration:

**Review Settings**:
- Total Boxes: 7 (Fixed in MVP)
- Review Order: Random (Dropdown: Ascending / Descending / Random)
- Show Answer Time: True (Toggle)

**Forgotten Card Actions**:
- Forgotten Card Action: Move to Box 1 (Dropdown: Move to Box 1 / Move Down N Boxes / Stay in Box)
- Move Down By: 2 boxes (Enabled if "Move Down N Boxes" selected)

**Daily Limits**:
- New Cards Per Day: 20 (Slider: 0-100)
- Max Reviews Per Day: 200 (Slider: 0-500)

**Notifications**:
- Notification Enabled: True (Toggle)
- Notification Time: 09:00 (Time picker)

### Step 3: Modify Settings
**Actor**: User makes changes:
- Review Order: Random → **Ascending** (easier cards first)
- Forgotten Card Action: Move to Box 1 → **Move Down N Boxes**
- Move Down By: 2 → **3 boxes** (more forgiving)
- New Cards Per Day: 20 → **10** (less overwhelming)
- Max Reviews Per Day: 200 → **150** (more realistic)

**System**:
- Validates inputs in real-time:
  - Move Down By: 1-6 boxes (must be < total_boxes)
  - New Cards Per Day: 0-100
  - Max Reviews Per Day: 0-500
- Shows live preview of impact:
  - "With 10 new cards/day, you'll learn 300 cards/month"
  - "150 reviews/day means ~20 minutes of daily study"

### Step 4: Submit Changes
**Actor**: User clicks "Save Changes"

**System**:
1. Validates all inputs (server-side):
   - Review order is valid enum value
   - Forgotten card action is valid enum value
   - Move down by: 1 ≤ value < total_boxes
   - New cards per day: 0 ≤ value ≤ 100
   - Max reviews per day: 0 ≤ value ≤ 500
   - Notification time: valid time format (HH:MM)

2. Updates settings in database:
```sql
UPDATE srs_settings SET
    review_order = 'ASCENDING',
    forgotten_card_action = 'MOVE_DOWN_N_BOXES',
    move_down_by = 3,
    new_cards_per_day = 10,
    max_reviews_per_day = 150,
    updated_at = NOW()
WHERE user_id = :user_id;
```

3. Invalidates cached settings (if any)
4. Logs event: "SRS settings updated by user {email}"

### Step 5: Confirm Changes
**System**:
- Shows success toast: "SRS settings saved successfully"
- Displays updated values in form
- Shows informational message:
  - "✓ Settings applied to future review sessions"
  - "ℹ️ Existing card schedules unchanged"

**Actor**: User sees confirmation and can:
- Start review session with new settings
- Continue editing settings
- Return to dashboard

## 6. Alternative Flows

### A1: Reset to Defaults
**Trigger**: User clicks "Reset to Defaults" button (Step 3)

**Flow**:
1. System shows confirmation dialog:
   - "Reset all SRS settings to defaults?"
   - Shows default values:
     - Review Order: Random
     - Forgotten Card Action: Move to Box 1
     - New Cards Per Day: 20
     - Max Reviews Per Day: 200
     - Notifications: Enabled, 09:00
2. User confirms
3. System resets settings to defaults:
```sql
UPDATE srs_settings SET
    review_order = 'RANDOM',
    forgotten_card_action = 'MOVE_TO_BOX_1',
    move_down_by = NULL,
    new_cards_per_day = 20,
    max_reviews_per_day = 200,
    notification_enabled = TRUE,
    notification_time = '09:00',
    updated_at = NOW()
WHERE user_id = :user_id;
```
4. Form updates with default values
5. Success toast shown

**Continue to**: Step 5

---

### A2: Invalid Move Down By Value
**Trigger**: User enters move_down_by ≥ total_boxes (Step 3)

**Flow**:
1. User selects "Move Down N Boxes"
2. User sets "Move Down By" to 7 boxes
3. System validates: 7 ≥ 7 (total_boxes) → Invalid!
4. System shows error: "Cannot move down 7 boxes (total boxes: 7). Choose 1-6."
5. System disables Save button
6. User corrects to 3 boxes
7. Error clears, Save button enabled

**Return to**: Step 3

---

### A3: Zero New Cards Per Day (Review Only Mode)
**Trigger**: User sets new_cards_per_day = 0 (Step 3)

**Flow**:
1. User drags slider to 0
2. System shows warning: "⚠️ Review only mode: No new cards will be introduced"
3. System confirms: "You can still review existing cards"
4. User saves settings
5. Future review sessions show only due cards, no new cards

**Continue to**: Step 4

---

### A4: Disable Notifications
**Trigger**: User toggles "Notification Enabled" to False (Step 3)

**Flow**:
1. User toggles notification switch to OFF
2. System grays out "Notification Time" picker (disabled)
3. User saves settings
4. System updates:
```sql
UPDATE srs_settings SET
    notification_enabled = FALSE,
    updated_at = NOW()
WHERE user_id = :user_id;
```
5. User no longer receives daily review reminders

**Continue to**: Step 4

---

### A5: Extreme Daily Limits
**Trigger**: User sets very high daily limits (Step 3)

**Flow**:
1. User sets max_reviews_per_day = 500
2. System shows warning: "⚠️ 500 reviews/day may take 1+ hour"
3. System suggests: "Recommended: 100-200 reviews/day"
4. User can still save (not blocked)
5. Settings saved with warning acknowledged

**Continue to**: Step 4

---

### A6: Settings Conflict - Concurrent Update
**Trigger**: Another session updates settings (Step 4)

**Flow**:
1. User on device A modifies settings
2. User on device B modifies settings (concurrent)
3. Device A submits first → Success
4. Device B submits:
   - Optimistic locking check fails (updated_at changed)
   - OR last-write-wins (MVP, no locking)
5. Device B shows warning: "Settings were updated elsewhere. Refreshing..."
6. System reloads settings from database
7. User reviews merged settings

**End Use Case**

## 7. Special Requirements

### Performance
- Settings load in < 100ms
- Settings update in < 200ms
- Form validation in real-time (< 50ms)

### Usability
- Group related settings into sections
- Show explanations for each setting
- Provide live preview of impact
- Offer "Reset to Defaults" option
- Validate inputs before submit
- Show warnings for extreme values

### Persistence
- Settings synced across all devices
- Changes applied immediately after save
- Existing card schedules unaffected

## 8. Business Rules

### BR-043: Review Order Strategies
- **ASCENDING**: Cards sorted by current_box ASC, due_date ASC (easier first)
- **DESCENDING**: Cards sorted by current_box DESC, due_date ASC (harder first)
- **RANDOM**: Cards shuffled (no predictability)
- Applied at review session start, not per-card

### BR-044: Forgotten Card Actions
- **MOVE_TO_BOX_1**: Reset to beginning (strictest)
- **MOVE_DOWN_N_BOXES**: Demote by N boxes (configurable forgiveness)
- **STAY_IN_BOX**: Keep in current box, halve interval (most forgiving)
- Applied when user rates card "Again" (<1 min)

### BR-045: Daily Limits
- **new_cards_per_day**: 0-100, controls how many new cards enter Box 1
- **max_reviews_per_day**: 0-500, limits total review session size
- 0 is valid (review-only or new-cards-only mode)
- Limits enforced at session start, not mid-session

### BR-046: Notification Settings
- **notification_enabled**: Boolean, master toggle
- **notification_time**: HH:MM format, local timezone
- Notification sent daily at specified time (Future: requires push service)
- Content: "You have X cards due for review today"

### BR-047: Box Configuration (Fixed in MVP)
- total_boxes = 7 (not configurable in MVP)
- Box intervals: [1, 3, 7, 14, 30, 60, 120] days (fixed)
- Future: Allow custom box counts and intervals

## 9. Data Requirements

### Input
- review_order: ENUM('ASCENDING', 'DESCENDING', 'RANDOM')
- forgotten_card_action: ENUM('MOVE_TO_BOX_1', 'MOVE_DOWN_N_BOXES', 'STAY_IN_BOX')
- move_down_by: INTEGER (1 to total_boxes-1), nullable
- new_cards_per_day: INTEGER (0-100)
- max_reviews_per_day: INTEGER (0-500)
- notification_enabled: BOOLEAN
- notification_time: TIME

### Output
- Updated srs_settings object

### Database Changes
```sql
UPDATE srs_settings SET
    review_order = :review_order,
    forgotten_card_action = :forgotten_card_action,
    move_down_by = :move_down_by, -- NULL if not using MOVE_DOWN_N_BOXES
    new_cards_per_day = :new_cards_per_day,
    max_reviews_per_day = :max_reviews_per_day,
    notification_enabled = :notification_enabled,
    notification_time = :notification_time,
    updated_at = NOW()
WHERE user_id = :user_id;
```

### Validation Rules
```java
// Review Order
if (!Arrays.asList("ASCENDING", "DESCENDING", "RANDOM").contains(reviewOrder)) {
    throw new ValidationException("Invalid review order");
}

// Forgotten Card Action
if (!Arrays.asList("MOVE_TO_BOX_1", "MOVE_DOWN_N_BOXES", "STAY_IN_BOX").contains(forgottenCardAction)) {
    throw new ValidationException("Invalid forgotten card action");
}

// Move Down By
if (forgottenCardAction.equals("MOVE_DOWN_N_BOXES")) {
    if (moveDownBy == null || moveDownBy < 1 || moveDownBy >= totalBoxes) {
        throw new ValidationException("Move down by must be 1-" + (totalBoxes - 1));
    }
}

// Daily Limits
if (newCardsPerDay < 0 || newCardsPerDay > 100) {
    throw new ValidationException("New cards per day must be 0-100");
}
if (maxReviewsPerDay < 0 || maxReviewsPerDay > 500) {
    throw new ValidationException("Max reviews per day must be 0-500");
}

// Notification Time
if (!notificationTime.matches("^([01]\\d|2[0-3]):([0-5]\\d)$")) {
    throw new ValidationException("Invalid time format (use HH:MM)");
}
```

## 10. UI Mockup

### SRS Settings Page
```
┌────────────────────────────────────────┐
│  ⚙️ SRS Settings                        │
├────────────────────────────────────────┤
│                                        │
│  📚 Review Settings                    │
│  ─────────────────────────────────────│
│                                        │
│  Review Order                          │
│  [Ascending ▼]                         │
│  ℹ️ Easier cards (Box 1-2) shown first │
│                                        │
│  Total Boxes: 7 (fixed in MVP)         │
│  Intervals: 1, 3, 7, 14, 30, 60, 120 days │
│                                        │
│  ─────────────────────────────────────│
│                                        │
│  😞 Forgotten Card Actions             │
│  ─────────────────────────────────────│
│                                        │
│  When I rate "Again" (<1 min):         │
│  [Move Down N Boxes ▼]                 │
│                                        │
│  Move Down By: 3 boxes                 │
│  [━━━━●━━━━━] (1-6)                    │
│  ℹ️ Card moves back 3 boxes on failure │
│                                        │
│  ─────────────────────────────────────│
│                                        │
│  📊 Daily Limits                       │
│  ─────────────────────────────────────│
│                                        │
│  New Cards Per Day: 10                 │
│  [━━●━━━━━━━] (0-100)                  │
│  ℹ️ ~300 new cards per month           │
│                                        │
│  Max Reviews Per Day: 150              │
│  [━━━━●━━━━━] (0-500)                  │
│  ℹ️ ~20 minutes of study per day       │
│                                        │
│  ─────────────────────────────────────│
│                                        │
│  🔔 Notifications                      │
│  ─────────────────────────────────────│
│                                        │
│  ☑ Enable daily reminders              │
│  Remind me at: [09:00]                 │
│  ℹ️ "You have X cards due for review"  │
│                                        │
│  ─────────────────────────────────────│
│                                        │
│  [Reset to Defaults]  [Save Changes]   │
└────────────────────────────────────────┘
```

### Success Toast
```
┌────────────────────────────────────────┐
│  ✓ SRS Settings Saved                  │
│                                        │
│  Changes applied to future sessions    │
│  Existing card schedules unchanged     │
│                                        │
│  [Dismiss]                             │
└────────────────────────────────────────┘
```

## 11. Testing Scenarios

### Happy Path
1. Load SRS settings page
2. Change review order to Ascending
3. Change forgotten card action to Move Down 3 Boxes
4. Reduce new cards to 10/day
5. Save changes
6. Verify settings updated in database
7. Start review session, verify ascending order applied

### Alternative Flows
1. Reset to defaults → All settings reverted
2. Disable notifications → notification_enabled = FALSE
3. Set new cards to 0 → Review-only mode

### Edge Cases
1. Set move_down_by to 6 (max) → Should succeed
2. Set move_down_by to 7 (= total_boxes) → Error
3. Set max_reviews to 0 → Valid, no reviews shown
4. Set new_cards to 100 → Warning, but allowed
5. Concurrent updates → Last-write-wins (MVP)

### Error Cases
1. Invalid review order → Validation error
2. move_down_by > total_boxes → Error: "Choose 1-6"
3. new_cards_per_day > 100 → Error
4. Invalid notification time "25:00" → Error

## 12. Performance Benchmarks

| Operation | Target | Max |
|-----------|--------|-----|
| Load settings | < 50ms | 100ms |
| Update settings | < 100ms | 200ms |
| Real-time validation | < 10ms | 50ms |

## 13. Related Use Cases

- **UC-001**: User Registration (creates default settings)
- **UC-019**: Review Cards with SRS (uses settings)
- **UC-003**: User Profile Management

## 14. Acceptance Criteria

- [ ] User can change review order (Ascending/Descending/Random)
- [ ] User can configure forgotten card action
- [ ] User can set move_down_by (1 to total_boxes-1)
- [ ] User can set daily limits (0-100 new cards, 0-500 reviews)
- [ ] User can enable/disable notifications
- [ ] User can set notification time (HH:MM)
- [ ] Reset to defaults works correctly
- [ ] Validation prevents invalid values
- [ ] Settings save in < 200ms
- [ ] Changes applied to future sessions immediately
- [ ] Existing card schedules unchanged
- [ ] Settings synced across devices
- [ ] Warnings shown for extreme values
- [ ] Live preview shows impact of changes

---

**Version**: 1.0
**Last Updated**: 2025-01
