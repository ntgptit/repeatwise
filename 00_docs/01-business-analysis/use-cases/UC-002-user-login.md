# UC-002: User Login

## 1. Brief Description

Registered user logs into RepeatWise system using username or email and password, receiving access token and refresh token for authenticated sessions.

## 2. Actors

- **Primary Actor:** Registered User
- **Secondary Actor:** Authentication Service, Token Service

## 3. Preconditions

- User has a registered account in the system
- User has internet connection
- User can access the login page
- User account is not locked/suspended

## 4. Postconditions

### Success Postconditions

- User is authenticated
- Access token (JWT) generated with 15 minutes expiry
- Refresh token generated with 7 days expiry
- Refresh token saved to database (hashed with bcrypt)
- Refresh token set in HTTP-only cookie
- Access token returned in response body
- User redirected to Dashboard
- User session is active

### Failure Postconditions

- User is not authenticated
- No tokens generated
- Error message displayed
- User remains on login page

## 5. Main Success Scenario (Basic Flow)

1. User accesses Login page
2. System displays login form with fields:
   - Username or Email (required)
   - Password (required)
   - Remember me checkbox (optional - future)
3. User enters username or email (e.g., "john_doe123" or "<user@example.com>")
4. User enters password "Password123"
5. User clicks "Login" button
6. System validates input format:
   - Username/Email is not empty
   - Password is not empty
7. System determines if input is email or username:
   - If input contains "@" symbol: treat as email
   - Otherwise: treat as username
8. System normalizes input:
   - If email: convert to lowercase
   - If username: keep as provided (case-sensitive)
9. System queries database for user:
   - If email: query by email (case-insensitive)
   - If username: query by username (case-sensitive)
10. System retrieves user record with password_hash
11. System compares input password with stored password_hash using bcrypt.compare()
12. Password match confirmed
13. System generates access token (JWT):
    - Payload: { userId, email, username, iat, exp }
    - Expiry: 15 minutes (900 seconds)
    - Signed with secret key
14. System generates refresh token:
    - Random secure token (UUID or crypto.randomBytes)
    - Expiry: 7 days
15. System hashes refresh token with bcrypt
16. System saves refresh token to `refresh_tokens` table:
    - id: UUID
    - user_id: user id
    - token_hash: bcrypt(refresh_token)
    - expires_at: current time + 7 days
    - created_at: current timestamp
17. System returns response:
    - Body: { access_token, expires_in: 900, user: {...} }
    - Set-Cookie: refresh_token=<token>; HttpOnly; Secure; SameSite=Strict; Max-Age=604800
18. Client stores access token in memory (not localStorage for security)
19. System redirects user to Dashboard
20. User sees Dashboard with personalized content

## 6. Alternative Flows

### 6a. Invalid Credentials - Username/Email Not Found

**Trigger:** Step 9 - Username or Email does not exist in database

1. System queries database and finds no matching username or email
2. System returns 401 Unauthorized
3. UI displays generic error: "Invalid username/email or password"
4. User remains on login page
5. Use case ends (failure)

**Note:** Use generic error message to avoid disclosing whether username/email exists (security best practice)

### 6b. Invalid Credentials - Wrong Password

**Trigger:** Step 10-11 - Password does not match

1. System compares password with hash
2. bcrypt.compare() returns false
3. System returns 401 Unauthorized
3. UI displays generic error: "Invalid username/email or password"
5. User remains on login page
6. Use case ends (failure)

### 6c. Empty Required Fields

**Trigger:** Step 6 - User clicks Login with empty fields

1. Client-side validation detects empty fields
2. UI displays validation errors
3. "Login" button disabled until fields filled
4. Return to Step 3 (Main Flow)

### 6d. Database Connection Error

**Trigger:** Step 8 or 15 - Database error

1. System encounters database connection error
2. System logs error
3. System returns 500 Internal Server Error
4. UI displays error: "Login failed. Please try again later."
5. User remains on login page
6. Use case ends (failure)

### 6e. Token Generation Failed

**Trigger:** Step 12-14 - Error generating tokens

1. System fails to generate or sign tokens
2. System logs error
3. System returns 500 Internal Server Error
4. UI displays error: "Login failed. Please try again later."
5. Use case ends (failure)

### 6f. Account Locked (Future)

**Trigger:** Step 9 - User account is locked due to multiple failed attempts

1. System detects account status = 'LOCKED'
2. System returns 403 Forbidden
3. UI displays error: "Account locked due to multiple failed login attempts. Please contact support or reset your password."
4. Use case ends (failure)

**Note:** Account locking is future feature, not in MVP

## 7. Special Requirements

### 7.1 Performance

- Response time < 1 second (including bcrypt comparison)
- bcrypt compare should be fast (previous cost factor 12 for hashing)

### 7.2 Security

- **Password comparison:** Use bcrypt.compare() - timing-safe comparison
- **Generic error messages:** Don't disclose if email exists
- **HTTPS only:** All login requests must use HTTPS in production
- **Refresh token security:**
  - Stored in HTTP-only cookie (not accessible by JavaScript)
  - Secure flag enabled (HTTPS only)
  - SameSite=Strict (CSRF protection)
- **Access token security:**
  - Stored in memory, not localStorage (XSS protection)
  - Short expiry (15 minutes)
- **Rate limiting:** 5 login attempts per minute per IP (future)
- **Brute force protection:** Account lockout after 5 failed attempts (future)

### 7.3 Usability

- Show/hide password toggle
- Clear error messages
- Loading spinner during authentication
- Auto-focus on email field
- Tab navigation between fields
- Enter key submits form

### 7.4 Token Management

- Access token payload:

  ```json
  {
    "userId": "uuid",
    "email": "user@example.com",
    "username": "john_doe123",
    "iat": 1234567890,
    "exp": 1234568790
  }
  ```

- Refresh token: Random secure string (32+ bytes)

## 8. Technology and Data Variations

### 8.1 JWT Configuration

- Algorithm: HS256 (HMAC with SHA-256)
- Secret key: Stored in environment variable
- Access token expiry: 900 seconds (15 minutes)

### 8.2 Cookie Configuration

- Name: `refresh_token`
- HttpOnly: true (not accessible by JavaScript)
- Secure: true (HTTPS only in production)
- SameSite: Strict (CSRF protection)
- Max-Age: 604800 seconds (7 days)
- Path: /api/auth (restrict to auth endpoints)

### 8.3 Database Query Optimization

- Index on users.email for fast email lookup
- Index on users.username for fast username lookup
- Single query to fetch user with password_hash (by email or username)

## 9. Frequency of Occurrence

- Expected: 100-500 logins/day (MVP phase)
- Peak: 1000-5000 logins/day (post-launch)
- Each user logs in 1-3 times per day (depending on token expiry)

## 10. Open Issues

- **OAuth integration:** Google/Facebook login is out of scope for MVP
- **Two-factor authentication (2FA):** Not in MVP, future security enhancement
- **Remember me:** Not in MVP (all sessions use same 7-day refresh token)
- **Session management:** Single device only in MVP, multi-device session management in future
- **Password reset flow:** Separate use case (future)
- **Account lockout:** After N failed attempts (future)

## 11. Related Use Cases

- [UC-001: User Registration](UC-001-user-registration.md) - User creates account before login
- [UC-003: Refresh Access Token](UC-003-refresh-token.md) - User refreshes expired access token
- [UC-004: User Logout](UC-004-user-logout.md) - User logs out and revokes tokens
- [UC-006: Change Password](UC-006-change-password.md) - User changes password (requires login)

## 12. Business Rules References

- **BR-1.2:** Password Policy (verification)
- **BR-1.3:** Token Management (access token, refresh token)

## 13. UI Mockup Notes

- Form layout: center-aligned, max-width 400px
- Link to Registration: "Don't have an account? Sign up"
- Link to Password Reset: "Forgot password?" (future)
- Social login buttons: Google, Facebook (future)
- Responsive design: mobile, tablet, desktop

## 14. API Endpoint

```
POST /api/auth/login
```

**Request Body:**

```json
{
  "usernameOrEmail": "john_doe123",
  "password": "Password123"
}
```

**Alternative (with email):**

```json
{
  "usernameOrEmail": "user@example.com",
  "password": "Password123"
}
```

**Success Response (200 OK):**

```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expires_in": 900,
  "user": {
    "id": "uuid",
    "email": "user@example.com",
    "username": "john_doe123",
    "name": "John Doe",
    "language": "VI",
    "theme": "SYSTEM",
    "timezone": "Asia/Ho_Chi_Minh"
  }
}
```

**Set-Cookie Header:**

```
Set-Cookie: refresh_token=<secure-token>; HttpOnly; Secure; SameSite=Strict; Max-Age=604800; Path=/api/auth
```

**Error Responses:**

401 Unauthorized - Invalid credentials:

```json
{
  "error": "Invalid credentials",
  "message": "Invalid email or password"
}
```

400 Bad Request - Validation error:

```json
{
  "error": "Validation failed",
  "details": [
    {
      "field": "usernameOrEmail",
      "message": "Username or email is required"
    }
  ]
}
```

500 Internal Server Error:

```json
{
  "error": "Internal server error",
  "message": "Login failed. Please try again later."
}
```

403 Forbidden - Account locked (future):

```json
{
  "error": "Account locked",
  "message": "Account locked due to multiple failed login attempts."
}
```

## 15. Test Cases

### TC-002-001: Successful Login with Email

- **Given:** User with email "<user@example.com>" and password "Password123" exists
- **When:** User submits valid email and password
- **Then:** Access token returned, refresh token set in cookie, redirected to dashboard

### TC-002-001b: Successful Login with Username

- **Given:** User with username "john_doe123" and password "Password123" exists
- **When:** User submits valid username and password
- **Then:** Access token returned, refresh token set in cookie, redirected to dashboard

### TC-002-002: Wrong Password

- **Given:** User with username "john_doe123" or email "<user@example.com>" exists
- **When:** User submits wrong password "WrongPass"
- **Then:** 401 error with message "Invalid username/email or password"

### TC-002-003: Username/Email Not Found

- **Given:** Username "nonexistent" or email "<nonexistent@example.com>" does not exist
- **When:** User submits credentials
- **Then:** 401 error with generic message "Invalid username/email or password"

### TC-002-004: Empty Username/Email

- **Given:** User leaves username/email field empty
- **When:** User clicks Login
- **Then:** Validation error "Username or email is required"

### TC-002-005: Empty Password

- **Given:** User leaves password field empty
- **When:** User clicks Login
- **Then:** Validation error "Password is required"

### TC-002-006: Case Insensitive Email

- **Given:** User registered with "<User@Example.COM>"
- **When:** User logs in with "<user@example.com>"
- **Then:** Login successful (email normalized to lowercase)

### TC-002-007: Case Sensitive Username

- **Given:** User registered with username "JohnDoe"
- **When:** User logs in with "johndoe"
- **Then:** 401 error (username is case-sensitive)

### TC-002-008: Email Format Detection

- **Given:** User with username "test@example" exists
- **When:** User logs in with "test@example"
- **Then:** System treats as email (checks email field), login fails if no email match

### TC-002-009: Token Expiry

- **Given:** User logged in successfully
- **When:** Access token expires after 15 minutes
- **Then:** Subsequent API calls return 401, client should refresh token

### TC-002-010: Refresh Token in Cookie

- **Given:** User logged in successfully
- **When:** Response received
- **Then:** Refresh token present in HTTP-only cookie with correct flags

## 16. Security Considerations

### 16.1 Token Storage Best Practices

- **Access Token:** Store in memory (React state, Zustand store)
  - Fast access for API calls
  - Cleared on page refresh (acceptable for 15-min expiry)
  - Not vulnerable to XSS
- **Refresh Token:** Store in HTTP-only cookie
  - Not accessible by JavaScript (XSS protection)
  - Automatically sent with requests to /api/auth/*
  - Secure flag for HTTPS

### 16.2 CSRF Protection

- SameSite=Strict cookie attribute
- Refresh endpoint only accepts POST requests
- Origin/Referer header validation (optional)

### 16.3 Brute Force Mitigation (Future)

- Rate limiting: 5 attempts per minute per IP
- Account lockout: After 5 failed attempts in 15 minutes
- CAPTCHA: After 3 failed attempts
- Email notification: When suspicious login activity detected
