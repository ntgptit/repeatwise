# API – User Profile

**Source**: `00_docs/03-design/api/user-profile-apis.md`.

## Endpoints
- `GET /api/users/me` – returns profile with `id, email, name, language, theme, timezone`.
- `PUT /api/users/me` – update profile; accepts partial fields.
- `PUT /api/users/password` – change password (requires current + new) [if specified; if not, note absent].

## Constraints
- Name <=100 chars; timezone must be valid IANA; enums limited to `VI/EN`, `LIGHT/DARK/SYSTEM`.
- Password change requires 8–128 chars and new != old.

## Claude tips
- Only copy the payload schema needed (update vs password).
- Remind Claude to return 200 with updated resource.
