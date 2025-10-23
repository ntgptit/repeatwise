# Test Coverage Requirements (Claude Prompt)

## Targets
- Backend: ≥80% line + branch coverage for core modules (`service`, `controller`, `srs`).
- Frontend web: ≥70% line coverage for feature folders; key components require interaction tests.
- Mobile: ≥60% coverage initially; focus on navigation + review flows.

## Enforcement
- CI fails if thresholds unmet (see GitHub Actions summary in deployment prompts).
- Coverage reports stored in `/coverage` artifacts.

## Claude tips
- Encourage Claude to focus tests on business-critical paths first (auth, SRS, import/export).
