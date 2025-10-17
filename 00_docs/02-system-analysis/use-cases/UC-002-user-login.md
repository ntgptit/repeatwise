# UC-002: User Login

## 1. Use Case Information

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-002 |
| **Use Case Name** | User Login |
| **Primary Actor** | Registered User |
| **Secondary Actors** | None |
| **Priority** | High (P0) |
| **Complexity** | Low |
| **Status** | MVP |

## 2. Brief Description

Registered user logs into the system using email and password. System authenticates the user and issues a JWT token for accessing protected resources.

## 3. Preconditions

- User has a registered account
- User has internet connection
- System is operational

## 4. Postconditions

**Success**:
- User authenticated successfully
- JWT token issued and stored
- User redirected to dashboard
- Session created

**Failure**:
- User remains on login page
- Error message displayed
- No token issued

## 5. Main Success Scenario

### Step 1: Access Login Page
**Actor**: User opens app and navigates to login

**System**: Displays login form with:
- Email field
- Password field
- "Remember me" checkbox (optional)
- "Forgot password?" link
- "Create account" link

### Step 2: Enter Credentials
**Actor**: User enters:
- Email: `minh@example.com`
- Password: `SecurePass123`

**System**: Validates input format (client-side)

### Step 3: Submit Login
**Actor**: User clicks "Log in" button

**System**:
1. Validates inputs (server-side)
2. Looks up user by email (case-insensitive)
3. Compares password hash using bcrypt
4. If match:
   - Generates JWT token (24h expiry)
   - Updates user.updated_at timestamp
   - Logs event: "User logged in: {email}"
   - Returns token + user profile

### Step 4: Token Storage and Redirect
**System**:
- Stores JWT in localStorage (web) or SecureStorage (mobile)
- Sets Authorization header for future requests
- Shows success message: "Welcome back!"
- Redirects to dashboard

**Actor**: User sees dashboard with their data

## 6. Alternative Flows

### A1: Invalid Email
**Trigger**: Email not found in database (Step 3)

**Flow**:
1. System searches for email
2. No user found
3. System returns generic error: "Invalid email or password"
4. System does NOT reveal which field is wrong (security)
5. User can:
   - Retry with correct email
   - Click "Create account" to register

**Return to**: Step 2

---

### A2: Incorrect Password
**Trigger**: Password doesn't match hash (Step 3)

**Flow**:
1. System finds user by email
2. bcrypt comparison fails
3. System increments failed_login_attempts counter
4. System returns generic error: "Invalid email or password"
5. If failed_attempts >= 5:
   - System temporarily locks account (15 minutes)
   - System shows: "Too many failed attempts. Try again in 15 minutes."
6. User can:
   - Retry with correct password
   - Click "Forgot password?" link

**Return to**: Step 2

---

### A3: Account Locked (Future)
**Trigger**: Too many failed login attempts (Step 3)

**Flow**:
1. System checks failed_login_attempts count
2. If count >= 5 and last_failed_at < 15 minutes ago:
   - System returns error: "Account temporarily locked. Try again in X minutes."
3. User must wait for lockout period to expire
4. After 15 minutes, counter resets

**End Use Case**

---

### A4: Network Error
**Trigger**: Connection lost during login (Step 3)

**Flow**:
1. Request times out
2. System shows error: "Connection lost. Please try again."
3. Form data retained
4. User clicks "Retry" button

**Return to**: Step 3

## 7. Exception Flows

### E1: Database Unavailable
**Trigger**: Cannot connect to database (Step 3)

**Flow**:
1. System detects database error
2. System logs critical error
3. System shows: "Service unavailable. Please try again later."
4. Alert sent to ops team

**End Use Case**

## 8. Special Requirements

### Performance
- Login request completes in < 300ms (p95)
- bcrypt comparison should not block for too long

### Security
- Password never sent/logged in plain text
- Generic error messages (don't reveal if email exists)
- Rate limiting: Max 5 attempts per IP per 15 minutes
- Account lockout after 5 failed attempts (15 min cooldown)
- JWT signed with secret key (HS256)
- HTTPS required in production

### Usability
- Auto-focus on email field
- Enter key submits form
- Show password toggle (eye icon)
- Remember email (optional)
- Clear error messages

## 9. Business Rules

### BR-005: Authentication
- Email comparison is case-insensitive
- Password comparison uses bcrypt.compare()
- Failed attempts counter resets on successful login

### BR-006: JWT Token
- Expires in 24 hours for MVP
- Contains: user_id, email, issued_at, expires_at
- Signed with HS256 algorithm
- No refresh token in MVP

### BR-007: Session Management
- Single device only (MVP)
- Logout invalidates token client-side only (MVP)
- Server-side token blacklist (Future)

## 10. Data Requirements

### Input
- Email: VARCHAR(255)
- Password: String (raw, never stored)

### Output
- JWT token: String
- User: { id, email, name, language, theme, timezone }

### Database Changes
- UPDATE users SET updated_at = NOW() WHERE id = ?
- (Future) UPDATE failed_login_attempts

## 11. Testing Scenarios

### Happy Path
1. Enter valid email and password
2. Submit
3. Token received
4. Redirected to dashboard

### Error Cases
1. Wrong email → Generic error
2. Wrong password → Generic error
3. Empty fields → Validation error
4. 5 failed attempts → Account locked
5. Network timeout → Retry option

## 12. Related Use Cases

- **UC-001**: User Registration
- **UC-003**: User Profile Management
- **UC-004**: Password Reset (Future)

## 13. Acceptance Criteria

- [ ] User can login with valid credentials
- [ ] JWT token issued on successful login
- [ ] Generic error for invalid credentials
- [ ] Rate limiting prevents brute force
- [ ] Token stored securely
- [ ] Login completes in < 300ms (p95)
- [ ] Failed login attempts tracked
- [ ] Account lockout after 5 failures

---

**Version**: 1.0
**Last Updated**: 2025-01
