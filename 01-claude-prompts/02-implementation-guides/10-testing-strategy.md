# Testing Strategy (Claude Prompt)

**Sources**
- `01-claude-prompts/09-testing-specifications/*`
- `00_docs/04-detail-design/03-business-logic-flows.md` (for expected outcomes)
- `00_docs/04-detail-design/06-error-handling-specs.md` (for negative cases)

## Approach
1. **Unit tests** – Cover services, utilities, and React hooks using guidelines in `09-testing-specifications/01-unit-testing-guide.md`.
2. **Integration tests** – Focus on REST controllers + persistence (Spring Boot) and API integration (frontend) following `.../02-integration-testing-guide.md`.
3. **E2E tests** – Prioritise smoke flows: auth, create folder/deck/card, SRS review session, import/export (web) using `.../03-e2e-testing-guide.md`.
4. **Mocking & fixtures** – Use strategies in `.../05-mocking-strategies.md`; prefer builders from `.../04-test-data-builders.md`.
5. **Coverage goals** – Backend 80% lines/branches, frontend 70% lines, performance tests for review API P95 <500 ms (`.../07-performance-testing.md`).

## Claude tips
- When writing tests, paste only the checklist relevant to the test level to keep prompts short.
- Remind Claude to assert error codes, not just HTTP status, aligning with error handling spec.
