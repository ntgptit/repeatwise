# ADR – Why JWT + Refresh Token

**Decision**: Short-lived access tokens (15 min) with HttpOnly refresh cookies.

**Rationale**
- Balances security and UX for personal-use app.
- Refresh cookie prevents XSS token theft; rotation reduces replay risk.
- Easier integration with web/mobile clients compared to session IDs.

**Alternatives**: Long-lived JWT (higher risk), server sessions (stateful, harder for mobile).

**Claude tips**
- Reference this when implementing auth flows; emphasise rotation requirement.
