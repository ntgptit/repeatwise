# E2E Testing Guide (Claude Prompt)

**Tools**: Playwright (web), Detox (mobile optional).

## Critical journeys
1. Register → login → complete onboarding.
2. Create folder → deck → add card → perform SRS review.
3. Update settings (SRS + notifications) and verify reminders scheduled.
4. Import sample file → verify cards appear.

## Tips
- Use seeded test user; clean up via API calls after run.
- Capture screenshots on failure.
- Keep scenario scripts under 200 steps to avoid flakiness.
