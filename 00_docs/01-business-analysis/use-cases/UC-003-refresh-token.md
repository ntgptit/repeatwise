# UC-003: Refresh Access Token

## 1. Brief Description

The system automatically refreshes an expired access token using a valid refresh token, providing seamless authentication without requiring user to log in again.

## 2. Actors

- **Primary Actor:** Authenticated User (with expired access token)
- **Secondary Actor:** Authentication Service, Token Service

## 3. Preconditions

- User previously logged in successfully
- User has valid refresh token stored in HTTP-only cookie
- Refresh token has not expired (within 7 days)
- Refresh token has not been revoked

## 4. Postconditions

### Success Postconditions

- New access token generated with 15 minutes expiry
- New refresh token generated (token rotation)
- Old refresh token marked as revoked in database
- New refresh token saved to database (hashed)
- New refresh token set in HTTP-only cookie
- New access token returned in response
- User session continues seamlessly

### Failure Postconditions

- No new tokens generated
- User remains unauthenticated
- User must log in again

## 5. Main Success Scenario (Basic Flow)

1. User is actively using the application
2. Access token expires after 15 minutes
3. Client makes API request with expired access token
4. Backend API returns 401 Unauthorized with error code "TOKEN_EXPIRED"
5. Axios interceptor detects 401 response
6. Client automatically calls POST /api/auth/refresh endpoint
7. Refresh token from cookie automatically sent with request
8. Backend receives refresh request
9. System extracts refresh token from HTTP-only cookie
10. System queries `refresh_tokens` table by token_hash
11. System finds matching refresh token record
12. System validates refresh token:
    - Token has not expired (expires_at > current time)
    - Token has not been revoked (revoked_at IS NULL)
    - Token belongs to valid user
13. System retrieves user information
14. System generates new access token (JWT):
    - Payload: { userId, email, iat, exp }
    - Expiry: 15 minutes
15. System generates new refresh token (token rotation)
16. System hashes new refresh token with bcrypt
17. System marks old refresh token as revoked:
    - UPDATE refresh_tokens SET revoked_at = CURRENT_TIMESTAMP WHERE id = old_token_id
18. System creates new refresh token record:
    - token_hash: bcrypt(new_refresh_token)
    - expires_at: current time + 7 days
19. System returns response:
    - Body: { access_token, expires_in: 900 }
    - Set-Cookie: new refresh_token
20. Client stores new access token in memory
21. Client retries original failed API request with new access token
22. Original API request succeeds
23. User continues working without interruption

## 6. Alternative Flows

### 6a. Refresh Token Expired

**Trigger:** Step 12 - Refresh token has expired

1. System checks expires_at timestamp
2. expires_at < current time (token expired)
3. System returns 401 Unauthorized with error "REFRESH_TOKEN_EXPIRED"
4. Client clears auth state (access token, cookies)
5. Client redirects user to Login page
6. UI displays message: "Session expired. Please login again."
7. Use case ends (failure)

### 6b. Refresh Token Revoked

**Trigger:** Step 12 - Refresh token has been revoked

1. System checks revoked_at field
2. revoked_at IS NOT NULL (token was revoked)
3. System returns 401 Unauthorized with error "REFRESH_TOKEN_REVOKED"
4. Client clears auth state
5. Client redirects user to Login page
6. UI displays message: "Session invalidated. Please login again."
7. Use case ends (failure)

### 6c. Refresh Token Not Found

**Trigger:** Step 10-11 - Refresh token not found in database

1. System queries refresh_tokens table
2. No matching token_hash found
3. System returns 401 Unauthorized with error "INVALID_REFRESH_TOKEN"
4. Client clears auth state
5. Client redirects user to Login page
6. Use case ends (failure)

### 6d. No Refresh Token in Cookie

**Trigger:** Step 9 - No refresh token in cookie

1. System checks request cookies
2. refresh_token cookie not present
3. System returns 401 Unauthorized with error "REFRESH_TOKEN_MISSING"
4. Client redirects user to Login page
5. Use case ends (failure)

### 6e. Database Error During Token Rotation

**Trigger:** Step 17-18 - Database error during token rotation

1. System successfully generates new tokens
2. System fails to update database (revoke old token or save new token)
3. System rolls back transaction
4. System returns 500 Internal Server Error
5. Client keeps using current tokens
6. User may try refresh again
7. Use case ends (failure)

### 6f. Token Reuse Detection (Security)

**Trigger:** Step 12 - Refresh token already used (revoked) but attempted reuse

1. System detects revoked_at IS NOT NULL
2. System identifies potential token theft/replay attack
3. System revokes ALL refresh tokens for this user (security measure)
4. System logs security event
5. System returns 401 Unauthorized with error "TOKEN_REUSE_DETECTED"
6. Client redirects user to Login page
7. Optional: Send email notification to user about suspicious activity
8. Use case ends (failure)

## 7. Special Requirements

### 7.1 Performance

- Response time < 500ms
- Token rotation should be atomic (single transaction)
- Bcrypt hashing should not block main thread

### 7.2 Security

- **Token Rotation:** Every refresh generates new refresh token (old one revoked)
  - Mitigates token theft - stolen token usable only once
- **One-time use:** Refresh token can only be used once
- **HTTP-only cookie:** Refresh token not accessible by JavaScript
- **Secure flag:** Cookie only sent over HTTPS
- **SameSite=Strict:** CSRF protection
- **Token reuse detection:** Immediate revocation of all tokens on reuse attempt
- **Short access token expiry:** 15 minutes reduces window of compromise
- **Long refresh token expiry:** 7 days balance security vs UX

### 7.3 Usability

- **Seamless UX:** Automatic token refresh, no user interaction
- **No interruption:** User continues working without noticing
- **Clear error messages:** When refresh fails, inform user clearly
- **Loading states:** Show loading indicator during refresh (optional)

### 7.4 Error Handling

- Client must handle 401 responses gracefully
- Exponential backoff if refresh fails temporarily
- Avoid infinite refresh loops

## 8. Technology and Data Variations

### 8.1 Token Rotation Flow

```
Login:
  access_token_1 (15min) + refresh_token_A (7days)

After 15min (access expires):
  Client: refresh with refresh_token_A
  Server: return access_token_2 + refresh_token_B (revoke A)

After 30min (access expires again):
  Client: refresh with refresh_token_B
  Server: return access_token_3 + refresh_token_C (revoke B)

If client retries with refresh_token_A (already revoked):
  Server: detect reuse ’ revoke ALL tokens ’ force re-login
```

### 8.2 Database Operations

- **Atomic transaction:**
  - Validate old refresh token
  - Revoke old refresh token
  - Create new refresh token
  - If any step fails ’ rollback

### 8.3 Axios Interceptor Pattern (Client)

```typescript
axios.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const { data } = await axios.post('/api/auth/refresh');
        const newAccessToken = data.access_token;

        // Update access token in memory
        setAccessToken(newAccessToken);

        // Retry original request with new token
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
        return axios(originalRequest);
      } catch (refreshError) {
        // Refresh failed ’ logout
        clearAuth();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);
```

## 9. Frequency of Occurrence

- Expected: Every 15 minutes per active user
- If 100 concurrent users: ~400 refresh requests/hour
- Peak load: 1000+ refresh requests/hour (high traffic)

## 10. Open Issues

- **Refresh token family:** Track token lineage to detect compromise (future)
- **Device management:** Allow users to see/revoke active sessions per device (future)
- **Sliding expiration:** Extend refresh token expiry on each use (optional)
- **Remember me:** Longer refresh token expiry (30 days) if user opts in (future)
- **Concurrent refresh:** Handle race conditions if multiple tabs refresh simultaneously

## 11. Related Use Cases

- [UC-002: User Login](UC-002-user-login.md) - Initial login generates tokens
- [UC-004: User Logout](UC-004-user-logout.md) - Logout revokes refresh token
- [UC-006: Change Password](UC-006-change-password.md) - Password change revokes all tokens

## 12. Business Rules References

- **BR-1.3:** Token Management (refresh token rotation, expiry)

## 13. API Endpoint

```http
POST /api/auth/refresh
```

**Request Headers:**

```http
Cookie: refresh_token=<secure-token>
```

**Success Response (200 OK):**

```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expires_in": 900
}
```

**Set-Cookie Header:**

```http
Set-Cookie: refresh_token=<new-secure-token>; HttpOnly; Secure; SameSite=Strict; Max-Age=604800; Path=/api/auth
```

**Error Responses:**

401 Unauthorized - Token expired:

```json
{
  "error": "REFRESH_TOKEN_EXPIRED",
  "message": "Refresh token has expired. Please login again."
}
```

401 Unauthorized - Token revoked:

```json
{
  "error": "REFRESH_TOKEN_REVOKED",
  "message": "Refresh token has been revoked. Please login again."
}
```

401 Unauthorized - Token missing:

```json
{
  "error": "REFRESH_TOKEN_MISSING",
  "message": "Refresh token not found. Please login again."
}
```

401 Unauthorized - Token reuse detected:

```json
{
  "error": "TOKEN_REUSE_DETECTED",
  "message": "Token reuse detected. All sessions have been terminated for security. Please login again."
}
```

500 Internal Server Error:

```json
{
  "error": "INTERNAL_SERVER_ERROR",
  "message": "Failed to refresh token. Please try again."
}
```

## 14. Test Cases

### TC-003-001: Successful Token Refresh

- **Given:** User logged in with valid refresh token
- **When:** Access token expires after 15 minutes
- **Then:** New access token and refresh token generated, old token revoked

### TC-003-002: Refresh Token Expired

- **Given:** Refresh token expired (> 7 days old)
- **When:** Client attempts to refresh
- **Then:** 401 error, user redirected to login

### TC-003-003: Refresh Token Revoked

- **Given:** Refresh token previously revoked (user logged out)
- **When:** Client attempts to refresh
- **Then:** 401 error, user redirected to login

### TC-003-004: Token Reuse Detection

- **Given:** Refresh token A already used (revoked)
- **When:** Attacker tries to reuse token A
- **Then:** All user's tokens revoked, security event logged

### TC-003-005: No Refresh Token in Cookie

- **Given:** Cookie not present or cleared
- **When:** Client attempts to refresh
- **Then:** 401 error with "REFRESH_TOKEN_MISSING"

### TC-003-006: Concurrent Refresh Requests

- **Given:** User has multiple tabs open
- **When:** Access token expires, all tabs refresh simultaneously
- **Then:** Only first request succeeds with new tokens, others may fail gracefully

### TC-003-007: Automatic Retry After Refresh

- **Given:** API request fails with 401
- **When:** Token refreshed successfully
- **Then:** Original API request automatically retried and succeeds

## 15. Security Considerations

### 15.1 Token Rotation Benefits

- **Limits damage from token theft:** Stolen token only works once
- **Detects compromise:** Reuse attempt reveals token theft
- **Automatic revocation:** All tokens revoked on suspicious activity

### 15.2 Token Storage

- **Never store refresh token in localStorage:** Vulnerable to XSS
- **Use HTTP-only cookie:** JavaScript cannot access it
- **Secure flag:** Only sent over HTTPS
- **SameSite=Strict:** CSRF protection

### 15.3 Token Reuse Attack Scenario

```
Normal flow:
  User: refresh with token_A ’ get token_B
  User: refresh with token_B ’ get token_C

Attack scenario:
  Attacker steals token_A
  User: refresh with token_A ’ get token_B (token_A revoked)
  Attacker: tries to refresh with token_A
  System: detects revoked token reuse
  System: revokes ALL tokens for user
  System: forces re-login
```

### 15.4 Database Cleanup

- Scheduled job to delete expired/revoked tokens:
  - DELETE FROM refresh_tokens WHERE expires_at < NOW() - INTERVAL '30 days'
  - Run daily to keep table size manageable
