# Security & Authorization Rules (Claude Prompt)

**Source**: `00_docs/04-detail-design/05-validation-rules.md` §7 + `03-design/security/authn-authz-model.md`.

## Rules
- All APIs (except register/login/refresh) require authenticated user with role `ROLE_USER`.
- Requests must include `X-Timezone` header when relevant; default to user profile timezone otherwise.
- Users may only access resources where `user_id` matches the authenticated user. Enforce at repository/service level.
- Refresh tokens validated against hashed store; invalid/expired tokens return `AUTH_TOKEN_INVALID`.
- Rate limiting is not implemented—ensure Claude does not invent it.

## Claude tips
- When writing controller advice, include mapping for 401/403 with proper error codes.
- For multi-tenant checks, remind Claude to include `userId` filters in every repository query.
