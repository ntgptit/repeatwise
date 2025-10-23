# Mocking Strategies (Claude Prompt)

## Backend
- Use Mockito with strict stubs; prefer constructor injection.
- Mock external services (email/push notifications) while keeping domain services real.
- For async jobs, stub job repository + executor to control completion.

## Frontend
- Use MSW to intercept network requests; define handlers per endpoint.
- For push notifications, mock Expo modules with deterministic responses.

## Claude tips
- Avoid mocking what you don’t own (e.g., don’t mock JPA entities). Focus on boundaries.
