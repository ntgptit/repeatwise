# Use Case Map – Authentication

**Use cases**: `UC-001` Register, `UC-002` Login, `UC-003` Logout, `UC-004` Manage profile.

## Flow → API → UI
- **Register** → POST `/api/auth/register` → Web `RegisterPage`, Mobile `RegisterScreen`.
- **Login** → POST `/api/auth/login` (returns access + refresh) → Web `LoginPage`, Mobile `LoginScreen`.
- **Refresh** (implicit in UC-002) → POST `/api/auth/refresh` (HttpOnly cookie) → Axios interceptor / mobile fetch wrapper.
- **Logout current/all** → POST `/api/auth/logout`, `/api/auth/logout-all` → Account menu actions.
- **Profile** → GET/PUT `/api/users/me` → Web `ProfileSettings`, Mobile `ProfileScreen`.

## Data touchpoints
- Entities: `User`, `RefreshToken`, `SrsSettings` defaults.
- DTOs: `RegisterRequest/Response`, `LoginRequest/Response`, `UpdateProfileRequest`.
- Validation: email/password rules, timezone, enums.

## Acceptance highlights
- Successful register auto creates stats + settings, logs user in.
- Login rotates refresh token; store new cookie each time.
- Logout-all invalidates all refresh tokens for user.

## Claude tips
- Reference `authn-authz-model.md` for cookie/headers when needed.
- For UI, pull wireframes sections "Auth" from web/mobile specs to avoid large paste.
