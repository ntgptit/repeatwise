# API – Authentication

**Endpoints** (see `00_docs/03-design/api/authentication-apis.md`)
- `POST /api/auth/register` – register + auto login. Request: `{ email, password, name }`. Response: user profile + tokens.
- `POST /api/auth/login` – returns `{ accessToken, expiresIn }`, sets HttpOnly refresh cookie.
- `POST /api/auth/refresh` – rotates refresh cookie, returns new access token.
- `POST /api/auth/logout` – revokes current refresh token.
- `POST /api/auth/logout-all` – revokes all tokens for user.

**Profiles**
- `GET /api/users/me`, `PUT /api/users/me`.

**Notes**
- Access token TTL 15 min; refresh TTL 7 days.
- Login failure returns `AUTH_INVALID_CREDENTIALS` (401).
- All responses include ISO timestamps.

**Claude tips**
- Provide only the endpoint block relevant to your change (e.g., login) to conserve tokens.
