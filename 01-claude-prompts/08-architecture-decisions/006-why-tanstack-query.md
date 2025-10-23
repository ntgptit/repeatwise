# ADR – Why TanStack Query

**Decision**: Use TanStack Query for data fetching on web/mobile.

**Rationale**
- Handles caching, background refetching, and mutation states without custom boilerplate.
- Integrates with React/React Native, suits REST APIs.
- Built-in retry/backoff aligns with import/export polling needs.

**Alternatives**: Redux Toolkit Query (heavier setup), raw fetch (manual caching).

**Claude tips**
- Encourage Claude to use `useQuery`/`useMutation` wrappers defined in frontend architecture doc.
