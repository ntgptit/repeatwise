# Integration Testing Guide (Claude Prompt)

**Scope**: REST controllers + persistence, frontend API hooks with mock server.

## Expectations
- Backend: use Spring Boot Test with Testcontainers PostgreSQL; verify DB state and HTTP responses.
- Frontend: MSW-based API tests ensuring hooks/components integrate correctly.
- Cover authentication flow, folder/deck CRUD, SRS review submission, import job lifecycle.

## Claude tips
- Provide only relevant scenario to Claude to keep prompts small.
- Remind Claude to seed data via builders from `04-test-data-builders.md`.
