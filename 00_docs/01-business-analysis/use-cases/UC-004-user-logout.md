# UC-004: User Logout

## 1. Brief Description

User logs out from RepeatWise system, terminating current session by revoking refresh token and clearing authentication state.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** Authentication Service

## 3. Preconditions

- User is logged in (has valid access token)
- User has valid refresh token stored in HTTP-only cookie

## 4. Postconditions

### Success Postconditions

- Refresh token revoked in database (revoked_at set to current timestamp)
- HTTP-only cookie cleared/invalidated
- Access token cleared from client memory
- User session terminated
- User redirected to Login page
- Success message displayed: "Logged out successfully"

### Failure Postconditions

- User may still be in logged-in state (client-side)
- Error message displayed
- Session may or may not be terminated server-side

## 5. Main Success Scenario (Basic Flow)

1. User is logged in and using the application
2. User clicks "Logout" button (in header, menu, or settings)
3. Optional: Confirmation dialog appears "Are you sure you want to logout?" (can be skipped for better UX)
4. User confirms logout action
5. Client calls POST /api/auth/logout
6. Refresh token from HTTP-only cookie automatically sent with request
7. Access token sent in Authorization header for authentication
8. Backend receives logout request
9. System validates access token to identify user
10. System extracts refresh token from cookie
11. System queries `refresh_tokens` table for matching token
12. System finds refresh token record
13. System marks refresh token as revoked:
    - UPDATE refresh_tokens SET revoked_at = CURRENT_TIMESTAMP WHERE token_hash = ?
14. System returns 200 OK response
15. System sends Set-Cookie header to clear refresh token:
    - Set-Cookie: refresh_token=; HttpOnly; Secure; Max-Age=0; Path=/api/auth
16. Client receives successful response
17. Client clears authentication state:
    - Access token removed from memory (state management)
    - Auth context/store reset
    - User object cleared
18. Client redirects to Login page
19. Client displays success message: "Logged out successfully"
20. User sees Login page

## 6. Alternative Flows

### 6a. Logout Without Valid Access Token

**Trigger:** Step 9 - Access token invalid or expired

1. System attempts to validate access token
2. Token is invalid or expired
3. System still proceeds with logout (best effort)
4. If refresh token in cookie is valid, revoke it
5. System returns 200 OK (logout should always succeed from UX perspective)
6. Client clears auth state and redirects to Login
7. Use case continues from Step 17

**Rationale:** Logout should succeed even with invalid tokens for better UX

### 6b. No Refresh Token in Cookie

**Trigger:** Step 10 - Refresh token not present in cookie

1. System checks request cookies
2. refresh_token cookie not found
3. System logs warning but does not fail
4. System returns 200 OK (nothing to revoke)
5. Client clears auth state and redirects to Login
6. Use case continues from Step 17

**Rationale:** Client-side logout still valid even without server-side token

### 6c. Refresh Token Already Revoked

**Trigger:** Step 12-13 - Refresh token already revoked

1. System queries refresh_tokens table
2. Token found but revoked_at IS NOT NULL
3. System logs info (already revoked, no action needed)
4. System returns 200 OK (idempotent operation)
5. Client clears auth state and redirects to Login
6. Use case continues from Step 17

**Rationale:** Logout is idempotent - multiple logout attempts should succeed

### 6d. Refresh Token Not Found

**Trigger:** Step 12 - Refresh token not found in database

1. System queries refresh_tokens table
2. No matching token found
3. System logs warning
4. System returns 200 OK (nothing to revoke)
5. Client clears auth state and redirects to Login
6. Use case continues from Step 17

### 6e. Database Error

**Trigger:** Step 13 - Database error when revoking token

1. System attempts to update refresh_tokens table
2. Database error occurs (connection lost, etc.)
3. System logs error
4. System returns 500 Internal Server Error
5. Client still clears auth state (logout client-side)
6. Client redirects to Login with warning message
7. Use case continues from Step 17

**Rationale:** Client-side logout proceeds even if server-side fails

### 6f. Network Error

**Trigger:** Step 5 - Network request fails

1. Client sends logout request
2. Network error occurs (no internet, timeout, etc.)
3. Client catches error
4. Client still clears auth state locally (optimistic logout)
5. Client redirects to Login
6. Client displays message: "Logged out (offline)"
7. Use case ends

**Note:** Refresh token still valid on server until next successful logout/refresh attempt

## 7. Special Requirements

### 7.1 Performance

- Response time < 200ms
- Database update should be fast (indexed query)
- Logout should feel instant to user

### 7.2 Security

- **Token revocation:** Refresh token marked as unusable
- **Cookie clearing:** Remove refresh token cookie
- **Idempotent:** Multiple logout attempts safe
- **Graceful degradation:** Works even with invalid tokens
- **No sensitive data in response:** Don't expose token details

### 7.3 Usability

- **Always succeeds:** Logout should never "fail" from user perspective
- **Immediate feedback:** User sees login page immediately
- **Clear state:** All client-side auth data cleared
- **Success message:** Confirm logout completed
- **Optional confirmation:** Can add "Are you sure?" dialog

### 7.4 Client-Side Cleanup

Client must clear:

- Access token from memory (React state, Zustand, etc.)
- Auth context (user object, isAuthenticated flag)
- Any cached user data
- Query cache (React Query) - optional, depends on implementation

## 8. Technology and Data Variations

### 8.1 Cookie Clearing

Set-Cookie header to clear cookie:

```http
Set-Cookie: refresh_token=; HttpOnly; Secure; SameSite=Strict; Max-Age=0; Path=/api/auth
```

- Empty value
- Max-Age=0 (expires immediately)
- Same Path and domain as original cookie

### 8.2 Database Update

```sql
UPDATE refresh_tokens
SET revoked_at = CURRENT_TIMESTAMP
WHERE token_hash = ?
  AND user_id = ?
  AND revoked_at IS NULL;
```

- Use token_hash to find record
- Set revoked_at timestamp
- Only update if not already revoked (optimization)

### 8.3 Client-Side Implementation

```typescript
const logout = async () => {
  try {
    // Call logout endpoint
    await axios.post('/api/auth/logout');
  } catch (error) {
    // Log error but continue with client-side logout
    console.error('Logout request failed:', error);
  } finally {
    // Always clear client state (even if API fails)
    clearAccessToken();
    clearAuthContext();
    clearQueryCache(); // optional
    navigate('/login');
    showSuccessMessage('Logged out successfully');
  }
};
```

## 9. Frequency of Occurrence

- Expected: 1-3 logouts per user per day
- Total: 100-500 logout requests/day (MVP phase)
- Peak: 1000+ logout requests/day (post-launch)

## 10. Open Issues

- **Logout all devices:** Separate use case to revoke all refresh tokens (future)
- **Session expiry notification:** Notify user before session expires (future)
- **Remember last page:** Save current page to redirect after re-login (future)
- **Logout confirmation:** Add optional confirmation dialog
- **Audit log:** Log logout events for security tracking (future)

## 11. Related Use Cases

- [UC-002: User Login](UC-002-user-login.md) - User logs in after logout
- [UC-003: Refresh Access Token](UC-003-refresh-token.md) - Revoked token cannot be refreshed
- [UC-006: Change Password](UC-006-change-password.md) - Password change logs out all sessions

## 12. Business Rules References

- **BR-1.3:** Token Management (token revocation)

## 13. API Endpoint

```http
POST /api/auth/logout
```

**Request Headers:**

```http
Authorization: Bearer <access-token>
Cookie: refresh_token=<secure-token>
```

**Success Response (200 OK):**

```json
{
  "message": "Logged out successfully"
}
```

**Set-Cookie Header:**

```http
Set-Cookie: refresh_token=; HttpOnly; Secure; SameSite=Strict; Max-Age=0; Path=/api/auth
```

**Response (500 Internal Server Error - rare):**

```json
{
  "error": "INTERNAL_SERVER_ERROR",
  "message": "Logout failed on server, but you have been logged out locally."
}
```

**Note:** Client should treat 500 as success for logout (optimistic approach)

## 14. Test Cases

### TC-004-001: Successful Logout

- **Given:** User logged in with valid tokens
- **When:** User clicks logout
- **Then:** Refresh token revoked, cookie cleared, redirected to login

### TC-004-002: Logout with Expired Access Token

- **Given:** Access token expired but refresh token valid
- **When:** User clicks logout
- **Then:** Logout succeeds, refresh token revoked

### TC-004-003: Logout Without Refresh Token

- **Given:** Refresh token cookie missing or cleared
- **When:** User clicks logout
- **Then:** Logout succeeds, client state cleared, redirected to login

### TC-004-004: Multiple Logout Attempts

- **Given:** User already logged out
- **When:** User clicks logout again (edge case)
- **Then:** Logout succeeds (idempotent), no error

### TC-004-005: Logout with Network Error

- **Given:** User logged in but offline
- **When:** User clicks logout
- **Then:** Client-side logout succeeds, redirected to login

### TC-004-006: Logout with Database Error

- **Given:** Database temporarily unavailable
- **When:** User clicks logout
- **Then:** Client-side logout succeeds, warning logged

### TC-004-007: Cookie Cleared After Logout

- **Given:** User logged out successfully
- **When:** Checking browser cookies
- **Then:** refresh_token cookie not present or expired

### TC-004-008: Revoked Token Cannot Be Used

- **Given:** User logged out successfully
- **When:** Attempting to use old refresh token
- **Then:** Token refresh fails with 401 error

## 15. Additional Features (Future)

### Logout All Devices

Separate endpoint to revoke all refresh tokens:

```http
POST /api/auth/logout-all
```

Implementation:

```sql
UPDATE refresh_tokens
SET revoked_at = CURRENT_TIMESTAMP
WHERE user_id = ?
  AND revoked_at IS NULL;
```

Use cases:

- User suspects account compromise
- User wants to terminate all active sessions
- User changes password (auto-trigger)

### Session Management UI

Future feature to show active sessions:

- List all devices/browsers with active sessions
- Show last activity time
- Allow selective logout per session
- Show session details (IP, location, device)

## 16. Security Considerations

### 16.1 Token Revocation

- Refresh token immediately unusable after logout
- Access token still valid until expiry (15 min) - acceptable risk
- For critical operations, can add server-side token blacklist (future)

### 16.2 Optimistic Logout

- Client clears state even if server request fails
- Prevents user from being "stuck" logged in
- User can always re-login if needed

### 16.3 No Token in Response

- Never return token value in logout response
- Only return generic success message
- Minimize information leakage

### 16.4 Audit Trail

- Log logout events (future):
  - user_id, logout_time, ip_address, user_agent
  - Useful for security analysis and debugging
