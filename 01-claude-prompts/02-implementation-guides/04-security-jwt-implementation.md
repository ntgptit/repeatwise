# Security & JWT Implementation (Claude Prompt)

**Sources**
- `00_docs/03-design/security/authn-authz-model.md`
- `06-api-specifications/01-authentication-apis.md`
- Validation & error handling docs

## Implementation outline
1. **Authentication endpoints** – Implement register/login/refresh/logout using DTOs defined in API spec. Refresh token returned via HttpOnly cookie.
2. **JWT filter** – Configure Spring Security with `UsernamePasswordAuthenticationFilter` replacement, extracting Bearer token, verifying signature, and loading user details.
3. **Refresh token store** – Persist hashed tokens (bcrypt) with 7-day expiry; revoke on logout or rotation.
4. **Exception handling** – Standardise responses using `ErrorCode.AUTH_*` from `04-detail-design/06-error-handling-specs.md`.
5. **Password policy** – Enforce validations via `05-validation-rules.md` (min 8 chars, max 128).
6. **Testing** – Add integration tests verifying login + refresh flows and token revocation.

## Claude tips
- Remind Claude to keep JWT access token TTL at 15 minutes and to rotate refresh tokens every refresh call.
- When adjusting security config, quote only the sections from `authn-authz-model.md` covering filter order and cookie flags.
