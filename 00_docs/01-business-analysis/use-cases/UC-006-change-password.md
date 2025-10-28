# UC-006: Change Password

## 1. Brief Description

Authenticated user changes their password by providing current password and new password. All refresh tokens are revoked to force re-authentication on all devices for security.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** Authentication Service, Token Service

## 3. Preconditions

- User is logged in (has valid access token)
- User can access Settings/Security page
- User knows their current password

## 4. Postconditions

### Success Postconditions

- User's password updated in database (password_hash with new hash)
- All refresh tokens for this user revoked (logout from all devices)
- Current session terminated
- Success message displayed
- User redirected to Login page
- User must log in with new password

### Failure Postconditions

- Password not changed
- User session remains active
- Error message displayed
- User stays on Change Password page

## 5. Main Success Scenario (Basic Flow)

1. User is logged in and navigates to Settings > Security > Change Password
2. System displays Change Password form with fields:
   - Current Password (password input, required)
   - New Password (password input, required, min 8 chars)
   - Confirm New Password (password input, required, must match new password)
3. User enters current password "OldPassword123"
4. User enters new password "NewPassword456"
5. User enters confirm new password "NewPassword456"
6. User clicks "Change Password" button
7. System validates input:
   - All fields are not empty
   - New password >= 8 characters
   - Confirm new password matches new password
   - New password different from current password (optional validation)
8. System queries database for user by user_id
9. System retrieves user record with current password_hash
10. System verifies current password using bcrypt.compare()
11. Current password matches
12. System hashes new password with bcrypt (cost factor 12)
13. System starts database transaction
14. System updates users table:

    ```sql
    UPDATE users
    SET password_hash = ?,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = ?
    ```

15. System revokes ALL refresh tokens for this user:

    ```sql
    UPDATE refresh_tokens
    SET revoked_at = CURRENT_TIMESTAMP
    WHERE user_id = ?
      AND revoked_at IS NULL
    ```

16. System commits transaction
17. System sends Set-Cookie header to clear current refresh token
18. System returns 200 OK with success message
19. Client receives response
20. Client clears authentication state:
    - Access token cleared from memory
    - Auth context reset
21. Client redirects to Login page
22. System displays success message: "Password changed successfully. Please login with your new password."
23. User sees Login page
24. User logs in with new password

## 6. Alternative Flows

### 6a. Current Password Incorrect

**Trigger:** Step 10-11 - Current password does not match

1. System compares input password with stored hash
2. bcrypt.compare() returns false
3. System returns 400 Bad Request
4. UI displays error: "Current password is incorrect"
5. User must re-enter correct current password
6. Return to Step 3 (Main Flow)

**Security Note:** Can implement rate limiting to prevent brute force attempts

### 6b. New Password Too Short

**Trigger:** Step 7 - New password < 8 characters

1. System validates new password length
2. newPassword.length < 8
3. System returns 400 Bad Request (or client-side validation)
4. UI displays error: "New password must be at least 8 characters"
5. User must enter longer password
6. Return to Step 4 (Main Flow)

### 6c. Passwords Do Not Match

**Trigger:** Step 7 - Confirm password doesn't match new password

1. System validates password match
2. confirmPassword !== newPassword
3. System returns 400 Bad Request (or client-side validation)
4. UI displays error: "Passwords do not match"
5. User must re-enter matching passwords
6. Return to Step 5 (Main Flow)

### 6d. New Password Same as Current

**Trigger:** Step 7 - New password identical to current password

1. System detects new password matches current (optional check)
2. System returns 400 Bad Request
3. UI displays error: "New password must be different from current password"
4. User must choose different password
5. Return to Step 4 (Main Flow)

**Note:** This validation is optional for MVP

### 6e. Empty Required Fields

**Trigger:** Step 7 - User submits form with empty fields

1. Client-side validation detects empty fields
2. UI displays validation errors for each empty field
3. "Change Password" button disabled until all fields filled
4. Return to Step 3 (Main Flow)

### 6f. Database Error During Transaction

**Trigger:** Step 14-16 - Database error occurs

1. System attempts to update password or revoke tokens
2. Database error occurs (connection lost, constraint violation, etc.)
3. System rolls back entire transaction
4. No password changed, no tokens revoked
5. System logs error
6. System returns 500 Internal Server Error
7. UI displays error: "Failed to change password. Please try again."
8. User remains on Change Password page
9. Use case ends (failure)

### 6g. Session Expired During Password Change

**Trigger:** Step 8 - Access token invalid or expired

1. User's session expires while on form
2. System cannot authenticate user
3. System returns 401 Unauthorized
4. Client attempts token refresh (UC-003)
5. If refresh succeeds, retry password change
6. If refresh fails, redirect to login
7. User must log in again to change password

## 7. Special Requirements

### 7.1 Performance

- Response time < 2 seconds (bcrypt hashing takes time)
- Transaction should be atomic (all or nothing)

### 7.2 Security

- **Current password verification:** Prevents unauthorized password change
- **Revoke all sessions:** Force re-login on all devices for security
- **Hash with bcrypt:** Same cost factor 12 as registration
- **Rate limiting:** Limit password change attempts (5 per hour) to prevent abuse
- **Strong password policy:** Minimum 8 characters (can add complexity rules later)
- **Password history:** Don't allow reusing last N passwords (future)
- **Audit logging:** Log password change events with timestamp and IP

### 7.3 Usability

- **Clear instructions:** Explain that user will be logged out after change
- **Password strength indicator:** Visual feedback on password strength (optional)
- **Show/hide password toggles:** For all three password fields
- **Confirmation dialog:** "You will be logged out from all devices. Continue?"
- **Success message:** Clear next steps (login with new password)

### 7.4 Password Validation

MVP Requirements:

- Minimum 8 characters
- Not empty

Future Enhancements:

- At least one uppercase letter
- At least one lowercase letter
- At least one number
- At least one special character
- Not in common password list (pwned passwords API)

## 8. Technology and Data Variations

### 8.1 Atomic Transaction

```sql
BEGIN;

-- Update password
UPDATE users
SET password_hash = ?,
    updated_at = CURRENT_TIMESTAMP
WHERE id = ?;

-- Revoke all refresh tokens
UPDATE refresh_tokens
SET revoked_at = CURRENT_TIMESTAMP
WHERE user_id = ?
  AND revoked_at IS NULL;

COMMIT;
```

If any step fails, entire transaction rolls back.

### 8.2 Password Strength Checking (Future)

Client-side library: zxcvbn

```typescript
import zxcvbn from 'zxcvbn';

const checkPasswordStrength = (password: string) => {
  const result = zxcvbn(password);
  // result.score: 0-4 (weak to strong)
  return {
    score: result.score,
    feedback: result.feedback,
    strength: ['Very Weak', 'Weak', 'Fair', 'Strong', 'Very Strong'][result.score]
  };
};
```

### 8.3 Logout All Devices Implementation

Same logic as UC-004 Logout, but for all user's tokens:

```typescript
const revokeAllUserTokens = async (userId: string) => {
  await db.query(
    'UPDATE refresh_tokens SET revoked_at = CURRENT_TIMESTAMP WHERE user_id = ? AND revoked_at IS NULL',
    [userId]
  );
};
```

### 8.4 Password History (Future)

Store hashed versions of last N passwords:

```sql
CREATE TABLE password_history (
  id UUID PRIMARY KEY,
  user_id UUID REFERENCES users(id),
  password_hash VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Check if password was used before
SELECT COUNT(*) FROM password_history
WHERE user_id = ?
  AND created_at > NOW() - INTERVAL '90 days'
  -- Compare new password against historical hashes
```

## 9. Frequency of Occurrence

- Expected: 0.1-1 password changes per user per month
- Most users rarely change password unless security concern
- Total: 5-20 password changes/day (MVP phase)
- Spike: After security breach notification

## 10. Open Issues

- **Password reset flow:** Forgot password via email (separate use case, future)
- **Password strength requirements:** Enforce complexity rules (future)
- **Password history:** Prevent reuse of last 5 passwords (future)
- **Two-factor authentication:** Add 2FA requirement before password change (future)
- **Security notifications:** Email user after password change (future)
- **Account recovery:** Backup email, security questions (future)

## 11. Related Use Cases

- [UC-001: User Registration](UC-001-user-registration.md) - Initial password set
- [UC-002: User Login](UC-002-user-login.md) - Login with new password
- [UC-004: User Logout](UC-004-user-logout.md) - Similar token revocation logic
- Password Reset Flow (future use case)

## 12. Business Rules References

- **BR-1.2:** Password Policy
- **BR-1.3:** Token Management (revoke all tokens)

## 13. API Endpoint

```http
POST /api/users/change-password
```

**Request Headers:**

```http
Authorization: Bearer <access-token>
Content-Type: application/json
```

**Request Body:**

```json
{
  "currentPassword": "OldPassword123",
  "newPassword": "NewPassword456",
  "confirmNewPassword": "NewPassword456"
}
```

**Success Response (200 OK):**

```json
{
  "message": "Password changed successfully. Please login with your new password."
}
```

**Set-Cookie Header (clears refresh token):**

```http
Set-Cookie: refresh_token=; HttpOnly; Secure; SameSite=Strict; Max-Age=0; Path=/api/auth
```

**Error Responses:**

400 Bad Request - Current password incorrect:

```json
{
  "error": "Invalid password",
  "message": "Current password is incorrect"
}
```

400 Bad Request - Validation error:

```json
{
  "error": "Validation failed",
  "details": [
    {
      "field": "newPassword",
      "message": "New password must be at least 8 characters"
    },
    {
      "field": "confirmNewPassword",
      "message": "Passwords do not match"
    }
  ]
}
```

400 Bad Request - Same password:

```json
{
  "error": "Invalid password",
  "message": "New password must be different from current password"
}
```

401 Unauthorized:

```json
{
  "error": "Unauthorized",
  "message": "Authentication required"
}
```

429 Too Many Requests (rate limit):

```json
{
  "error": "Too many requests",
  "message": "Too many password change attempts. Please try again in 1 hour."
}
```

500 Internal Server Error:

```json
{
  "error": "Internal server error",
  "message": "Failed to change password. Please try again."
}
```

## 14. Test Cases

### TC-006-001: Successful Password Change

- **Given:** User logged in with password "OldPass123"
- **When:** User changes password to "NewPass456"
- **Then:** Password updated, all tokens revoked, redirected to login

### TC-006-002: Incorrect Current Password

- **Given:** User logged in with password "OldPass123"
- **When:** User enters wrong current password "WrongPass"
- **Then:** Error "Current password is incorrect"

### TC-006-003: New Password Too Short

- **Given:** User enters new password "short"
- **When:** User clicks Change Password
- **Then:** Validation error "New password must be at least 8 characters"

### TC-006-004: Passwords Do Not Match

- **Given:** User enters new password "NewPass123" and confirm "NewPass456"
- **When:** User clicks Change Password
- **Then:** Error "Passwords do not match"

### TC-006-005: New Password Same as Current

- **Given:** User enters current password "OldPass123"
- **When:** User enters new password also "OldPass123"
- **Then:** Error "New password must be different from current password"

### TC-006-006: Empty Fields

- **Given:** User leaves any field empty
- **When:** User tries to submit
- **Then:** Validation errors for empty fields, button disabled

### TC-006-007: All Tokens Revoked

- **Given:** User logged in on 3 devices
- **When:** User changes password on device 1
- **Then:** All devices logged out, must re-login with new password

### TC-006-008: Login with New Password

- **Given:** User successfully changed password to "NewPass456"
- **When:** User logs in with "NewPass456"
- **Then:** Login successful

### TC-006-009: Login with Old Password Fails

- **Given:** User changed password from "OldPass" to "NewPass"
- **When:** User tries to login with "OldPass"
- **Then:** Login fails with "Invalid email or password"

### TC-006-010: Session Expired During Change

- **Given:** User's access token expires while on form
- **When:** User submits password change
- **Then:** Token refreshed, password change succeeds

### TC-006-011: Rate Limiting

- **Given:** User failed password change 5 times in 1 hour
- **When:** User tries 6th time
- **Then:** 429 error "Too many attempts, try again in 1 hour"

## 15. UI/UX Considerations

### 15.1 Form Layout

```
Settings > Security > Change Password

�  You will be logged out from all devices after changing password.

Current Password *
["""""""""""]  =A

New Password *
["""""""""""]  =A
Password strength: Strong ����������

Confirm New Password *
["""""""""""]  =A

[Cancel]  [Change Password]
```

### 15.2 Confirmation Dialog

Before submission:

```
�  Change Password?

You will be logged out from all devices and need to login again
with your new password.

[Cancel]  [Continue]
```

### 15.3 Success Flow

After successful change:

```
 Password Changed Successfully

Your password has been updated. Please login with your new password.

[Go to Login]
```

### 15.4 Password Strength Indicator

Visual feedback:

- Very Weak: =4 Red bar (20%)
- Weak: =� Orange bar (40%)
- Fair: =� Yellow bar (60%)
- Strong: =� Green bar (80%)
- Very Strong: =� Green bar (100%)

## 16. Security Best Practices

### 16.1 Password Change Notifications

Future enhancement: Send email notification after password change

```
Subject: Password Changed

Your RepeatWise password was changed on [date] at [time] from IP [ip].

If you did not make this change, please contact support immediately.

[Secure Your Account]
```

### 16.2 Account Recovery

If user forgets password:

- Password Reset flow (future use case)
- Email verification
- Temporary reset link (expires in 1 hour)
- Security questions (optional)

### 16.3 Audit Trail

Log password change events:

```sql
CREATE TABLE security_audit (
  id UUID PRIMARY KEY,
  user_id UUID,
  event_type VARCHAR(50), -- 'PASSWORD_CHANGE'
  ip_address VARCHAR(45),
  user_agent TEXT,
  created_at TIMESTAMP
);
```

Use for:

- Security monitoring
- Suspicious activity detection
- Compliance/audit requirements
