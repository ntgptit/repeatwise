# User & Authentication Domain (Claude Brief)

**Sources**
- `00_docs/02-system-analysis/domain-model.md` §2.1 & §3.1–3.2
- Use cases: `UC-001` Register, `UC-002` Login, `UC-003` Logout, `UC-004` Profile (`00_docs/02-system-analysis/use-cases`)
- Validation specs: `00_docs/04-detail-design/05-validation-rules.md` (User fields), `06-error-handling-specs.md`

## Responsibilities
- Manage user lifecycle (register, login with refresh token rotation, logout current/all sessions).
- Maintain profile preferences (name, timezone, language, theme) and SRS configuration defaults.
- Enforce ownership boundaries—every other domain queries data scoped by `user_id`.

## Key entities & invariants
- `User`: unique email (lowercased), bcrypt password hash, defaults `language=VI`, `theme=SYSTEM`, timezone validated against IANA list.
- `RefreshToken`: hashed token per device, 7-day expiry, revocation tracked; belongs to `User` aggregate.
- `SRSSettings`: per-user defaults for review limits and forgotten-card strategy (see SRS domain brief for details).

## Implementation checkpoints
1. **Registration flow** – apply field validations, create default `SRSSettings` + `UserStats`, issue JWT + HttpOnly refresh cookie (see API pack `06-api-specifications/01-authentication-apis.md`).
2. **Login** – verify password via `PasswordEncoder`, rotate refresh token (old token revoked), respond with 15-minute access token.
3. **Profile update** – accept partial updates, ensure timezone/time format conversions use the `java.time` API.
4. **Authorization guard** – controllers annotate with `@PreAuthorize("hasRole('USER')")` and repository queries always filter by `userId`.

## Claude usage tips
- When coding security filters, load the minimal excerpt from `authn-authz-model.md` for token headers/cookies.
- For validation or DTO mapping, quote only the relevant rows from `02-api-request-response-specs.md`.
- Remind Claude that OAuth, avatars, and premium tiers are out-of-scope for MVP.
