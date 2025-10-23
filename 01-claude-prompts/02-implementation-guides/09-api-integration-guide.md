# API Integration Guide (Claude Prompt)

**Goal**: Connect frontends to backend endpoints efficiently.

## Steps
1. **Identify endpoints** – Use `06-api-specifications` cheat-sheets. Start with `00-api-overview.md` to pick the right resource file.
2. **Authentication** – Web/mobile clients store access token in memory; refresh via `/auth/refresh` using HttpOnly cookie/secure storage. See `frontend-architecture.md` token flow.
3. **HTTP client** – Axios (web) or Fetch wrapper (mobile) configured with interceptors for 401 handling and timezone headers.
4. **Error handling** – Map backend `errorCode` to user-friendly messages using `04-detail-design/06-error-handling-specs.md`.
5. **Pagination/filtering** – Follow query params defined in `11-pagination-filtering.md` (page, size, sort, filter expressions).
6. **Testing** – Mock API with MSW (web) or Mirage (mobile) referencing `09-testing-specifications/05-mocking-strategies.md`.

## Claude tips
- Provide only the specific endpoint block when discussing an API; avoid pasting the whole summary file.
- Remind Claude that rate limiting/webhooks are not part of MVP.
