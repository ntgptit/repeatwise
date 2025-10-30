# UC-028: Configure SRS Settings

## 1. Brief Description

User configures personal Spaced Repetition System (SRS) settings including total boxes, review order, daily card limits, notification preferences, and forgotten card action to customize their learning experience.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** Settings Service, Notification Service

## 3. Preconditions

- User is authenticated with valid access token
- SRS settings exist for the user (initialized during registration with default values)
- User has access to Settings page

## 4. Postconditions

### Success Postconditions

- SRS settings updated in `srs_settings` table
- `updated_at` timestamp refreshed
- Settings immediately apply to subsequent review sessions
- Success message displayed to user
- Notification schedule updated (if notification settings changed)
- User remains on settings page

### Failure Postconditions

- No changes saved to database
- Error message displayed
- User remains on settings page
- Original settings preserved

## 5. Main Success Scenario (Basic Flow)

1. User navigates to Settings > SRS Configuration page
2. System fetches current SRS settings from database:
   ```sql
   SELECT total_boxes, review_order, new_cards_per_day, 
          max_reviews_per_day, forgotten_card_action, move_down_boxes,
          notification_enabled, notification_time, updated_at
   FROM srs_settings
   WHERE user_id = ?
   ```
3. System displays settings form with current values pre-populated:
   - Total Boxes: 7 (range 3-10, number input)
   - Review Order: RANDOM (dropdown: DUE_DATE_ASC, RANDOM, CURRENT_BOX_ASC)
   - New Cards Per Day: 20 (range 1-100, number input)
   - Max Reviews Per Day: 200 (range 10-500, number input)
   - Forgotten Card Action: MOVE_TO_BOX_1 (dropdown: MOVE_TO_BOX_1, MOVE_DOWN_N_BOXES, REPEAT_IN_SESSION)
   - If MOVE_DOWN_N_BOXES selected: Move Down Boxes: 1 (range 1-3, number input)
   - Notification Enabled: true (checkbox)
   - Notification Time: "09:00" (time picker, format HH:mm)
4. User reviews current settings
5. User updates desired fields:
   - Changes Total Boxes from 7 to 5
   - Changes New Cards Per Day from 20 to 30
   - Changes Review Order from RANDOM to DUE_DATE_ASC
   - Changes Forgotten Card Action to MOVE_DOWN_N_BOXES
   - Sets Move Down Boxes to 2
6. User clicks "Save Settings" button
7. Client validates all inputs:
   - total_boxes: 3 ≤ value ≤ 10
   - new_cards_per_day: 1 ≤ value ≤ 100
   - max_reviews_per_day: 10 ≤ value ≤ 500
   - notification_time: valid HH:mm format (00:00 - 23:59)
   - review_order: valid enum value
   - forgotten_card_action: valid enum value
   - move_down_boxes: 1 ≤ value ≤ 3 (required if forgotten_card_action = MOVE_DOWN_N_BOXES)
8. Client sends PATCH request to update endpoint:
   ```json
   {
     "totalBoxes": 5,
     "reviewOrder": "DUE_DATE_ASC",
     "newCardsPerDay": 30,
     "maxReviewsPerDay": 200,
     "forgottenCardAction": "MOVE_DOWN_N_BOXES",
     "moveDownBoxes": 2,
     "notificationEnabled": true,
     "notificationTime": "09:00"
   }
   ```
9. Backend validates all inputs again (server-side validation)
10. System starts database transaction
11. System updates `srs_settings` table:
    ```sql
    UPDATE srs_settings
    SET total_boxes = ?,
        review_order = ?,
        new_cards_per_day = ?,
        max_reviews_per_day = ?,
        forgotten_card_action = ?,
        move_down_boxes = ?,
        notification_enabled = ?,
        notification_time = ?,
        updated_at = CURRENT_TIMESTAMP
    WHERE user_id = ?
    ```
12. If notification settings changed, system updates notification schedule
13. System commits transaction
14. System returns 200 OK with updated settings:
    ```json
    {
      "message": "SRS settings updated successfully",
      "settings": {
        "totalBoxes": 5,
        "reviewOrder": "DUE_DATE_ASC",
        "newCardsPerDay": 30,
        "maxReviewsPerDay": 200,
        "forgottenCardAction": "MOVE_DOWN_N_BOXES",
        "moveDownBoxes": 2,
        "notificationEnabled": true,
        "notificationTime": "09:00",
        "updatedAt": "2025-01-28T14:45:00Z"
      }
    }
    ```
15. Client receives response
16. Client displays success message: "SRS settings updated successfully"
17. Client updates form with new values
18. System applies new settings to all future review sessions
19. User sees updated settings on page

## 6. Alternative Flows

### 6a. Invalid Total Boxes - Too Low

**Trigger:** Step 7 or 9 - total_boxes < 3

1. System validates total_boxes
2. Value is 2 (below minimum)
3. System returns 400 Bad Request:
   ```json
   {
     "error": "Validation failed",
     "details": [
       {
         "field": "totalBoxes",
         "message": "Total boxes must be between 3 and 10"
       }
     ]
   }
   ```
4. UI displays inline error below Total Boxes field
5. Save button disabled until valid value entered
6. User must enter valid value
7. Return to Step 5 (Main Flow)

### 6b. Invalid Total Boxes - Too High

**Trigger:** Step 7 or 9 - total_boxes > 10

1. System validates total_boxes
2. Value is 15 (above maximum)
3. System returns 400 Bad Request with validation error
4. UI displays inline error
5. User must enter valid value
6. Return to Step 5 (Main Flow)

### 6c. Invalid New Cards Per Day - Too Low

**Trigger:** Step 7 or 9 - new_cards_per_day < 1

1. System validates new_cards_per_day
2. Value is 0 or negative
3. System returns 400 Bad Request:
   ```json
   {
     "error": "Validation failed",
     "details": [
       {
         "field": "newCardsPerDay",
         "message": "New cards per day must be between 1 and 100"
       }
     ]
   }
   ```
4. UI displays validation error
5. Return to Step 5 (Main Flow)

### 6d. Invalid Max Reviews Per Day - Out of Range

**Trigger:** Step 7 or 9 - max_reviews_per_day out of range

1. System validates max_reviews_per_day
2. Value is 5 (< 10) or 600 (> 500)
3. System returns 400 Bad Request:
   ```json
   {
     "error": "Validation failed",
     "details": [
       {
         "field": "maxReviewsPerDay",
         "message": "Max reviews per day must be between 10 and 500"
       }
     ]
   }
   ```
4. UI displays validation error
5. Return to Step 5 (Main Flow)

### 6e. Invalid Notification Time Format

**Trigger:** Step 7 or 9 - notification_time not in HH:mm format

1. System validates time format with regex `^([01]\d|2[0-3]):([0-5]\d)$`
2. Format is invalid (e.g., "9:00" or "25:00" or "09:60")
3. System returns 400 Bad Request:
   ```json
   {
     "error": "Validation failed",
     "details": [
       {
         "field": "notificationTime",
         "message": "Invalid time format. Use HH:mm (e.g., 09:00)"
       }
     ]
   }
   ```
4. UI displays error message
5. User must enter valid time format
6. Return to Step 5 (Main Flow)

### 6f. Invalid Enum Values

**Trigger:** Step 7 or 9 - review_order or forgotten_card_action invalid

1. System validates enum values
2. Value not in allowed set (e.g., "INVALID_ORDER")
3. System returns 400 Bad Request:
   ```json
   {
     "error": "Validation failed",
     "details": [
       {
         "field": "reviewOrder",
         "message": "Review order must be one of: DUE_DATE_ASC, RANDOM, CURRENT_BOX_ASC"
       }
     ]
   }
   ```
4. UI displays error
5. Return to Step 5 (Main Flow)

### 6f1. Invalid Move Down Boxes Value

**Trigger:** Step 7 or 9 - move_down_boxes invalid when forgotten_card_action = MOVE_DOWN_N_BOXES

1. User selects MOVE_DOWN_N_BOXES
2. User enters move_down_boxes = 4 (invalid, > 3) or 0 (invalid, < 1)
3. System validates move_down_boxes: must be 1-3 when forgotten_card_action = MOVE_DOWN_N_BOXES
4. System returns 400 Bad Request:
   ```json
   {
     "error": "Validation failed",
     "details": [
       {
         "field": "moveDownBoxes",
         "message": "Move down boxes must be between 1 and 3"
       }
     ]
   }
   ```
5. UI displays validation error
6. Return to Step 5 (Main Flow)

### 6f2. Move Down Boxes Missing When Required

**Trigger:** Step 7 or 9 - forgotten_card_action = MOVE_DOWN_N_BOXES but move_down_boxes not provided

1. User selects MOVE_DOWN_N_BOXES
2. User does not enter move_down_boxes value
3. System validates: move_down_boxes is required when forgotten_card_action = MOVE_DOWN_N_BOXES
4. System returns 400 Bad Request:
   ```json
   {
     "error": "Validation failed",
     "details": [
       {
         "field": "moveDownBoxes",
         "message": "Move down boxes is required when forgotten card action is MOVE_DOWN_N_BOXES"
       }
     ]
   }
   ```
5. UI displays validation error
6. Return to Step 5 (Main Flow)

### 6g. Session Expired During Update

**Trigger:** Step 9 - Access token expired

1. User's access token expires while on settings page
2. System returns 401 Unauthorized
3. Client attempts token refresh (UC-003)
4. If refresh succeeds:
   - Retry update request with new token
   - Continue to Step 10 (Main Flow)
5. If refresh fails:
   - Client redirects to login
   - Changes lost (user must re-enter)
   - Use case ends (failure)

### 6h. Database Error

**Trigger:** Step 13 - Database update fails

1. System attempts to update database
2. Database error occurs (connection lost, constraint violation)
3. Transaction automatically rolled back
4. No settings updated
5. System logs error details
6. System returns 500 Internal Server Error:
   ```json
   {
     "error": "Internal server error",
     "message": "Failed to update settings. Please try again."
   }
   ```
7. UI displays error message
8. User remains on settings page
9. User can retry save
10. Use case ends (failure)

### 6i. Concurrent Update Conflict

**Trigger:** Step 11 - Settings updated by another session

1. System detects updated_at timestamp mismatch (optimistic locking)
2. Settings were modified in another tab/session
3. System returns 409 Conflict:
   ```json
   {
     "error": "Concurrent modification",
     "message": "Settings were updated elsewhere. Please refresh and try again."
   }
   ```
4. Client displays conflict message
5. Client offers option to refresh settings
6. User refreshes and sees latest values
7. User can modify and save again
8. Use case ends (failure or retry)

### 6j. Reset to Defaults

**Trigger:** Step 5 - User clicks "Reset to Defaults" button

1. User clicks "Reset to Defaults" link/button
2. System loads default values:
   - total_boxes: 7
   - review_order: RANDOM
   - new_cards_per_day: 20
   - max_reviews_per_day: 200
   - forgotten_card_action: MOVE_TO_BOX_1
   - move_down_boxes: 1
   - notification_enabled: true
   - notification_time: "09:00"
3. Form fields updated with default values
4. User can save defaults or modify further
5. Return to Step 5 (Main Flow)

## 7. Special Requirements

### 7.1 Performance

- Response time < 1 second for fetching and updating settings
- Changes apply immediately to new sessions (no caching delay)
- Efficient single query to fetch settings

### 7.2 Usability

- **Real-time validation:** Inline validation as user types/changes values
- **Clear help text:** Tooltips or help icons explaining each setting
- **Reset to Defaults:** One-click reset button
- **Preview:** Show how settings affect review behavior (optional)
- **Save button state:** Disabled when form is pristine or invalid
- **Visual feedback:** Loading spinner during save, success toast

### 7.3 Data Validation

- **Client-side validation:** Mirrors server-side validation for immediate feedback
- **Numeric inputs:** Use number input type with min/max attributes
- **Time picker:** Use HTML5 time input or custom time picker component
- **Enum dropdowns:** Prevent invalid selections via UI

### 7.4 Notifications

- If notification_enabled toggled or notification_time changed:
  - Update scheduled notifications in notification service
  - Respect user's timezone for notification scheduling
  - Cancel existing notifications if disabled

### 7.5 Settings Persistence

- Settings persist across sessions
- Settings apply to all future review sessions
- No retroactive changes to existing sessions

## 8. Technology and Data Variations

### 8.1 Enum Values

**review_order:**
- `DUE_DATE_ASC`: Review cards by due date (earliest first)
- `RANDOM`: Randomize review order
- `CURRENT_BOX_ASC`: Review lower boxes first (Box 1 → Box 7)

**forgotten_card_action:**
- `MOVE_TO_BOX_1`: Move forgotten cards back to Box 1 (default)
- `MOVE_DOWN_N_BOXES`: Move down N boxes (1-3, configurable via move_down_boxes)
- `REPEAT_IN_SESSION`: Show forgotten cards again in current session without changing box

### 8.2 Default Values

Created during user registration (UC-001):
- total_boxes: 7
- review_order: RANDOM
- new_cards_per_day: 20
- max_reviews_per_day: 200
- forgotten_card_action: MOVE_TO_BOX_1
- move_down_boxes: 1 (default, only used if forgotten_card_action = MOVE_DOWN_N_BOXES)
- notification_enabled: true
- notification_time: "09:00"

### 8.3 Settings Update Logic

```typescript
interface SRSSettings {
  totalBoxes: number; // 3-10
  reviewOrder: 'DUE_DATE_ASC' | 'RANDOM' | 'CURRENT_BOX_ASC';
  newCardsPerDay: number; // 1-100
  maxReviewsPerDay: number; // 10-500
  forgottenCardAction: 'MOVE_TO_BOX_1' | 'MOVE_DOWN_N_BOXES' | 'REPEAT_IN_SESSION';
  moveDownBoxes?: number; // 1-3, required if forgottenCardAction = 'MOVE_DOWN_N_BOXES'
  notificationEnabled: boolean;
  notificationTime: string; // HH:mm format
}

const validateSRSSettings = (settings: Partial<SRSSettings>): ValidationResult => {
  const errors: ValidationError[] = [];

  if (settings.totalBoxes !== undefined) {
    if (settings.totalBoxes < 3 || settings.totalBoxes > 10) {
      errors.push({
        field: 'totalBoxes',
        message: 'Total boxes must be between 3 and 10'
      });
    }
  }

  if (settings.forgottenCardAction === 'MOVE_DOWN_N_BOXES') {
    if (!settings.moveDownBoxes || settings.moveDownBoxes < 1 || settings.moveDownBoxes > 3) {
      errors.push({
        field: 'moveDownBoxes',
        message: 'Move down boxes must be between 1 and 3'
      });
    }
  }

  // ... other validations

  return { isValid: errors.length === 0, errors };
};
```

### 8.4 Notification Schedule Update

```typescript
const updateNotificationSchedule = async (
  userId: string,
  enabled: boolean,
  time: string
) => {
  if (enabled) {
    // Cancel existing notifications
    await cancelScheduledNotifications(userId);

    // Schedule new notification at specified time
    const [hours, minutes] = time.split(':').map(Number);
    await scheduleDailyNotification(userId, hours, minutes);
  } else {
    await cancelScheduledNotifications(userId);
  }
};
```

## 9. Frequency of Occurrence

- **Infrequent:** 1-5 times per user during onboarding
- **Occasional updates:** When users adjust learning pace or preferences
- **Total:** 10-50 updates/day (MVP phase)
- Most users set once and rarely change

## 10. Open Issues

- **Preset profiles:** Offer presets like "Conservative", "Balanced", "Aggressive" (future)
- **Custom box intervals:** Allow users to customize time intervals for each box (future)
- **Per-deck settings:** Override global settings per deck (future)
- **Advanced scheduler:** Alternative algorithms like SM-2, SM-17 (future)
- **Settings import/export:** Allow users to backup/restore settings (future)

## 11. Related Use Cases

- [UC-001: User Registration](UC-001-user-registration.md) - SRS settings initialized
- [UC-023: Review Cards (SRS)](UC-023-review-cards-srs.md) - Settings applied during review
- [UC-024: Rate Card](UC-024-rate-card.md) - forgotten_card_action affects rating behavior

## 12. Business Rules References

- **BR-SRS-01:** Total boxes default 7, allowed range 3-10
- **BR-SRS-02:** Forgotten card action default MOVE_TO_BOX_1, options include MOVE_DOWN_N_BOXES (1-3 boxes)
- **BR-SRS-03:** Review order default RANDOM
- **BR-SRS-04:** New cards per day and max reviews per day must be positive integers with caps
- **BR-6:** SRS Rules (forgotten card action options)

## 13. UI Mockup Notes

### Settings Form Layout

```
Settings > SRS Configuration

┌─────────────────────────────────────────┐
│ Learning Pace                           │
├─────────────────────────────────────────┤
│ Total Boxes *                           │
│ [7]  (3-10)                            │
│ ℹ️ Number of SRS boxes                 │
│                                         │
│ New Cards Per Day *                     │
│ [20]  (1-100)                           │
│ ℹ️ Maximum new cards introduced daily  │
│                                         │
│ Max Reviews Per Day *                   │
│ [200]  (10-500)                         │
│ ℹ️ Maximum cards reviewed per day      │
└─────────────────────────────────────────┘

┌─────────────────────────────────────────┐
│ Review Preferences                      │
├─────────────────────────────────────────┤
│ Review Order *                          │
│ [Random ▼]                              │
│   • Due Date (Earliest First)          │
│   • Random                              │
│   • Current Box (Low to High)          │
│                                         │
│ Forgotten Card Action *                 │
│ [Move to Box 1 ▼]                      │
│   • Move to Box 1                      │
│   • Move Down N Boxes                  │
│   • Repeat in Session                   │
│                                         │
│ If "Move Down N Boxes" selected:       │
│ Move Down Boxes *                      │
│ [1]  (1-3)                              │
│ ℹ️ Number of boxes to move down       │
└─────────────────────────────────────────┘

┌─────────────────────────────────────────┐
│ Notifications                           │
├─────────────────────────────────────────┤
│ [✓] Enable Daily Review Reminder        │
│                                         │
│ Notification Time *                     │
│ [09:00]                                 │
└─────────────────────────────────────────┘

[Reset to Defaults]  [Save Settings]
```

## 14. API Endpoint

```http
GET /api/srs-settings
PATCH /api/srs-settings
```

**Request Headers:**

```http
Authorization: Bearer <access-token>
Content-Type: application/json
```

**GET Success Response (200 OK):**

```json
{
  "totalBoxes": 7,
  "reviewOrder": "RANDOM",
  "newCardsPerDay": 20,
  "maxReviewsPerDay": 200,
  "forgottenCardAction": "MOVE_TO_BOX_1",
  "moveDownBoxes": 1,
  "notificationEnabled": true,
  "notificationTime": "09:00",
  "updatedAt": "2025-01-01T10:00:00Z"
}
```

**PATCH Request Body:**

```json
{
  "totalBoxes": 5,
  "reviewOrder": "DUE_DATE_ASC",
  "newCardsPerDay": 30,
  "maxReviewsPerDay": 150,
  "forgottenCardAction": "MOVE_DOWN_N_BOXES",
  "moveDownBoxes": 2,
  "notificationEnabled": false,
  "notificationTime": "08:00"
}
```

**PATCH Success Response (200 OK):**

```json
{
  "message": "SRS settings updated successfully",
  "settings": {
    "totalBoxes": 5,
    "reviewOrder": "DUE_DATE_ASC",
    "newCardsPerDay": 30,
    "maxReviewsPerDay": 150,
    "forgottenCardAction": "MOVE_DOWN_N_BOXES",
    "moveDownBoxes": 2,
    "notificationEnabled": false,
    "notificationTime": "08:00",
    "updatedAt": "2025-01-28T14:45:00Z"
  }
}
```

**Error Responses:**

400 Bad Request - Validation errors:

```json
{
  "error": "Validation failed",
  "details": [
    {
      "field": "totalBoxes",
      "message": "Total boxes must be between 3 and 10"
    },
    {
      "field": "notificationTime",
      "message": "Invalid time format. Use HH:mm"
    }
  ]
}
```

401 Unauthorized:

```json
{
  "error": "Unauthorized",
  "message": "Authentication required"
}
```

409 Conflict - Concurrent modification:

```json
{
  "error": "Concurrent modification",
  "message": "Settings were updated elsewhere. Please refresh and try again."
}
```

500 Internal Server Error:

```json
{
  "error": "Internal server error",
  "message": "Failed to update settings. Please try again."
}
```

## 15. Test Cases

### TC-028-001: Successful Settings Update

- **Given:** User has default SRS settings
- **When:** User updates total_boxes to 5 and new_cards_per_day to 30
- **Then:** Settings saved successfully, applied to future sessions

### TC-028-002: Invalid Total Boxes (Too Low)

- **Given:** User enters total_boxes = 2
- **When:** User clicks Save
- **Then:** 400 error with message "Total boxes must be between 3 and 10"

### TC-028-003: Invalid Total Boxes (Too High)

- **Given:** User enters total_boxes = 15
- **When:** User clicks Save
- **Then:** 400 error with validation message

### TC-028-004: Invalid Notification Time Format

- **Given:** User enters notification_time = "25:00"
- **When:** User clicks Save
- **Then:** 400 error with message "Invalid time format"

### TC-028-005: Reset to Defaults

- **Given:** User has custom settings
- **When:** User clicks "Reset to Defaults"
- **Then:** All settings revert to default values

### TC-028-006: Settings Apply Immediately

- **Given:** User changes review_order to DUE_DATE_ASC
- **When:** User starts new review session
- **Then:** Cards presented in due date order

### TC-028-007: Partial Update

- **Given:** User updates only notification_time
- **When:** User clicks Save
- **Then:** Only notification_time updated, other fields unchanged

### TC-028-008: Session Expired During Update

- **Given:** User's access token expires
- **When:** User saves settings
- **Then:** Token refreshed automatically, save succeeds

### TC-028-009: Concurrent Update Conflict

- **Given:** User editing settings in Tab 1
- **When:** Settings updated in Tab 2, then user saves in Tab 1
- **Then:** 409 Conflict, option to refresh

### TC-028-010: Notification Disabled

- **Given:** User disables notification_enabled
- **When:** User saves settings
- **Then:** Scheduled notifications cancelled

### TC-028-011: Configure Move Down N Boxes

- **Given:** User has cards in Box 5
- **When:** User sets forgotten_card_action = MOVE_DOWN_N_BOXES with move_down_boxes = 2
- **Then:** When rating AGAIN, card moves from Box 5 to Box 3 (not Box 1)

### TC-028-012: Move Down N Boxes Validation

- **Given:** User selects MOVE_DOWN_N_BOXES
- **When:** User sets move_down_boxes = 4 (invalid, > 3)
- **Then:** Validation error "Move down boxes must be between 1 and 3"

### TC-028-013: Move Down N Boxes Minimum Validation

- **Given:** User selects MOVE_DOWN_N_BOXES
- **When:** User sets move_down_boxes = 0 (invalid, < 1)
- **Then:** Validation error "Move down boxes must be between 1 and 3"

### TC-028-014: Move Down N Boxes Required When Selected

- **Given:** User selects MOVE_DOWN_N_BOXES
- **When:** User saves without setting move_down_boxes
- **Then:** Validation error "Move down boxes is required when forgotten card action is MOVE_DOWN_N_BOXES"

## 16. Database Schema Reference

### srs_settings table

```sql
CREATE TABLE srs_settings (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
  total_boxes INTEGER NOT NULL DEFAULT 7 CHECK (total_boxes >= 3 AND total_boxes <= 10),
  review_order VARCHAR(20) NOT NULL DEFAULT 'RANDOM' 
    CHECK (review_order IN ('DUE_DATE_ASC', 'RANDOM', 'CURRENT_BOX_ASC')),
  new_cards_per_day INTEGER NOT NULL DEFAULT 20 CHECK (new_cards_per_day >= 1 AND new_cards_per_day <= 100),
  max_reviews_per_day INTEGER NOT NULL DEFAULT 200 CHECK (max_reviews_per_day >= 10 AND max_reviews_per_day <= 500),
  forgotten_card_action VARCHAR(20) NOT NULL DEFAULT 'MOVE_TO_BOX_1'
    CHECK (forgotten_card_action IN ('MOVE_TO_BOX_1', 'MOVE_DOWN_N_BOXES', 'REPEAT_IN_SESSION')),
  move_down_boxes INTEGER NOT NULL DEFAULT 1 CHECK (move_down_boxes >= 1 AND move_down_boxes <= 3),
  notification_enabled BOOLEAN NOT NULL DEFAULT true,
  notification_time VARCHAR(5) NOT NULL DEFAULT '09:00' 
    CHECK (notification_time ~ '^([01]\d|2[0-3]):([0-5]\d)$'),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_srs_settings_user_id ON srs_settings(user_id);
```
