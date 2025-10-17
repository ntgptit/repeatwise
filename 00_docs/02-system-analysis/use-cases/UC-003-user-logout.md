# UC-003: User Logout

## 1. Use Case Information

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-003 |
| **Use Case Name** | User Logout |
| **Primary Actor** | Authenticated User |
| **Secondary Actors** | None |
| **Priority** | Medium (P1) |
| **Complexity** | Low |
| **Status** | MVP |

## 2. Brief Description

Authenticated user logs out of the application. System invalidates the JWT token (client-side only for MVP) and redirects user to login page.

## 3. Preconditions

- User is logged in
- Valid JWT token exists in storage
- User is on any authenticated page

## 4. Postconditions

**Success**:
- JWT token removed from client storage
- User session terminated
- User redirected to login page
- Protected routes inaccessible

**Failure**:
- User remains logged in
- Error message displayed

## 5. Main Success Scenario

### Step 1: Access Logout Option
**Actor**: User clicks on profile menu

**System**:
- Shows dropdown menu with options:
  - Profile Settings
  - SRS Settings
  - Statistics
  - **Logout**

### Step 2: Initiate Logout
**Actor**: User clicks "Logout"

**System**:
- Shows confirmation dialog (optional, can be disabled in settings):
  - "Are you sure you want to log out?"
  - [Cancel] [Logout]

### Step 3: Confirm Logout
**Actor**: User clicks "Logout" in confirmation dialog

**System**:
1. Removes JWT token from storage:
   - Web: Delete from localStorage
   - Mobile: Delete from SecureStore
2. Clears app state:
   - Reset user context (user = null)
   - Clear TanStack Query cache
   - Reset Zustand store (if used)
3. Logs event: "User logged out: {email}"
4. Redirects to login page
5. Shows success toast: "You have been logged out"

**Actor**: User sees login page

## 6. Alternative Flows

### A1: Logout Without Confirmation
**Trigger**: User has disabled logout confirmation in settings (Step 2)

**Flow**:
1. User clicks "Logout"
2. System skips confirmation dialog
3. Immediately proceeds to Step 3

**Continue to**: Step 3

---

### A2: Cancel Logout
**Trigger**: User clicks "Cancel" in confirmation dialog (Step 3)

**Flow**:
1. System closes confirmation dialog
2. User remains on current page
3. No changes to session

**End Use Case**

---

### A3: Session Expired
**Trigger**: JWT token expired, user tries to access protected route

**Flow**:
1. System detects expired token (401 response from API)
2. System automatically logs out user
3. Shows message: "Your session has expired. Please log in again."
4. Redirects to login page
5. Preserves attempted URL to redirect after login

**End Use Case**

---

### A4: Quick Logout (Keyboard Shortcut)
**Trigger**: User presses keyboard shortcut (Ctrl+Shift+Q)

**Flow**:
1. System detects keyboard shortcut
2. Shows confirmation dialog
3. Proceeds with normal logout flow

**Continue to**: Step 3

## 7. Exception Flows

### E1: Storage Clear Failure
**Trigger**: Cannot remove token from storage (Step 3)

**Flow**:
1. System attempts to clear storage
2. Storage API throws error
3. System logs error
4. System still proceeds with logout (best effort)
5. User redirected to login page anyway
6. Note: Stale token will be rejected by server on next use

**Continue to**: Redirect to login

## 8. Special Requirements

### Performance
- Logout completes instantly (< 100ms)
- No API call required for MVP (token validation client-side only)

### Security
- Token immediately removed from client storage
- No token transmitted to server on logout (MVP)
- Future: Implement token blacklist on server side for production

### Usability
- Logout accessible from all authenticated pages
- Clear logout button in navigation
- Confirmation dialog prevents accidental logout
- Option to disable confirmation for power users

### Accessibility
- Logout button keyboard accessible (Tab + Enter)
- Screen reader announces logout action
- Confirmation dialog focusable and keyboard navigable

## 9. Business Rules

### BR-005: Token Invalidation
- **MVP**: Client-side only (remove token from storage)
- **Production**: Server-side token blacklist recommended
  - Store invalidated tokens in Redis with TTL = token expiry
  - API validates token not in blacklist

### BR-006: Session Timeout
- JWT token expires after 24 hours
- No automatic refresh for MVP
- User must login again after expiry

### BR-007: Multi-Device Logout
- **MVP**: Logout only affects current device
- **Future**: "Logout from all devices" option
  - Requires server-side token management

### BR-008: Unsaved Changes Warning
- If user has unsaved changes (e.g., editing card), show warning:
  - "You have unsaved changes. Log out anyway?"
- Prevents accidental data loss

## 10. Data Requirements

### Input Data
- user_id: UUID (from JWT token)
- device_id: String (for logging, optional)

### Output Data
- None (logout is state-clearing operation)

### Storage Changes
- **Client Storage**:
  - DELETE jwt_token from localStorage/SecureStore
  - CLEAR user_context
  - CLEAR query_cache (TanStack Query)
  - RESET ui_state (Zustand)

### Logging
- Log event: "User logged out: {user_id}, device: {device_id}, timestamp: {now}"

## 11. UI Mockup Notes

### Navigation Menu (Logged In)
```
┌─────────────────────────────────────┐
│  RepeatWise          [☰] [Profile]  │
├─────────────────────────────────────┤
│  Profile Dropdown:                  │
│  ┌────────────────────────────┐    │
│  │  Nguyễn Văn Minh           │    │
│  │  minh@example.com          │    │
│  ├────────────────────────────┤    │
│  │  👤 Profile Settings       │    │
│  │  ⚙️  SRS Settings          │    │
│  │  📊 Statistics             │    │
│  │  🌙 Dark Mode        [ON]  │    │
│  ├────────────────────────────┤    │
│  │  🚪 Logout                 │    │
│  └────────────────────────────┘    │
└─────────────────────────────────────┘
```

### Logout Confirmation Dialog
```
┌─────────────────────────────────────┐
│  Logout Confirmation                │
├─────────────────────────────────────┤
│  Are you sure you want to log out?  │
│                                     │
│  Your progress is saved.            │
│                                     │
│  [ ] Don't ask again                │
│                                     │
│  [Cancel]              [Logout]     │
└─────────────────────────────────────┘
```

### After Logout (Login Page)
```
┌─────────────────────────────────────┐
│  Welcome Back to RepeatWise         │
├─────────────────────────────────────┤
│  ✅ You have been logged out        │
│                                     │
│  Email Address                      │
│  [_____________________________]    │
│                                     │
│  Password                           │
│  [_____________________________]    │
│                                     │
│  [Log In]                           │
│                                     │
│  Don't have an account? [Sign Up]   │
└─────────────────────────────────────┘
```

## 12. Testing Scenarios

### Happy Path
1. User clicks profile menu
2. User clicks "Logout"
3. User confirms logout
4. Token removed from storage
5. User redirected to login page
6. Protected routes inaccessible

### Edge Cases
1. Logout while on review session → Confirm unsaved progress
2. Logout with slow network → Still works (client-side only)
3. Logout on mobile → SecureStore cleared properly
4. Logout immediately after login → No errors
5. Multiple rapid logout clicks → Handled gracefully (idempotent)

### Error Cases
1. Storage API error → Logout proceeds anyway
2. Token already invalid → Logout succeeds (no-op)
3. Logout on expired session → Redirects without error

### Multi-Platform Testing
1. Web: localStorage cleared, redirect works
2. Mobile: SecureStore cleared, navigation resets
3. Cross-device: Logout on Device A doesn't affect Device B (MVP)

## 13. Related Use Cases

- **UC-002**: User Login - User logs in after logout
- **UC-001**: User Registration - Alternative if no account
- **UC-004**: Password Reset - If user forgot password (Future)

## 14. Notes & Assumptions

### Assumptions
- **MVP**: Token validation is client-side only (no server-side blacklist)
- No "remember me" option (always 24h session)
- No concurrent session management (can be logged in on multiple devices)
- Logout doesn't notify other devices (no WebSocket)

### Future Enhancements
- **Server-side token blacklist**: Redis-based invalidation
- **Logout from all devices**: Invalidate all user tokens
- **Session activity tracking**: Show last active sessions
- **Auto-logout on inactivity**: Timeout after 30 minutes idle
- **Logout event webhook**: Notify integrations of logout

### Production Security Recommendations
1. **Token Blacklist**:
   - Store invalidated tokens in Redis
   - Check blacklist on every API request
   - TTL = token expiry time
2. **Logout Endpoint**:
   - POST /api/auth/logout
   - Add token to blacklist
   - Optional: Invalidate refresh token (when implemented)
3. **Multi-Device Logout**:
   - Track active sessions per user
   - "Logout all devices" endpoint
   - Show active sessions in settings

## 15. Acceptance Criteria

- [ ] User can logout from profile menu
- [ ] Confirmation dialog shown (unless disabled)
- [ ] JWT token removed from client storage
- [ ] User context cleared (user = null)
- [ ] TanStack Query cache cleared
- [ ] User redirected to login page
- [ ] Protected routes inaccessible after logout
- [ ] Success message displayed
- [ ] Keyboard shortcut works (Ctrl+Shift+Q)
- [ ] Mobile: SecureStore properly cleared
- [ ] Logout completes in < 100ms
- [ ] No errors if token already expired
- [ ] Unsaved changes warning shown if applicable

---

**Version**: 1.0
**Last Updated**: 2025-01
**Author**: Product Team
