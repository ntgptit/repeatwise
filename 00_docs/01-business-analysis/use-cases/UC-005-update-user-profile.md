# UC-005: Update User Profile

## 1. Brief Description

Authenticated user updates their profile settings including name, timezone, language preference, and theme preference in RepeatWise system.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** None

## 3. Preconditions

- User is logged in (has valid access token)
- User can access Settings/Profile page

## 4. Postconditions

### Success Postconditions

- User profile updated in database (users table)
- updated_at timestamp refreshed
- Success message displayed
- UI immediately reflects new settings:
  - Language changes if language updated
  - Theme changes if theme updated
  - Timezone used for date/time display
- User remains on Settings page

### Failure Postconditions

- User profile not updated
- Error message displayed
- User remains on Settings page

## 5. Main Success Scenario (Basic Flow)

1. User is logged in and navigates to Settings page
2. System displays current profile settings form with fields:
   - Name (text input)
   - Timezone (dropdown select)
   - Language (radio buttons: Vietnamese, English)
   - Theme (radio buttons: Light, Dark, System)
3. System pre-populates form with current user values
4. User updates one or more fields:
   - Changes name from "John Doe" to "John Smith"
   - Changes timezone to "Asia/Ho_Chi_Minh"
   - Changes language from "EN" to "VI"
   - Changes theme from "SYSTEM" to "DARK"
5. User clicks "Save" or "Update Profile" button
6. System validates input:
   - Name max length 100 characters
   - Timezone is valid timezone identifier
   - Language is 'VI' or 'EN'
   - Theme is 'LIGHT', 'DARK', or 'SYSTEM'
7. System trims whitespace from name
8. System updates users table:

   ```sql
   UPDATE users
   SET name = ?,
       timezone = ?,
       language = ?,
       theme = ?,
       updated_at = CURRENT_TIMESTAMP
   WHERE id = ?
   ```

9. System returns 200 OK with updated user object
10. Client receives response
11. Client updates auth context with new user data
12. Client applies theme change immediately (if theme updated)
13. Client switches language (if language updated):
    - i18n.changeLanguage(newLanguage)
    - UI text updates to new language
14. System displays success message: "Profile updated successfully"
15. User sees updated settings on page

## 6. Alternative Flows

### 6a. Name Too Long

**Trigger:** Step 6 - Name exceeds 100 characters

1. System validates name length
2. name.length > 100
3. System returns 400 Bad Request
4. UI displays validation error: "Name must be 100 characters or less"
5. User must shorten name
6. Return to Step 4 (Main Flow)

### 6b. Invalid Timezone

**Trigger:** Step 6 - Timezone not valid

1. System validates timezone identifier
2. Timezone not in valid list (e.g., typo, invalid format)
3. System returns 400 Bad Request
4. UI displays error: "Invalid timezone selected"
5. User must select valid timezone
6. Return to Step 4 (Main Flow)

**Note:** Client should use dropdown with predefined valid timezones to prevent this

### 6c. Invalid Language

**Trigger:** Step 6 - Language not 'VI' or 'EN'

1. System validates language value
2. Language not in ['VI', 'EN']
3. System returns 400 Bad Request
4. UI displays error: "Invalid language selected"
5. Return to Step 4 (Main Flow)

**Note:** Should be prevented by UI (radio buttons)

### 6d. Invalid Theme

**Trigger:** Step 6 - Theme not in valid values

1. System validates theme value
2. Theme not in ['LIGHT', 'DARK', 'SYSTEM']
3. System returns 400 Bad Request
4. UI displays error: "Invalid theme selected"
5. Return to Step 4 (Main Flow)

**Note:** Should be prevented by UI (radio buttons)

### 6e. Empty Name

**Trigger:** Step 7 - Name is empty or only whitespace

1. User clears name field
2. User clicks Save
3. System trims whitespace
4. Resulting name is empty string
5. System returns 400 Bad Request
6. UI displays error: "Name cannot be empty"
7. Return to Step 4 (Main Flow)

**Note:** Name is optional in MVP - can allow empty. Decision: Make it required.

### 6f. Unauthorized Access

**Trigger:** Step 8 - Access token invalid or expired

1. User's session expired
2. System cannot authenticate user
3. System returns 401 Unauthorized
4. Client catches error
5. Client refreshes token (UC-003)
6. If refresh succeeds, retry update
7. If refresh fails, redirect to login

### 6g. Database Error

**Trigger:** Step 8 - Database update fails

1. System attempts to update database
2. Database error occurs (connection lost, etc.)
3. System logs error
4. System returns 500 Internal Server Error
5. UI displays error: "Failed to update profile. Please try again."
6. User remains on Settings page
7. Use case ends (failure)

### 6h. No Changes Made

**Trigger:** Step 5 - User clicks Save without changing any field

1. System detects no changes (optional optimization)
2. System can either:
   - Option A: Return success immediately without DB update
   - Option B: Proceed with update (refresh updated_at timestamp)
3. UI displays message: "Profile updated successfully" or "No changes made"
4. Use case ends (success)

## 7. Special Requirements

### 7.1 Performance

- Response time < 300ms
- Theme/language change should apply instantly
- No page reload required

### 7.2 Validation

- Name: 1-100 characters, trim whitespace
- Timezone: Must be valid IANA timezone identifier
- Language: Enum ['VI', 'EN']
- Theme: Enum ['LIGHT', 'DARK', 'SYSTEM']

### 7.3 Usability

- **Inline validation:** Real-time feedback as user types
- **Instant apply:** Theme/language changes without page reload
- **Visual feedback:** Save button shows loading state
- **Success message:** Clear confirmation of update
- **Preserve state:** User stays on same page after update
- **Keyboard shortcuts:** Ctrl+S to save (optional)

### 7.4 Internationalization

- All UI labels support both Vietnamese and English
- Language switch immediately updates all text
- Date/time format follows selected timezone

## 8. Technology and Data Variations

### 8.1 Timezone Handling

Common timezones for Vietnam users:

- Asia/Ho_Chi_Minh (GMT+7)
- Asia/Bangkok
- Asia/Singapore

Full list from IANA timezone database:
<https://en.wikipedia.org/wiki/List_of_tz_database_time_zones>

### 8.2 Theme Application

Client-side theme logic:

```typescript
const applyTheme = (theme: 'LIGHT' | 'DARK' | 'SYSTEM') => {
  if (theme === 'SYSTEM') {
    const systemTheme = window.matchMedia('(prefers-color-scheme: dark)').matches
      ? 'DARK'
      : 'LIGHT';
    document.documentElement.classList.toggle('dark', systemTheme === 'DARK');
  } else {
    document.documentElement.classList.toggle('dark', theme === 'DARK');
  }
};
```

### 8.3 Language Switching

Using react-i18next:

```typescript
import { useTranslation } from 'react-i18next';

const { i18n } = useTranslation();

const changeLanguage = (lang: 'vi' | 'en') => {
  i18n.changeLanguage(lang);
  // Save to localStorage for persistence
  localStorage.setItem('language', lang);
};
```

### 8.4 Profile Update Optimization

Partial update only changed fields:

```typescript
const updateProfile = async (updates: Partial<UserProfile>) => {
  // Only send changed fields
  const changedFields = Object.keys(updates).reduce((acc, key) => {
    if (updates[key] !== currentUser[key]) {
      acc[key] = updates[key];
    }
    return acc;
  }, {});

  if (Object.keys(changedFields).length === 0) {
    return { message: 'No changes made' };
  }

  return api.patch('/api/users/profile', changedFields);
};
```

## 9. Frequency of Occurrence

- Expected: 1-5 profile updates per user per month
- Most users update once during onboarding
- Total: 10-50 updates/day (MVP phase)

## 10. Open Issues

- **Avatar upload:** Not in MVP (file upload adds complexity)
- **Email change:** Separate flow with verification (future)
- **Profile visibility:** All profiles private in MVP (no public profiles)
- **Additional fields:** Phone number, bio, etc. (future)
- **Preferences sync:** Across devices (handled by database, no special sync needed)

## 11. Related Use Cases

- [UC-001: User Registration](UC-001-user-registration.md) - Initial profile created
- [UC-006: Change Password](UC-006-change-password.md) - Separate security setting
- [UC-028: Configure SRS Settings](UC-028-configure-srs-settings.md) - Learning preferences

## 12. Business Rules References

- **BR-1.4:** User Profile (name, timezone, language, theme)

## 13. API Endpoint

```http
PUT /api/users/profile
PATCH /api/users/profile (recommended for partial updates)
```

**Request Headers:**

```http
Authorization: Bearer <access-token>
Content-Type: application/json
```

**Request Body:**

```json
{
  "name": "John Smith",
  "timezone": "Asia/Ho_Chi_Minh",
  "language": "VI",
  "theme": "DARK"
}
```

**Success Response (200 OK):**

```json
{
  "message": "Profile updated successfully",
  "user": {
    "id": "uuid",
    "email": "user@example.com",
    "name": "John Smith",
    "timezone": "Asia/Ho_Chi_Minh",
    "language": "VI",
    "theme": "DARK",
    "created_at": "2025-01-15T10:30:00Z",
    "updated_at": "2025-01-28T14:45:00Z"
  }
}
```

**Error Responses:**

400 Bad Request - Validation error:

```json
{
  "error": "Validation failed",
  "details": [
    {
      "field": "name",
      "message": "Name must be 100 characters or less"
    },
    {
      "field": "timezone",
      "message": "Invalid timezone"
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

500 Internal Server Error:

```json
{
  "error": "Internal server error",
  "message": "Failed to update profile. Please try again."
}
```

## 14. Test Cases

### TC-005-001: Update Name Successfully

- **Given:** User logged in with name "John Doe"
- **When:** User changes name to "Jane Smith" and saves
- **Then:** Name updated in database, success message shown

### TC-005-002: Update Timezone Successfully

- **Given:** User with timezone "UTC"
- **When:** User changes to "Asia/Ho_Chi_Minh" and saves
- **Then:** Timezone updated, date/time display uses new timezone

### TC-005-003: Switch Language to Vietnamese

- **Given:** User with language "EN"
- **When:** User selects "VI" and saves
- **Then:** Language updated, UI switches to Vietnamese immediately

### TC-005-004: Switch Theme to Dark Mode

- **Given:** User with theme "LIGHT"
- **When:** User selects "DARK" and saves
- **Then:** Theme updated, UI switches to dark mode immediately

### TC-005-005: Name Too Long

- **Given:** User enters name with 101 characters
- **When:** User clicks Save
- **Then:** Validation error "Name must be 100 characters or less"

### TC-005-006: Empty Name

- **Given:** User clears name field (empty string)
- **When:** User clicks Save
- **Then:** Validation error "Name cannot be empty"

### TC-005-007: Invalid Timezone

- **Given:** User sends API request with invalid timezone "InvalidTZ"
- **When:** Request processed
- **Then:** 400 error with "Invalid timezone"

### TC-005-008: Update Multiple Fields

- **Given:** User logged in
- **When:** User updates name, timezone, language, and theme simultaneously
- **Then:** All fields updated successfully

### TC-005-009: No Changes Made

- **Given:** User opens settings, makes no changes
- **When:** User clicks Save
- **Then:** Success message or "No changes made"

### TC-005-010: Session Expired During Update

- **Given:** User's access token expires
- **When:** User tries to save profile
- **Then:** Token refreshed automatically, update succeeds

## 15. UI/UX Considerations

### 15.1 Form Layout

```
Settings > Profile

Name *
[John Doe                    ]

Timezone *
[Asia/Ho_Chi_Minh           �]

Language *
( ) English  (") Vietnamese

Theme *
( ) Light  ( ) Dark  (") System

[Cancel]  [Save Changes]
```

### 15.2 Validation Feedback

- Real-time character counter for name field
- Inline error messages below fields
- Disable save button while validation errors exist
- Show asterisk (*) for required fields

### 15.3 Success Feedback

- Toast notification: "Profile updated successfully"
- Brief highlight animation on updated fields
- Check mark icon next to Save button

### 15.4 Loading States

- Disable form during save
- Show spinner on Save button
- Prevent duplicate submissions

## 16. Accessibility

- Proper label associations for form fields
- Keyboard navigation (Tab, Enter)
- Screen reader announcements for validation errors
- Focus management after save
- Clear error messages

## 17. Future Enhancements

- Profile picture upload
- Email change with verification
- Phone number field
- Bio/description field
- Account deletion option
- Export profile data (GDPR compliance)
- Profile completeness indicator
- Notification preferences (separate from profile)
