# UC-024: Manage Notifications

## 1. Use Case Information

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-024 |
| **Use Case Name** | Manage Notifications |
| **Primary Actor** | Student (Learner) |
| **Secondary Actors** | Notification Service (Email/Push) |
| **Priority** | Medium (P2) |
| **Complexity** | Low-Medium |
| **Status** | MVP (Basic), Future (Advanced) |

## 2. Brief Description

User configures notification preferences to receive reminders about due cards, study streaks, and achievements. Basic MVP includes daily review reminders. Future versions will support push notifications, email, custom schedules, and achievement notifications.

## 3. Preconditions

- User is logged in
- User has created an account with email
- Notification service is operational
- User has granted notification permissions (for push)

## 4. Postconditions

**Success**:
- Notification preferences saved
- User receives notifications according to settings
- Email/push notifications sent at configured times
- User can modify settings anytime

**Failure**:
- Preferences not saved
- Error message displayed
- User remains on settings page

## 5. Main Success Scenario

### Step 1: Navigate to Notification Settings
**Actor**: User clicks "Settings" → "Notifications"

**System**:
- Loads current notification preferences from database:
```sql
SELECT * FROM notification_settings WHERE user_id = :user_id;
```
- Displays notification configuration form
- Shows current settings
- Shows notification preview

### Step 2: View Current Settings
**System** displays current configuration:

**Daily Review Reminders** (MVP):
- Enabled: ☑ (Toggle)
- Time: [09:00] (Time picker)
- Days: ☑ Mon ☑ Tue ☑ Wed ☑ Thu ☑ Fri ☐ Sat ☐ Sun
- Method: Email (MVP) / Push (Future)
- Preview: "You have 20 cards due for review today"

**Streak Reminders** (Future):
- Enabled: ☐
- When: About to break streak (no review today)
- Time: [20:00]

**Achievement Notifications** (Future):
- Enabled: ☐
- Types: Milestones, Streaks, Mastery

### Step 3: Modify Settings
**Actor**: User makes changes:
- Daily reminders: Enabled → Enabled (keep)
- Time: 09:00 → **19:00** (evening instead)
- Days: Mon-Fri → **Mon-Sun** (every day)

**System**:
- Validates inputs:
  - Time in HH:MM format
  - At least one day selected
  - Valid email address (if email enabled)
- Shows live preview:
  - "You'll receive reminders daily at 7:00 PM"
  - "Next reminder: Today at 7:00 PM"

### Step 4: Test Notification (Optional)
**Actor**: User clicks "Send Test Notification"

**System**:
1. Sends test notification immediately:
   - Email subject: "[RepeatWise] Test Notification"
   - Body: "This is a test notification. You have 20 cards due for review."
2. Shows confirmation: "Test notification sent to your-email@example.com"
3. User checks email/device for notification

### Step 5: Save Settings
**Actor**: User clicks "Save Changes"

**System**:
1. Validates all settings
2. Updates database:
```sql
UPDATE notification_settings SET
    daily_reminder_enabled = TRUE,
    daily_reminder_time = '19:00',
    daily_reminder_days = '{MON,TUE,WED,THU,FRI,SAT,SUN}',
    notification_method = 'EMAIL',
    updated_at = NOW()
WHERE user_id = :user_id;
```
3. Schedules next notification:
   - Calculates next send time: Today 19:00 (if before) or Tomorrow 19:00
   - Creates scheduled job in notification queue
4. Shows success toast: "Notification settings saved. Next reminder: Today at 7:00 PM"

### Step 6: Receive Daily Notification
**System** (at scheduled time - 19:00):
1. Background job triggers at 19:00
2. Queries users with notifications enabled at this time:
```sql
SELECT u.id, u.email, u.name
FROM users u
JOIN notification_settings ns ON ns.user_id = u.id
WHERE ns.daily_reminder_enabled = TRUE
  AND ns.daily_reminder_time = '19:00'
  AND EXTRACT(DOW FROM NOW()) = ANY(ns.daily_reminder_days);
```
3. For each user, calculates due cards:
```sql
SELECT COUNT(*) FROM card_box_position cbp
WHERE cbp.user_id = :user_id AND cbp.due_date <= CURRENT_DATE;
```
4. Sends notification:
   - **Email**:
     - Subject: "📚 [RepeatWise] You have 20 cards due for review"
     - Body: "Hi John, don't forget to review your 20 due cards today! [Start Review]"
     - Link: https://repeatwise.app/review
   - **Push** (Future):
     - Title: "📚 RepeatWise Reminder"
     - Body: "You have 20 cards due for review"
     - Action: Open app → Review screen
5. Logs notification sent:
```sql
INSERT INTO notification_logs (user_id, type, sent_at, status)
VALUES (:user_id, 'DAILY_REMINDER', NOW(), 'SENT');
```

**Actor**: User receives notification, clicks link, and starts reviewing

## 6. Alternative Flows

### A1: Disable All Notifications
**Trigger**: User wants to stop all notifications (Step 3)

**Flow**:
1. User unchecks "Daily review reminders"
2. System shows confirmation: "Disable daily reminders?"
3. User confirms
4. System updates:
```sql
UPDATE notification_settings SET
    daily_reminder_enabled = FALSE
WHERE user_id = :user_id;
```
5. System cancels all scheduled notifications for user
6. Toast: "Daily reminders disabled"

**Continue to**: Step 5

---

### A2: No Cards Due - Skip Notification
**Trigger**: User has 0 cards due (Step 6)

**Flow**:
1. System calculates due cards: 0
2. System skips sending notification (no need to remind)
3. Logs: "Notification skipped: No cards due"
4. User receives no notification today
5. Next notification: Tomorrow 19:00 (if cards due)

**End Use Case**

---

### A3: Email Delivery Failure
**Trigger**: Email fails to send (Step 6)

**Flow**:
1. System attempts to send email
2. SMTP server returns error: "Mailbox not found"
3. System logs failure:
```sql
INSERT INTO notification_logs (user_id, type, sent_at, status, error)
VALUES (:user_id, 'DAILY_REMINDER', NOW(), 'FAILED', 'Mailbox not found');
```
4. System retries after 1 hour (max 3 attempts)
5. If all retries fail:
   - Flag user's email as invalid
   - Show warning in app: "Email notifications failing. Please update your email."

**End Use Case**

---

### A4: Notification Frequency Too High (Future)
**Trigger**: User sets multiple notifications (Alternative Step 3)

**Flow**:
1. User enables:
   - Daily reminders: 09:00
   - Streak reminders: 20:00
   - Achievement notifications: Instant
2. System calculates potential frequency
3. System shows info: "You may receive up to 3 notifications per day"
4. User acknowledges
5. Settings saved

**Continue to**: Step 5

---

### A5: Change Email Address
**Trigger**: User wants to change notification email (Alternative Step 3)

**Flow**:
1. User clicks "Change Email"
2. System shows input: "Notification email: [new-email@example.com]"
3. User enters new email
4. System sends verification email to new address
5. User clicks verification link
6. Email confirmed
7. Notifications sent to new email

**Continue to**: Step 5

---

### A6: Timezone Change
**Trigger**: User travels to different timezone (Alternative scenario)

**Flow**:
1. System detects timezone change (from browser/device)
2. System converts notification time to new timezone
3. Example: 09:00 PST → 12:00 EST
4. System shows notification: "Timezone changed. Your 9:00 AM reminder is now 12:00 PM local time. [Adjust Time]"
5. User can adjust time to maintain original local time

**End Use Case**

## 7. Special Requirements

### Performance
- Save settings in < 200ms
- Send notification within 1 minute of scheduled time
- Process 10,000+ users in batch job efficiently

### Reliability
- Guaranteed delivery (at-least-once)
- Retry on failure (3 attempts)
- Log all notification events
- Handle email bounces gracefully

### Usability
- Clear explanation of each setting
- Test notification feature
- Preview of notification content
- Easy to enable/disable

### Privacy
- User controls all notifications
- No spam (only requested notifications)
- Unsubscribe link in all emails
- Respect do-not-disturb hours (Future)

## 8. Business Rules

### BR-076: Notification Types (MVP)
- **Daily Review Reminder**: Sent at user-configured time if cards are due
- **Streak Reminder** (Future): Sent if user hasn't reviewed today and has active streak
- **Achievement** (Future): Sent when milestone reached (100 cards, 30-day streak, etc.)

### BR-077: Notification Timing
- User sets time in HH:MM format (local timezone)
- System converts to UTC for storage
- Sends within ±5 minutes of scheduled time
- Skip if no cards due (daily reminders)

### BR-078: Notification Methods
- **Email** (MVP): Standard, works for all users
- **Push** (Future): Requires app installation and permission
- **SMS** (Future): Premium feature, requires phone number

### BR-079: Frequency Limits
- Max 1 daily reminder per day
- Achievement notifications: Max 5 per day
- Streak reminders: Max 1 per day
- Total: Max 10 notifications per day (hard limit)

### BR-080: Opt-Out
- User can disable any notification type
- Global disable option available
- Unsubscribe link in all emails
- Preference persists across sessions

## 9. Data Requirements

### Input
- daily_reminder_enabled: BOOLEAN
- daily_reminder_time: TIME (HH:MM)
- daily_reminder_days: ENUM[] (MON, TUE, WED, THU, FRI, SAT, SUN)
- notification_method: ENUM ('EMAIL', 'PUSH', 'SMS')
- notification_email: VARCHAR(255) (optional, defaults to user email)

### Output
- Updated notification settings
- Scheduled notification jobs

### Database Changes

**Update Settings**:
```sql
UPDATE notification_settings SET
    daily_reminder_enabled = :enabled,
    daily_reminder_time = :time,
    daily_reminder_days = :days,
    notification_method = :method,
    notification_email = :email,
    updated_at = NOW()
WHERE user_id = :user_id;
```

**Log Notification Sent**:
```sql
INSERT INTO notification_logs (
    user_id, notification_type, sent_at,
    status, error_message, metadata
)
VALUES (
    :user_id, 'DAILY_REMINDER', NOW(),
    :status, :error, :metadata
);
```

**Query Users for Notification Batch**:
```sql
-- Find users to notify at current time
SELECT u.id, u.email, u.name, u.timezone
FROM users u
JOIN notification_settings ns ON ns.user_id = u.id
WHERE ns.daily_reminder_enabled = TRUE
  AND ns.daily_reminder_time = EXTRACT(HOUR FROM NOW() AT TIME ZONE u.timezone) || ':' || EXTRACT(MINUTE FROM NOW() AT TIME ZONE u.timezone)
  AND EXTRACT(DOW FROM NOW()) = ANY(ns.daily_reminder_days);
```

## 10. UI Mockup

### Notification Settings Page
```
┌────────────────────────────────────────┐
│  🔔 Notification Settings              │
├────────────────────────────────────────┤
│                                        │
│  📅 Daily Review Reminders             │
│  ─────────────────────────────────────│
│                                        │
│  ☑ Enable daily reminders              │
│                                        │
│  Remind me at: [19:00]                 │
│                                        │
│  Days:                                 │
│  ☑ Mon  ☑ Tue  ☑ Wed  ☑ Thu  ☑ Fri    │
│  ☑ Sat  ☑ Sun                          │
│                                        │
│  Send via: [Email ▼]                   │
│  Email: john@example.com [Change]      │
│                                        │
│  Preview:                              │
│  ┌──────────────────────────────────┐ │
│  │ 📚 You have 20 cards due         │ │
│  │ Don't forget to review today!    │ │
│  │ [Start Review]                   │ │
│  └──────────────────────────────────┘ │
│                                        │
│  [Send Test Notification]              │
│                                        │
│  ─────────────────────────────────────│
│                                        │
│  🔥 Streak Reminders (Coming Soon)     │
│  ☐ Remind me if I'm about to break    │
│     my study streak                    │
│                                        │
│  ─────────────────────────────────────│
│                                        │
│  🎉 Achievement Notifications (Future) │
│  ☐ Notify me of milestones & achievements│
│                                        │
│  ─────────────────────────────────────│
│                                        │
│  Next reminder: Today at 7:00 PM       │
│                                        │
│  [Cancel]  [Save Changes]              │
└────────────────────────────────────────┘
```

### Email Notification Template
```
Subject: 📚 [RepeatWise] You have 20 cards due for review

Hi John,

You have 20 cards due for review today.

Keep up your 15-day study streak! 🔥

┌─────────────────────────────┐
│  [Start Review]             │
└─────────────────────────────┘

Decks with due cards:
• Academic Vocabulary: 12 cards
• IELTS Speaking: 8 cards

Happy learning!
- RepeatWise Team

────────────────────────────────
Not interested in these reminders?
[Manage notification settings] | [Unsubscribe]
```

## 11. Testing Scenarios

### Happy Path
1. User enables daily reminders at 19:00
2. Settings saved successfully
3. At 19:00, system sends email
4. User receives notification with correct due count
5. User clicks link and starts review

### Alternative Scenarios
1. Disable notifications → No emails sent
2. No cards due → Notification skipped
3. Email failure → Retry mechanism works
4. Test notification → Received immediately

### Edge Cases
1. Set time to 00:00 (midnight) → Works correctly
2. Set time to 23:59 → Works correctly
3. Select 0 days → Error: "Select at least one day"
4. User has 0 cards → Notification still sent (can add cards)
5. User in different timezone → Time converted correctly

### Error Cases
1. Invalid email format → Validation error
2. SMTP server down → Retry, then fail gracefully
3. User email bounces → Flag account, show warning
4. Database unavailable → Settings not saved, error shown

## 12. Performance Benchmarks

| Operation | Target | Max |
|-----------|--------|-----|
| Save settings | < 100ms | 200ms |
| Send single email | < 2s | 5s |
| Process 1000 users (batch) | < 30s | 60s |
| Process 10,000 users | < 5min | 10min |

## 13. Related Use Cases

- **UC-002**: User Login (sets timezone)
- **UC-004**: User Profile Management (email address)
- **UC-019**: Review Cards with SRS (generates due cards)
- **UC-022**: Configure SRS Settings (notification time stored here)
- **UC-023**: View Statistics (streak data for reminders)

## 14. Acceptance Criteria

- [ ] User can enable/disable daily reminders
- [ ] User can set notification time (HH:MM)
- [ ] User can select days of week
- [ ] Settings saved to database correctly
- [ ] Email sent at configured time
- [ ] Email contains correct due card count
- [ ] Email has link to start review
- [ ] Test notification works immediately
- [ ] No notification sent if 0 cards due
- [ ] Timezone conversion works correctly
- [ ] Retry on email failure (3 attempts)
- [ ] Unsubscribe link in all emails
- [ ] User can change notification email
- [ ] Batch processing handles 10k+ users
- [ ] Performance within benchmarks

---

**Version**: 1.0
**Last Updated**: 2025-01
