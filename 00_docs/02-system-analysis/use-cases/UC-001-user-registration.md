# UC-001: User Registration

## 1. Use Case Information

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-001 |
| **Use Case Name** | User Registration |
| **Primary Actor** | New User (Guest) |
| **Secondary Actors** | Email Service |
| **Priority** | High (P0) |
| **Complexity** | Medium |
| **Status** | MVP |

## 2. Brief Description

New user creates an account by providing username, email, and password. System validates the information, creates the account with default settings, and logs the user in automatically.

## 3. Preconditions

- User has internet connection
- User does not have an existing account with the email
- System is operational

## 4. Postconditions

**Success**:
- User account created in database
- Default SRS settings created for user
- User stats record initialized
- User logged in with JWT token
- User redirected to onboarding/dashboard

**Failure**:
- No account created
- User remains on registration page with error message

## 5. Main Success Scenario

### Step 1: Access Registration Page
**Actor**: User opens app and clicks "Sign Up" or "Create Account"

**System**:
- Displays registration form with fields:
  - Username (required)
  - Email (required)
  - Password (required)
  - Confirm Password (required)
  - Name (optional)
- Shows link to Terms of Service and Privacy Policy

### Step 2: Enter Registration Information
**Actor**: User enters:
- Username: `minhnguyendev`
- Email: `minh@example.com`
- Password: `SecurePass123`
- Confirm Password: `SecurePass123`
- Name: `Nguyễn Văn Minh` (optional)

**System**:
- Validates input in real-time:
  - Username format validation (3-30 chars, alphanumeric + underscore, client-side)
  - Email format validation (client-side)
  - Password strength indicator (client-side)
  - Confirm password match check (client-side)

### Step 3: Submit Registration
**Actor**: User clicks "Create Account" button

**System**:
1. Validates all inputs (server-side):
   - Username format is valid (3-30 chars, alphanumeric + underscore)
   - Username not already taken
   - Email format is valid
   - Email not already registered
   - Password meets minimum requirements (≥8 characters)
   - Passwords match
2. Hashes password with bcrypt (cost factor 12)
3. Generates UUID for user
4. Creates user record in database
5. Creates default SRS settings:
   - total_boxes = 7
   - review_order = RANDOM
   - notification_enabled = TRUE
   - notification_time = 09:00
   - forgotten_card_action = MOVE_TO_BOX_1
   - new_cards_per_day = 20
   - max_reviews_per_day = 200
6. Creates user_stats record (all zeros)
7. Generates JWT token (24h expiry)
8. Logs event: "User registered: {username} ({email})"

### Step 4: Auto Login and Redirect
**System**:
- Returns JWT token to client
- Stores token in local storage (web) or secure storage (mobile)
- Shows success message: "Welcome to RepeatWise!"
- Redirects to onboarding tour (first-time users)

**Actor**: User sees welcome screen and starts onboarding

## 6. Alternative Flows

### A1: Username Already Taken
**Trigger**: User enters username that already exists (Step 2)

**Flow**:
1. System detects duplicate username during validation
2. System returns error: "This username is already taken"
3. System suggests alternative usernames (e.g., "minhnguyendev2", "minhnguyendev_")
4. User tries different username

**Return to**: Step 2

---

### A2: Email Already Registered
**Trigger**: User enters email that already exists (Step 2)

**Flow**:
1. System detects duplicate email during validation
2. System returns error: "This email is already registered"
3. System suggests: "Already have an account? [Log in here](#)"
4. User can either:
   - Try different email
   - Navigate to login page

**Return to**: Step 2

---

### A3: Invalid Username Format
**Trigger**: User enters invalid username (Step 2)

**Flow**:
1. System validates username format
2. System returns error: "Username must be 3-30 characters, letters, numbers, and underscores only"
3. System highlights username field in red
4. User corrects username format

**Return to**: Step 2

---

### A4: Password Too Weak
**Trigger**: User enters password < 8 characters (Step 2)

**Flow**:
1. System validates password length
2. System returns error: "Password must be at least 8 characters"
3. System highlights password field in red
4. User enters stronger password

**Return to**: Step 2

---

### A5: Passwords Don't Match
**Trigger**: Password and Confirm Password fields don't match (Step 2)

**Flow**:
1. System compares password fields
2. System returns error: "Passwords do not match"
3. System highlights confirm password field in red
4. User corrects confirm password

**Return to**: Step 2

---

### A6: Invalid Email Format
**Trigger**: User enters invalid email (Step 2)

**Flow**:
1. System validates email format (client-side first)
2. System returns error: "Please enter a valid email address"
3. System shows example: "example@email.com"
4. User corrects email format

**Return to**: Step 2

---

### A7: Network Connection Lost
**Trigger**: Network error during registration submission (Step 3)

**Flow**:
1. System attempts to submit registration
2. Request times out or fails
3. System shows error: "Connection lost. Please check your internet and try again."
4. System keeps form data (don't clear fields)
5. User clicks "Retry" or fixes connection
6. System resubmits request

**Return to**: Step 3

---

### A8: Server Error
**Trigger**: Internal server error during account creation (Step 3)

**Flow**:
1. System encounters error during database operation
2. System rolls back transaction (no partial account created)
3. System logs error with stack trace
4. System returns error: "Something went wrong. Please try again later."
5. System shows support contact: "If problem persists, contact support@repeatwise.com"
6. User can retry later

**End Use Case**

## 7. Exception Flows

### E1: Database Connection Failure
**Trigger**: Cannot connect to database (Step 3)

**Flow**:
1. System detects database connection error
2. System logs critical error
3. System returns error: "Service temporarily unavailable. Please try again in a few minutes."
4. System sends alert to operations team
5. User waits and retries later

**End Use Case**

---

### E2: Email Service Unavailable
**Trigger**: Cannot send verification email (Future feature, not MVP)

**Flow**:
1. Account created successfully
2. Email service fails
3. System logs warning
4. User account remains active (email verification not required for MVP)
5. User can still use the app

**Continue to**: Step 4

## 8. Special Requirements

### Performance
- Registration completes in < 500ms (p95)
- Password hashing should not block UI (async)

### Security
- Password hashed with bcrypt (cost factor 12)
- Never log or display plain text password
- Rate limiting: Max 5 registration attempts per IP per hour
- CAPTCHA (Future, not MVP)

### Usability
- Form auto-focus on email field
- Show password strength indicator
- Toggle password visibility (eye icon)
- Clear error messages inline with fields
- Remember name field if re-registration needed

### Accessibility
- Form fields have proper labels
- Error messages announced to screen readers
- Keyboard navigation supported (Tab, Enter to submit)

## 9. Business Rules

### BR-001: Username Uniqueness
- Each username can only be used once
- Case-insensitive comparison (minhdev == MINHDEV)
- Valid characters: letters (a-z, A-Z), numbers (0-9), underscore (_)
- Length: 3-30 characters
- Cannot start or end with underscore

### BR-002: Email Uniqueness
- Each email can only register once
- Case-insensitive comparison (minh@example.com == MINH@EXAMPLE.COM)

### BR-003: Password Requirements
- Minimum 8 characters
- Maximum 128 characters
- No special character requirements for MVP
- Future: Require mix of uppercase, lowercase, numbers

### BR-004: Default Settings
- New users get default SRS settings
- Default language: Vietnamese (VI)
- Default theme: System
- Default timezone: Asia/Ho_Chi_Minh (detected from browser if possible)

### BR-005: Auto Login
- After successful registration, user is automatically logged in
- JWT token valid for 24 hours
- No email verification required for MVP

## 10. Data Requirements

### Input Data
- Username: VARCHAR(30), required, unique
- Email: VARCHAR(255), required, unique
- Password: String (8-128 chars), required
- Name: VARCHAR(100), optional

### Output Data
- JWT token: String
- User object: { id, username, email, name, language, theme, timezone }

### Database Changes
- INSERT into `users` table
- INSERT into `srs_settings` table
- INSERT into `user_stats` table

## 11. UI Mockup Notes

### Registration Form Layout
```
┌─────────────────────────────────────────┐
│          Welcome to RepeatWise          │
│   Simple flashcards. Smart reviews.    │
├─────────────────────────────────────────┤
│                                         │
│  Username                               │
│  [_________________________________]    │
│                                         │
│  Email Address                          │
│  [_________________________________]    │
│                                         │
│  Password                               │
│  [_________________________________] 👁  │
│  ▓▓▓▓░░░░ Weak                         │
│                                         │
│  Confirm Password                       │
│  [_________________________________] 👁  │
│                                         │
│  Name (Optional)                        │
│  [_________________________________]    │
│                                         │
│  [ Create Account ]                     │
│                                         │
│  Already have an account? [Log in]      │
│                                         │
│  By signing up, you agree to our        │
│  [Terms of Service] and [Privacy Policy]│
└─────────────────────────────────────────┘
```

## 12. Testing Scenarios

### Happy Path
1. Enter valid username, email, strong password, matching confirm, name
2. Submit form
3. Account created successfully
4. User logged in and redirected to dashboard

### Edge Cases
1. Username with underscores (minh_nguyen_dev) ✅
2. Email with special characters (test+user@example.com) ✅
3. Very long password (128 characters) ✅
4. Unicode characters in name (Nguyễn Văn Minh) ✅
5. Whitespace in username/email (should be trimmed) ✅
6. Concurrent registrations with same username or email (one should fail) ✅

### Error Cases
1. Duplicate username → Show error, suggest alternatives
2. Duplicate email → Show error, suggest login
3. Invalid username format → Show error inline
4. Weak password → Show error, suggest stronger
5. Mismatched passwords → Show error, highlight field
6. Invalid email format → Show error inline
7. Network timeout → Show error, keep form data
8. Server error → Show generic error, log details

## 13. Related Use Cases

- **UC-002**: User Login - User logs in after registration
- **UC-003**: User Profile Management - User updates profile
- **UC-004**: Password Reset - User forgot password (Future)

## 14. Notes & Assumptions

### Assumptions
- Email verification NOT required for MVP (Future enhancement)
- No CAPTCHA for MVP (add if spam is an issue)
- No social login (OAuth) for MVP
- Single role "USER" for MVP (no admin roles)

### Future Enhancements
- Email verification flow
- OAuth (Google, Facebook)
- CAPTCHA for bot prevention
- Password strength requirements (uppercase, numbers, symbols)

## 15. Acceptance Criteria

- [ ] User can register with valid username, email and password
- [ ] Duplicate username registration is prevented
- [ ] Duplicate email registration is prevented
- [ ] Password is hashed with bcrypt (cost 12)
- [ ] Default SRS settings are created
- [ ] User is auto-logged in after registration
- [ ] JWT token is returned and stored
- [ ] Validation errors are displayed clearly
- [ ] Form data is retained on validation errors
- [ ] Registration completes in < 500ms (p95)
- [ ] Rate limiting prevents abuse (5 attempts/hour/IP)

---

**Version**: 1.0
**Last Updated**: 2025-01
**Author**: Product Team
