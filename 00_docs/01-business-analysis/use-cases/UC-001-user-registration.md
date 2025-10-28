# UC-001: User Registration

## 1. Brief Description

User registers a new account in RepeatWise system using email and password.

## 2. Actors

- **Primary Actor:** Guest User (user without an account)
- **Secondary Actor:** Email System (future - for email verification)

## 3. Preconditions

- User does not have an existing account in the system
- User has internet connection
- User can access the registration page

## 4. Postconditions

### Success Postconditions

- New user account created in database with hashed password (bcrypt)
- User profile initialized with default values:
  - language: 'VI'
  - theme: 'SYSTEM'
  - timezone: auto-detected or default
- SRS settings initialized with default values
- User_stats record created with zero values
- User redirected to login page
- Success message "Registration successful. Please login." displayed

### Failure Postconditions

- No account created
- Error message displayed to user

## 5. Main Success Scenario (Basic Flow)

1. User accesses Registration page
2. System displays registration form with fields:
   - Email (required)
   - Password (required, min 8 chars)
   - Confirm Password (required, must match password)
   - Name (optional)
3. User enters valid email (e.g., "<user@example.com>")
4. User enters strong password (e 8 characters)
5. User enters matching confirm password
6. User enters name (optional)
7. User clicks "Register" button
8. System validates input:
   - Email format is valid (regex)
   - Password e 8 characters
   - Confirm password matches password
9. System checks email does not exist in database (case-insensitive)
10. System hashes password using bcrypt (cost factor 12)
11. System creates record in `users` table:
    - id: UUID (auto-generated)
    - email: lowercase(input email)
    - password_hash: bcrypt hash
    - name: input name or null
    - timezone: auto-detect or default
    - language: 'VI'
    - theme: 'SYSTEM'
    - created_at, updated_at: current timestamp
12. System creates record in `srs_settings` table with defaults:
    - user_id: new user id
    - total_boxes: 7
    - review_order: 'RANDOM'
    - notification_enabled: true
    - notification_time: '09:00'
    - forgotten_card_action: 'MOVE_TO_BOX_1'
    - new_cards_per_day: 20
    - max_reviews_per_day: 200
13. System creates record in `user_stats` table:
    - user_id: new user id
    - total_cards_learned: 0
    - streak_days: 0
    - last_study_date: null
    - total_study_time_minutes: 0
14. System displays success message "Registration successful. Please login."
15. System redirects user to Login page

## 6. Alternative Flows

### 6a. Email Already Exists

**Trigger:** Step 9 - Email already exists in database

1. System detects email is already registered
2. System returns 400 Bad Request
3. UI displays error message: "Email already exists. Please login or use a different email."
4. User remains on registration page
5. Use case ends (failure)

### 6b. Invalid Email Format

**Trigger:** Step 8 - Email format is invalid

1. System validates email and detects invalid format
2. UI displays inline validation error: "Invalid email format"
3. "Register" button is disabled
4. User must enter valid email
5. Return to Step 3 (Main Flow)

### 6c. Weak Password

**Trigger:** Step 8 - Password < 8 characters

1. System validates password and detects insufficient length
2. UI displays inline validation error: "Password must be at least 8 characters"
3. "Register" button is disabled
4. User must enter valid password
5. Return to Step 4 (Main Flow)

### 6d. Password Mismatch

**Trigger:** Step 8 - Confirm password does not match password

1. System validates and detects password mismatch
2. UI displays inline validation error: "Passwords do not match"
3. "Register" button is disabled
4. User must re-enter confirm password
5. Return to Step 5 (Main Flow)

### 6e. Network Error

**Trigger:** Step 11-13 - Database connection error or server error

1. System encounters error when creating user in database
2. System rolls back transaction (if partially created)
3. System returns 500 Internal Server Error
4. UI displays error message: "Registration failed. Please try again later."
5. User remains on registration page
6. Use case ends (failure)

### 6f. Empty Required Fields

**Trigger:** Step 7 - User clicks Register with empty required fields

1. Client-side validation detects empty required fields
2. UI displays validation errors for each empty field
3. "Register" button is disabled
4. User must fill in required fields
5. Return to Step 3 (Main Flow)

## 7. Special Requirements

### 7.1 Performance

- Response time < 2 seconds (including bcrypt hashing)
- Bcrypt cost factor = 12 (balance security vs performance)

### 7.2 Security

- Password MUST NEVER be stored in plain text
- Password must be hashed using bcrypt before saving to database
- Email must be normalized to lowercase before saving
- Do not disclose whether email exists (optional - can be simplified for MVP)

### 7.3 Usability

- Inline validation for all fields (real-time feedback)
- Password strength indicator (optional - future)
- Show/hide password toggle
- Clear error messages
- Auto-focus on first field

### 7.4 Data Validation

- Email regex: `^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$`
- Password min length: 8 characters
- Name max length: 100 characters
- Trim leading/trailing whitespace from all inputs

## 8. Technology and Data Variations

### 8.1 Database Transaction

- All operations (create user, srs_settings, user_stats) must be in single transaction
- If any operation fails � rollback entire transaction

### 8.2 Email Lowercase Normalization

- Email is always converted to lowercase before:
  - Checking for duplicates
  - Saving to database
- Example: "<User@Example.COM>" � "<user@example.com>"

### 8.3 Password Hashing

- Algorithm: bcrypt
- Cost factor: 12
- Salt is automatically generated and included in hash

## 9. Frequency of Occurrence

- Expected: 10-50 registrations/day (MVP phase)
- Peak: 100-200 registrations/day (post-launch)

## 10. Open Issues

- **Email verification:** MVP does not require email verification. Future version may add email verification flow.
- **CAPTCHA:** MVP does not include CAPTCHA. May need to add if spam registration occurs.
- **Password complexity:** MVP only requires min 8 chars. Future may require uppercase, number, special char.
- **Username:** MVP does not have username, only uses email. Future may add username field.
- **Social login:** OAuth (Google/Facebook) is out of scope for MVP.

## 11. Related Use Cases

- [UC-002: User Login](UC-002-user-login.md) - User logs in after registration
- [UC-005: Update User Profile](UC-005-update-user-profile.md) - User updates profile settings
- [UC-006: Change Password](UC-006-change-password.md) - User changes password

## 12. Business Rules References

- **BR-1.1:** Email Validation
- **BR-1.2:** Password Policy
- **BR-1.4:** User Profile defaults

## 13. UI Mockup Notes

- Form layout: center-aligned, max-width 400px
- Social login buttons (future): Google, Facebook OAuth
- Link to Login page: "Already have an account? Login here"
- Responsive design: mobile, tablet, desktop
- Loading spinner when submitting form

## 14. API Endpoint

```
POST /api/auth/register
```

**Request Body:**

```json
{
  "email": "user@example.com",
  "password": "Password123",
  "confirmPassword": "Password123",
  "name": "John Doe" // optional
}
```

**Success Response (201 Created):**

```json
{
  "message": "Registration successful. Please login.",
  "userId": "uuid-here"
}
```

**Error Responses:**

400 Bad Request - Email exists:

```json
{
  "error": "Email already exists",
  "message": "Email already exists. Please login or use a different email."
}
```

400 Bad Request - Validation error:

```json
{
  "error": "Validation failed",
  "details": [
    {
      "field": "email",
      "message": "Invalid email format"
    },
    {
      "field": "password",
      "message": "Password must be at least 8 characters"
    }
  ]
}
```

500 Internal Server Error:

```json
{
  "error": "Internal server error",
  "message": "Registration failed. Please try again later."
}
```

## 15. Test Cases

### TC-001-001: Successful Registration

- **Given:** Valid email "<newuser@example.com>" not in system
- **When:** User submits valid registration form
- **Then:** Account created, user redirected to login page

### TC-001-002: Email Already Exists

- **Given:** Email "<existing@example.com>" already in system
- **When:** User tries to register with same email
- **Then:** Error "Email already exists" displayed

### TC-001-003: Invalid Email Format

- **Given:** User enters "invalid-email"
- **Then:** Inline validation error "Invalid email format" displayed

### TC-001-004: Weak Password

- **Given:** User enters password "short"
- **Then:** Inline validation error "Password must be at least 8 characters"

### TC-001-005: Password Mismatch

- **Given:** Password "Password123" and Confirm "Password456"
- **Then:** Error "Passwords do not match" displayed

### TC-001-006: Empty Required Fields

- **Given:** User clicks Register with empty email
- **Then:** Validation errors displayed, button disabled
