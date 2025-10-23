# Unit Testing Guide (Claude Prompt)

**Scope**: Services, utilities, React hooks/components without network.

## Expectations
- Cover happy path + edge cases per business rules.
- Mock repositories/HTTP clients; assert error codes/messages.
- Follow naming conventions: `should<action>When<condition>`.
- Target coverage: ≥80% for backend service packages, ≥70% for frontend modules.

## Claude tips
- Reference `04-detail-design/03-business-logic-flows.md` for expected outcomes.
- Encourage snapshot tests only for stable UI components; avoid for dynamic lists.
