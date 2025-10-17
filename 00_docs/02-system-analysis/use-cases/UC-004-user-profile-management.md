# UC-004: User Profile Management

## 1. Use Case Information

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-004 |
| **Use Case Name** | User Profile Management |
| **Primary Actor** | Authenticated User |
| **Secondary Actors** | None |
| **Priority** | Medium (P1) |
| **Complexity** | Low |
| **Status** | MVP |

## 2. Brief Description

Authenticated user views and updates their profile information including name, timezone, language preference (Vietnamese/English), and theme (Light/Dark/System).

## 3. Preconditions

- User is logged in
- Valid JWT token exists
- User has access to Settings page

## 4. Postconditions

**Success**:
- User profile updated in database
- Changes reflected immediately in UI
- Success message displayed
- Preferences applied (language, theme)

**Failure**:
- No changes saved
- Error message displayed
- User can retry

## 5. Main Success Scenario

### Step 1: Access Profile Settings
**Actor**: User navigates to Settings

**System**:
- Shows main settings menu with tabs:
  - **Profile** (selected)
  - SRS Settings
  - Notifications
- Displays profile form pre-filled with current data:
  - Username: "minhnguyendev" (read-only)
  - Name: "Nguyễn Văn Minh"
  - Email: "minh@example.com" (read-only)
  - Timezone: "Asia/Ho_Chi_Minh"
  - Language: "Vietnamese"
  - Theme: "System"

### Step 2: Edit Profile Information
**Actor**: User modifies fields:
- Name: "Nguyễn Văn Minh" → "Minh Nguyen"
- Timezone: "Asia/Ho_Chi_Minh" → "Asia/Bangkok"
- Language: "Vietnamese" → "English"
- Theme: "System" → "Dark"

**System**:
- Enables "Save Changes" button (initially disabled)
- Shows unsaved changes indicator (*)

### Step 3: Save Changes
**Actor**: User clicks "Save Changes" button

**System**:
1. Validates input:
   - Name: not empty, max 100 characters
   - Timezone: valid timezone from list
   - Language: 'VI' or 'EN'
   - Theme: 'LIGHT', 'DARK', or 'SYSTEM'
2. Sends PUT request to API:
   ```
   PUT /api/users/profile
   {
     "name": "Minh Nguyen",
     "timezone": "Asia/Bangkok",
     "language": "EN",
     "theme": "DARK"
   }
   ```
3. Updates database:
   ```sql
   UPDATE users SET
     name = 'Minh Nguyen',
     timezone = 'Asia/Bangkok',
     language = 'EN',
     theme = 'DARK',
     updated_at = NOW()
   WHERE id = :userId;
   ```
4. Returns updated user object
5. Updates user context (React Context)
6. Applies changes:
   - Language: Switch UI to English
   - Theme: Apply dark mode immediately
   - Timezone: Use for date/time display
7. Shows success toast: "Profile updated successfully"
8. Disables "Save Changes" button (no unsaved changes)

**Actor**: User sees updated profile with English UI and dark theme

## 6. Alternative Flows

### A1: Change Language Only
**Trigger**: User changes language (Step 2)

**Flow**:
1. User changes language dropdown: Vietnamese → English
2. System immediately updates UI language (preview)
3. Shows banner: "Language will be saved when you click 'Save Changes'"
4. User clicks "Save Changes"
5. System persists language preference
6. All UI text switches to English

**Continue to**: Step 3

---

### A2: Change Theme Only
**Trigger**: User changes theme (Step 2)

**Flow**:
1. User changes theme: System → Dark
2. System immediately applies dark mode (preview)
3. All colors, backgrounds switch to dark palette
4. Shows banner: "Theme will be saved when you click 'Save Changes'"
5. User clicks "Save Changes"
6. System persists theme preference

**Continue to**: Step 3

---

### A3: Cancel Changes
**Trigger**: User clicks "Cancel" or navigates away (Step 2)

**Flow**:
1. User clicks "Cancel" button
2. System shows confirmation dialog if unsaved changes:
   - "Discard unsaved changes?"
   - [Stay] [Discard]
3. If user clicks "Discard":
   - System reverts form to original values
   - System reverts language/theme to saved state
   - Disables "Save Changes" button
4. User remains on profile page or navigates away

**End Use Case**

---

### A4: No Changes Made
**Trigger**: User views profile but doesn't edit (Step 2)

**Flow**:
1. User views profile settings
2. "Save Changes" button remains disabled
3. User navigates away without saving
4. No API call made

**End Use Case**

---

### A5: Auto-Detect Timezone
**Trigger**: User clicks "Detect Timezone" button (Step 2)

**Flow**:
1. User clicks "Detect Timezone" button
2. System uses browser API: `Intl.DateTimeFormat().resolvedOptions().timeZone`
3. System auto-fills timezone field with detected value
4. Example: "Asia/Ho_Chi_Minh"
5. User can accept or manually change
6. User clicks "Save Changes" to persist

**Continue to**: Step 3

## 7. Exception Flows

### E1: Invalid Name (Empty or Too Long)
**Trigger**: User enters invalid name (Step 3)

**Flow**:
1. User clears name field (empty) or enters > 100 characters
2. System validates on save
3. System shows error: "Name is required and must be less than 100 characters"
4. System highlights name field in red
5. User corrects name
6. User retries save

**Return to**: Step 2

---

### E2: Network Error During Save
**Trigger**: Network connection lost during save (Step 3)

**Flow**:
1. System attempts to save profile
2. Request times out or fails
3. System shows error toast: "Failed to save profile. Please check your connection and try again."
4. System keeps unsaved changes in form
5. "Save Changes" button remains enabled
6. User fixes connection and clicks "Save Changes" again

**Return to**: Step 3

---

### E3: Server Error (500)
**Trigger**: Internal server error during save (Step 3)

**Flow**:
1. System attempts to save profile
2. Server returns 500 error
3. System shows error toast: "Something went wrong. Please try again later."
4. System keeps unsaved changes in form
5. System logs error for debugging
6. User can retry or contact support

**Return to**: Step 3

## 8. Special Requirements

### Performance
- Profile load: < 200ms
- Profile save: < 300ms
- Theme switch: Instant (< 100ms)
- Language switch: < 500ms (load translations)

### Security
- Email cannot be changed (read-only for MVP)
- Only owner can update profile (JWT user_id validation)
- Input sanitization (prevent XSS)
- No sensitive data logged (e.g., passwords)

### Usability
- Auto-save not implemented (explicit save button)
- Unsaved changes warning on navigation
- Live preview for theme/language changes
- Clear field labels and help text
- Keyboard navigation supported

### Accessibility
- Form fields have proper labels
- Error messages announced to screen readers
- Theme toggle accessible (keyboard + screen reader)
- Focus management on validation errors

## 9. Business Rules

### BR-009: Profile Fields
- **Username**: Read-only, cannot be changed in MVP
- **Name**: Required, 1-100 characters, any Unicode characters
- **Email**: Read-only, cannot be changed in MVP
- **Timezone**: Must be valid IANA timezone (e.g., "Asia/Ho_Chi_Minh")
- **Language**: 'VI' (Vietnamese) or 'EN' (English) only
- **Theme**: 'LIGHT', 'DARK', or 'SYSTEM'

### BR-010: Theme Behavior
- **LIGHT**: Always light mode
- **DARK**: Always dark mode
- **SYSTEM**: Follow OS preference (prefers-color-scheme)
  - System watches for OS theme changes (real-time)

### BR-011: Language Behavior
- Language change affects:
  - All UI text (navigation, buttons, labels)
  - Date/time formats (Vietnamese vs English locale)
  - Error messages
  - Email notifications (Future)
- Translation missing → Fallback to English

### BR-012: Timezone Behavior
- Timezone affects:
  - Review schedule display ("9:00 AM Asia/Bangkok")
  - Notification times
  - Streak calculation (midnight in user's timezone)
  - Statistics date ranges

### BR-013: Username and Email Immutability (MVP)
- Username cannot be changed in MVP (would affect login and references)
- Email cannot be changed in MVP (security risk)
- Future: Implement username/email change flow with verification
  - Username change: Require password confirmation
  - Email change: Send verification email to new address
  - Confirm via link
  - Update after confirmation

## 10. Data Requirements

### Input Data
- name: VARCHAR(100), required
- timezone: VARCHAR(50), required, valid IANA timezone
- language: ENUM('VI', 'EN'), required
- theme: ENUM('LIGHT', 'DARK', 'SYSTEM'), required

### Output Data
- Updated user object: { id, username, email, name, timezone, language, theme, updated_at }

### Database Changes
- UPDATE users table:
  - name
  - timezone
  - language
  - theme
  - updated_at (automatic)

### API Endpoints
- **GET /api/users/profile**: Fetch current profile
- **PUT /api/users/profile**: Update profile

## 11. UI Mockup Notes

### Profile Settings Page
```
┌───────────────────────────────────────────────────┐
│  Settings                                  [×]    │
├───────────────────────────────────────────────────┤
│  [Profile] [SRS Settings] [Notifications]        │
├───────────────────────────────────────────────────┤
│                                                   │
│  Personal Information                             │
│  ─────────────────────────────────────────────   │
│                                                   │
│  Username                                         │
│  [minhnguyendev___________________] 🔒 Read-only │
│                                                   │
│  Name *                                           │
│  [Nguyễn Văn Minh_________________]              │
│                                                   │
│  Email                                            │
│  [minh@example.com________________] 🔒 Read-only │
│                                                   │
│  Timezone *                                       │
│  [Asia/Ho_Chi_Minh ▾] [Detect Timezone]          │
│                                                   │
│  Preferences                                      │
│  ─────────────────────────────────────────────   │
│                                                   │
│  Language *                                       │
│  ○ Tiếng Việt   ● English                        │
│                                                   │
│  Theme *                                          │
│  ○ Light   ○ Dark   ● System (Follow OS)         │
│                                                   │
│  Preview:                                         │
│  ┌─────────────────────────────────────┐         │
│  │ [Dark theme preview shown here]     │         │
│  └─────────────────────────────────────┘         │
│                                                   │
│  [Cancel]                    [Save Changes]      │
│                                                   │
└───────────────────────────────────────────────────┘
```

### Unsaved Changes Warning Dialog
```
┌─────────────────────────────────────┐
│  Unsaved Changes                    │
├─────────────────────────────────────┤
│  You have unsaved changes.          │
│  Are you sure you want to leave?    │
│                                     │
│  [Stay]              [Discard]      │
└─────────────────────────────────────┘
```

## 12. Testing Scenarios

### Happy Path
1. User opens profile settings
2. User changes name, timezone, language, theme
3. User clicks "Save Changes"
4. Profile updated successfully
5. UI reflects new language and theme
6. Success message displayed

### Edge Cases
1. Unicode name (Nguyễn, 日本語) → Saved correctly ✅
2. Very long name (100 chars) → Allowed ✅
3. Name with special chars (@, #, $) → Allowed ✅
4. Change theme while on review screen → Applied immediately ✅
5. Change language mid-session → UI updates without losing state ✅
6. Detect timezone on mobile → Works correctly ✅

### Error Cases
1. Empty name → Validation error, show message
2. Name > 100 chars → Validation error, truncate or show message
3. Invalid timezone → Dropdown prevents (client validation)
4. Network timeout → Show error, keep changes, allow retry
5. Server error → Show error, log details
6. Concurrent edits (two devices) → Last write wins (acceptable for MVP)

### Theme Testing
1. Light theme → All colors correct
2. Dark theme → All colors correct, no unreadable text
3. System theme → Follows OS preference
4. System theme changes → Updates in real-time
5. Theme persists across sessions → Correct

### Language Testing
1. Vietnamese UI → All text translated
2. English UI → All text translated
3. Missing translation → Fallback to English
4. Date formats → Correct locale (VI: "10/01/2025", EN: "01/10/2025")
5. Number formats → Correct locale

## 13. Related Use Cases

- **UC-001**: User Registration - Sets initial profile values
- **UC-002**: User Login - Loads profile on login
- **UC-003**: User Logout - Session ends
- **UC-022**: Configure SRS Settings - Related settings page

## 14. Notes & Assumptions

### Assumptions
- **Username immutable**: Cannot change username in MVP
- **Email immutable**: Cannot change email in MVP (security)
- **No avatar upload**: Plain text name only
- **Simple validation**: No complex password policy for name
- **Auto-save not needed**: Explicit save button sufficient
- **Single timezone**: No multi-timezone support (user picks one)

### Future Enhancements
- **Avatar upload**: Profile picture with cropping
- **Username change**: Allow with password confirmation
- **Email change**: Verification flow with confirmation email
- **Two-factor authentication**: Enable 2FA in profile
- **Delete account**: Self-service account deletion
- **Export user data**: GDPR compliance (download all user data)
- **Account activity log**: Show login history, sessions
- **Privacy settings**: Control data sharing, analytics opt-out

### MVP Limitations
- No avatar/profile picture
- No bio or description field
- No social links
- No privacy controls
- No account deletion (must contact support)

## 15. Acceptance Criteria

- [ ] User can view current profile information
- [ ] User can edit name, timezone, language, theme
- [ ] Username field is read-only (no edit)
- [ ] Email field is read-only (no edit)
- [ ] Name validation works (required, max 100 chars)
- [ ] Timezone dropdown shows valid IANA timezones
- [ ] "Detect Timezone" button works correctly
- [ ] Language switch updates UI immediately
- [ ] Theme switch applies instantly (preview)
- [ ] Changes persist after save
- [ ] Success message displayed on save
- [ ] Error messages clear and actionable
- [ ] Unsaved changes warning on navigation
- [ ] "Cancel" reverts changes
- [ ] Profile load < 200ms, save < 300ms
- [ ] Theme and language persist across sessions
- [ ] Keyboard navigation works
- [ ] Screen reader accessible

---

**Version**: 1.0
**Last Updated**: 2025-01
**Author**: Product Team
